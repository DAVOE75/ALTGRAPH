package com.example.altgraph

import android.content.Context
import android.util.Log
import io.hammerhead.karooext.KarooSystemService
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
 * Sustituye los ids de consumer en cada reconexión: primero quita todos los
 * anteriores y luego da de alta uno por cada `kind`.
 */
internal class ConsumerRegistry(
    private val add: (String) -> String,
    private val remove: (String) -> Unit
) {
    private val ids = mutableListOf<String>()

    fun register(kinds: List<String>) {
        ids.forEach(remove)
        ids.clear()
        kinds.forEach { ids.add(add(it)) }
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

    // Se crean en start() y se anulan en stop(); protegidos por el monitor del objeto
    @Volatile private var repoDispatcher: ExecutorCoroutineDispatcher? = null
    @Volatile private var scope: CoroutineScope? = null
    private var karooSystem: KarooSystemService? = null
    private var tickJob: Job? = null
    @Volatile private var appContext: Context? = null

    @Synchronized
    fun start(context: Context) {
        if (scope != null) return
        Log.i(TAG, "AltgraphRepository start")
        val ctx = context.applicationContext ?: context
        appContext = ctx
        val dispatcher = Executors.newSingleThreadExecutor { Thread(it, "altgraph-repo") }.asCoroutineDispatcher()
        val s = CoroutineScope(SupervisorJob() + dispatcher + CoroutineExceptionHandler { _, e -> Log.e(TAG, "repo", e) })
        repoDispatcher = dispatcher
        scope = s

        val system = KarooSystemService(ctx)
        karooSystem = system
        val registry = ConsumerRegistry(
            add = { kind -> addConsumer(system, s, kind) },
            remove = { id -> system.removeConsumer(id) }
        )
        val kinds = listOf(KIND_NAV, KIND_LOC, KIND_ZOOM) + NavigationSync.STREAM_TYPES
        // connect vuelve a llamar al callback en cada reconexión: se re-registra sin duplicar
        system.connect { connected ->
            if (connected) {
                registry.register(kinds)
                Log.i(TAG, "consumers registrados: ${kinds.size}")
            }
        }

        // Holders que siguieran activos de antes de un stop()
        if (gate.isActive) tickJob = launchTick(s)
    }

    // ponytail: close() no espera; un tick en curso puede solaparse con un start() rápido en el mismo
    // proceso (ventana de un tick). Mejora: crear el ejecutor una sola vez y no cerrarlo nunca.
    @Synchronized
    fun stop() {
        val s = scope ?: return
        karooSystem?.disconnect()
        karooSystem = null
        tickJob = null
        s.cancel()
        repoDispatcher?.close()
        repoDispatcher = null
        scope = null
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

    fun addNavListener(l: (OnNavigationState) -> Unit) {
        navListeners.add(l)
    }

    fun removeNavListener(l: (OnNavigationState) -> Unit) {
        navListeners.remove(l)
    }

    private fun dispatcher(): ExecutorCoroutineDispatcher =
        repoDispatcher ?: throw CancellationException("AltgraphRepository detenido")

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
                navListeners.forEach { l ->
                    try {
                        l(event)
                    } catch (e: Exception) {
                        Log.e(TAG, "navListener", e)
                    }
                }
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
                s.launch { NavigationSync.applyStream(core.calculator, kind, values) }
            }
        }
    }
}
