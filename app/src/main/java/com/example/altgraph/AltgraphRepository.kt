package com.example.altgraph

import android.content.Context
import android.util.Log
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.OnLocationChanged
import io.hammerhead.karooext.models.OnMapZoomLevel
import io.hammerhead.karooext.models.OnNavigationState
import io.hammerhead.karooext.models.OnStreamState
import io.hammerhead.karooext.models.StreamState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

/**
 * Consumers de una instancia de KarooSystemService: se dan de alta una sola vez (el SDK los
 * vuelve a registrar él mismo en cada reconexión) y se quitan todos en stop().
 */
internal class ConsumerRegistry(
    private val add: (String) -> String,
    private val remove: (String) -> Unit
) {
    private val ids = mutableListOf<String>()

    fun register(kinds: List<String>) {
        if (ids.isNotEmpty()) return
        kinds.forEach { ids.add(add(it)) }
    }

    fun removeAll() {
        ids.forEach(remove)
        ids.clear()
    }
}

/**
 * Única conexión con Karoo y único hilo del calculador compartido.
 * Todo acceso a `core` ocurre en el hilo "altgraph-repo".
 */
object AltgraphRepository {

    private const val TAG = "AltgraphRepository"
    private const val KIND_NAV = "nav"
    private const val KIND_LOC = "loc"
    private const val KIND_ZOOM = "zoom"

    private val core = AltgraphCore()
    private val gate = TickGate()
    private val navListeners = CopyOnWriteArrayList<(OnNavigationState) -> Unit>()
    private val _snapshot = MutableStateFlow<Snapshot?>(null)
    val snapshot: StateFlow<Snapshot?> = _snapshot

    // Un solo hilo para toda la vida del proceso y nunca se cierra: tras stop()+start() el trabajo
    // que siguiera en curso (tick o evento no suspend) termina antes de que corra nada nuevo sobre core
    private val repoDispatcher: ExecutorCoroutineDispatcher by lazy {
        Executors.newSingleThreadExecutor { Thread(it, "altgraph-repo") }.asCoroutineDispatcher()
    }

    // Se crean en start() y se anulan en stop(); protegidos por el monitor del objeto
    @Volatile private var scope: CoroutineScope? = null
    private var karooSystem: KarooSystemService? = null
    private var consumers: ConsumerRegistry? = null
    private var tickJob: Job? = null
    @Volatile private var appContext: Context? = null
    // Último estado de navegación (solo se escribe en el hilo del repositorio)
    @Volatile private var lastNav: OnNavigationState? = null

    @Synchronized
    fun start(context: Context) {
        if (scope != null) return
        Log.i(TAG, "AltgraphRepository start")
        val ctx = context.applicationContext ?: context
        appContext = ctx
        val s = CoroutineScope(SupervisorJob() + repoDispatcher + CoroutineExceptionHandler { _, e -> Log.e(TAG, "repo", e) })
        scope = s

        val system = KarooSystemService(ctx)
        karooSystem = system
        val registry = ConsumerRegistry(
            add = { kind -> addConsumer(system, s, kind) },
            remove = { id -> system.removeConsumer(id) }
        )
        val kinds = listOf(KIND_NAV, KIND_LOC, KIND_ZOOM) + NavigationSync.STREAM_TYPES
        // Alta única antes de conectar: el SDK registra en cada (re)conexión todos los consumers
        // que tiene. No se tocan desde el callback de connect, que corre dentro de ese recorrido.
        registry.register(kinds)
        consumers = registry
        Log.i(TAG, "consumers registrados: ${kinds.size}")
        system.connect()

        // Holders que siguieran activos de antes de un stop()
        if (gate.isActive) tickJob = launchTick(s)
    }

    // Nota: el ejecutor no se cierra; cancelar el scope basta y un start() rápido reutiliza el mismo hilo
    @Synchronized
    fun stop() {
        val s = scope ?: return
        consumers?.removeAll()
        consumers = null
        karooSystem?.disconnect()
        karooSystem = null
        tickJob = null
        s.cancel()
        scope = null
        // Tras un stop() el core queda obsoleto y Karoo reenvía la navegación al reconectar
        lastNav = null
    }

    // hold/release llegan desde hilos Binder: el monitor ordena launch/cancel igual que el gate
    @Synchronized
    fun hold(token: Any) {
        if (gate.add(token)) tickJob = scope?.let { launchTick(it) }
    }

    @Synchronized
    fun release(token: Any) {
        if (gate.remove(token)) {
            tickJob?.cancel()
            tickJob = null
        }
    }

    suspend fun strategyForRouteBar(viewWidth: Int, base: Snapshot): StrategyData =
        withContext(dispatcher()) { core.strategyForRouteBar(appContext, viewWidth, base) }

    suspend fun strategyFor3D(base: Snapshot): StrategyData =
        withContext(dispatcher()) { core.strategyFor3D(appContext, base) }

    suspend fun climbStrategy(climb: RouteClimb, start: Double?, length: Double?): StrategyData =
        withContext(dispatcher()) { core.climbStrategy(climb, start, length) }

    fun pan3d(deltaMeters: Double) {
        scope?.launch { core.pan3dMeters += deltaMeters }
    }

    // Alta y reenvío en el mismo bloque del hilo del repositorio: un evento ya encolado no llega dos veces
    fun addNavListener(l: (OnNavigationState) -> Unit) {
        val s = scope ?: run { navListeners.add(l); return }
        s.launch {
            navListeners.add(l)
            // Karoo entregó el estado de navegación al conectar, antes de que existiera esta vista: se reenvía el último
            lastNav?.let { deliverNav(l, it) }
        }
    }

    // Baja inmediata (la entrega comprueba pertenencia) y otra en el hilo por si el alta seguía encolada
    fun removeNavListener(l: (OnNavigationState) -> Unit) {
        navListeners.remove(l)
        scope?.launch { navListeners.remove(l) }
    }

    private fun deliverNav(l: (OnNavigationState) -> Unit, event: OnNavigationState) {
        if (l !in navListeners) return
        try {
            l(event)
        } catch (e: Exception) {
            Log.e(TAG, "navListener", e)
        }
    }

    private fun dispatcher(): ExecutorCoroutineDispatcher =
        if (scope != null) repoDispatcher else throw CancellationException("AltgraphRepository detenido")

    private fun launchTick(s: CoroutineScope): Job = s.launch {
        while (isActive) {
            // Un tick que falla se registra y el bucle sigue (tick no es suspend: no traga cancelaciones)
            try {
                _snapshot.value = core.tick(appContext)
            } catch (e: Exception) {
                Log.e(TAG, "tick", e)
            }
            delay(1000)
        }
    }

    // Los callbacks corren en hilos Binder: solo encolan en el hilo del repositorio y vuelven
    private fun addConsumer(system: KarooSystemService, s: CoroutineScope, kind: String): String = when (kind) {
        KIND_NAV -> system.addConsumer<OnNavigationState> { event ->
            s.launch {
                NavigationSync.applyNavigation(core.calculator, event.state)
                lastNav = event
                navListeners.forEach { l -> deliverNav(l, event) }
            }
        }
        KIND_LOC -> system.addConsumer<OnLocationChanged> { event ->
            s.launch { core.calculator.updateCurrentLocation(event.lat, event.lng) }
        }
        KIND_ZOOM -> system.addConsumer<OnMapZoomLevel> { event ->
            s.launch { core.mapZoomLevel = event.zoomLevel }
        }
        else -> system.addConsumer(OnStreamState.StartStreaming(kind)) { event: OnStreamState ->
            val st = event.state
            if (st is StreamState.Streaming) {
                val values = st.dataPoint.values
                s.launch {
                    NavigationSync.applyStream(core.calculator, kind, values)
                    // Mismo valor y fallback que leían ClimbPacing y GradientTrend con su propio consumer
                    if (kind == DataType.Type.ELEVATION_GRADE)
                        core.rawGrade = values[DataType.Field.ELEVATION_GRADE] ?: values[DataType.Field.SINGLE] ?: 0.0
                }
            }
        }
    }
}
