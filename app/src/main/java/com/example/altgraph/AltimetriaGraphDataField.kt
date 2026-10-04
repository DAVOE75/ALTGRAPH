package com.example.altgraph

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.RemoteViews
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.OnLocationChanged
import io.hammerhead.karooext.models.OnNavigationState
import io.hammerhead.karooext.models.OnStreamState
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AltimetriaGraphDataType(extension: String) : DataTypeImpl(extension, "altimetria_graph") {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var streamJob: Job? = null
    private val calculator = AltimetriaStrategyCalculator()
    // calculateStrategy() acumula distancia en cada llamada: el stream reutiliza el
    // resultado reciente de la vista en vez de calcularlo una segunda vez por segundo
    private var lastStrategy: StrategyData? = null
    private var lastStrategyAt = 0L
    // Conexión compartida entre las vistas abiertas de este campo: antes el cancel de
    // una vista la desconectaba aunque otra vista siguiera usándola
    private val systemRef = RefCounted<KarooSystemService> { it.disconnect() }

    override fun startStream(emitter: Emitter<StreamState>) {
        streamJob = scope.launch {
            while (true) {
                val fresh = System.currentTimeMillis() - lastStrategyAt < 2500
                val strategy = lastStrategy?.takeIf { fresh } ?: calculator.calculateStrategy()
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(
                                DataType.Field.SINGLE to strategy.avgGrade,
                                "remaining_distance" to strategy.remainingDistance,
                                "time_to_summit" to strategy.timeToSummit.toDouble(),
                                "total_fatigue_grade" to strategy.totalFatigueGrade.toDouble()
                            )
                        )
                    )
                )
                delay(1000)
            }
        }
        emitter.setCancellable {
            streamJob?.cancel()
        }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        emitter.onNext(UpdateGraphicConfig(showHeader = false))

        systemRef.acquire(emitter) {
            val system = KarooSystemService(context)
            system.connect { connected ->
                if (connected) {
                    // Consumidor 1: Polilínea e Itinerario de Ruta Precargada (GPX / FIT)
                    system.addConsumer<OnNavigationState> { navEvent ->
                        val state = navEvent.state
                        
                        if (state is OnNavigationState.NavigationState.NavigatingRoute) {
                            calculator.isNavigatingRoute = true
                            calculator.syncRouteDistance(state.routeDistance)
                            calculator.setRouteFromPolyline(state.routePolyline)
                            
                            var elevPoly = state.routeElevationPolyline
                            if (elevPoly.isNullOrEmpty()) {
                                elevPoly = (state.javaClass.methods.find { it.name == "getElevationPolyline" }?.invoke(state) as? String)
                            }
                            calculator.setRouteElevationProfile(elevPoly)
                            
                            calculator.setRoutePois(state.pois)
                            
                            val routeKey = "route:${state.name}"
                            val routeClimbs = state.climbs.map { climb ->
                                RouteClimb(
                                    startDistance = climb.startDistance,
                                    endDistance = climb.startDistance + climb.length,
                                    length = climb.length,
                                    totalElevation = climb.totalElevation,
                                    avgGrade = climb.grade
                                )
                            }
                            calculator.syncRouteClimbs(routeKey, routeClimbs)
                            
                        } else if (state is OnNavigationState.NavigationState.NavigatingToDestination) {
                            val dist = (state.javaClass.methods.find { it.name == "getDestinationDistance" || it.name == "getDistance" }?.invoke(state) as? Double) ?: 0.0
                            calculator.isNavigatingRoute = true
                            calculator.setRouteElevationProfile(state.elevationPolyline)
                            calculator.syncRouteDistance(dist)
                            
                        } else {
                            if (state.javaClass.simpleName == "Idle") {
                                calculator.clearRoute()
                            }
                        }
                    }

                    // Consumidor 2: Velocidad Instantánea en tiempo real (m/s)
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.SPEED)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val spd = streamState.dataPoint.values[DataType.Field.SPEED]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            calculator.currentSpeed = spd
                        }
                    }

                    // Consumidor 3: Altitud Barométrica Instantánea en tiempo real (metros)
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.PRESSURE_ELEVATION_CORRECTION)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val elev = streamState.dataPoint.values[DataType.Field.PRESSURE_ELEVATION]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            calculator.updateLiveElevation(elev)
                        }
                    }

                    // Consumidor 4: Pendiente Oficial Instantánea (%)
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.ELEVATION_GRADE)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val grade = streamState.dataPoint.values[DataType.Field.ELEVATION_GRADE]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            calculator.updateLiveGrade(grade)
                        }
                    }

                    // Consumidor 5: Posición GPS en tiempo real (Latitud / Longitud)
                    system.addConsumer<OnLocationChanged> { locEvent ->
                        calculator.updateCurrentLocation(locEvent.lat, locEvent.lng)
                    }

                    // ── Consumidores 6-10: Datos del Climber nativo de Karoo ────────────────
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.DISTANCE_TO_TOP)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val d = streamState.dataPoint.values[DataType.Field.DISTANCE_TO_TOP]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                            calculator.updateClimbData(distToTop = d)
                        }
                    }
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.ELEVATION_TO_TOP)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val e = streamState.dataPoint.values[DataType.Field.ELEVATION_TO_TOP]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                            calculator.updateClimbData(elevToTop = e)
                        }
                    }
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.DISTANCE_FROM_BOTTOM)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val d = streamState.dataPoint.values[DataType.Field.DISTANCE_TO_TOP]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                            calculator.updateClimbData(distFromBottom = d)
                        }
                    }
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.ELEVATION_FROM_BOTTOM)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val e = streamState.dataPoint.values[DataType.Field.ELEVATION_TO_TOP]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                            calculator.updateClimbData(elevFromBottom = e)
                        }
                    }
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.ELEVATION_REMAINING)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val e = streamState.dataPoint.values[DataType.Field.ELEVATION_REMAINING]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                            calculator.updateClimbData(elevRemaining = e)
                        }
                    }
                }
            }
            system
        }

        val altimetriaView = AltimetriaView(context)
        val w = if (config.viewSize.first > 0) config.viewSize.first else 480
        val h = if (config.viewSize.second > 0) config.viewSize.second else 240

        altimetriaView.measure(
            View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
        )
        altimetriaView.layout(0, 0, w, h)

        // Un único bitmap por vista: updateView es síncrono, así que se puede reutilizar
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        var lastFrame: List<Any?>? = null

        val viewJob = scope.launch {
            while (true) {
                val prefs = AppPreferences.getInstance(context)
                val strategy = calculator.calculateStrategy(context)
                lastStrategy = strategy
                lastStrategyAt = System.currentTimeMillis()
                val zoneColor = calculator.getZoneColor(calculator.currentElevation)

                // Nada ha cambiado (parado, sin ruta...): no redibujar ni reenviar el bitmap
                val frame = listOf(strategy, zoneColor, prefs.showBlockPercentages, prefs.visibleBlocksCount)
                if (frame == lastFrame) {
                    delay(1000)
                    continue
                }
                lastFrame = frame

                altimetriaView.updateStrategyData(
                    remainingDistance = strategy.remainingDistance,
                    timeToSummit = strategy.timeToSummit,
                    avgGrade = strategy.visibleAvgGrade,
                    currentZoneColor = zoneColor,
                    nextBlocks = strategy.nextBlocks,
                    attackAlert = strategy.attackAlert,
                    blockSizeMeters = strategy.blockSizeMeters,
                    showBlockPct = prefs.showBlockPercentages,
                    visibleBlocksCount = prefs.visibleBlocksCount
                )

                bitmap.eraseColor(android.graphics.Color.TRANSPARENT)
                altimetriaView.draw(canvas)

                val remoteViews = RemoteViews(context.packageName, R.layout.view_remote_graphic)
                remoteViews.setImageViewBitmap(R.id.img_graphic, bitmap)

                emitter.updateView(remoteViews)

                delay(1000)
            }
        }

        emitter.setCancellable {
            viewJob.cancel()
            systemRef.release(emitter)
        }
    }
}