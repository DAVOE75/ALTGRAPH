package com.example.altgraph

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.util.Log
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

private const val TAG = "ALTGRAPH"

class Altimetria3DGraphDataType(extension: String) : DataTypeImpl(extension, "altimetria_3d") {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var streamJob: Job? = null
    private val calculator = AltimetriaStrategyCalculator()
    private var karooSystem: KarooSystemService? = null
    private var zoomReceiver: BroadcastReceiver? = null

    companion object {
        const val ACTION_CYCLE_3D_ZOOM = "com.example.altgraph.ACTION_CYCLE_3D_ZOOM"
        const val ACTION_TOGGLE_COMPASS = "com.example.altgraph.ACTION_TOGGLE_COMPASS"
    }

    override fun startStream(emitter: Emitter<StreamState>) {
        streamJob = scope.launch {
            while (true) {
                val strategy = calculator.calculateStrategy()
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

        if (zoomReceiver == null) {
            zoomReceiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    if (intent?.action == ACTION_CYCLE_3D_ZOOM && ctx != null) {
                        val prefs = AppPreferences.getInstance(ctx)
                        val curr = prefs.lookaheadMeters3d
                        val nextVal = when {
                            curr < 350 -> 350
                            curr < 500 -> 500
                            curr < 1000 -> 1000
                            curr < 2000 -> 2000
                            curr < 5000 -> 5000
                            curr < 10000 -> 10000
                            curr < 20000 -> 20000
                            curr < 50000 -> 50000
                            curr < 100000 -> 100000
                            curr < 150000 -> 150000
                            curr < 200000 -> 200000
                            else -> 200
                        }
                        prefs.lookaheadMeters3d = nextVal
                    } else if (intent?.action == ACTION_TOGGLE_COMPASS && ctx != null) {
                        val prefs = AppPreferences.getInstance(ctx)
                        prefs.routeMapHeadingUp = !prefs.routeMapHeadingUp
                    }
                }
            }
            val filter = IntentFilter().apply {
                addAction(ACTION_CYCLE_3D_ZOOM)
                addAction(ACTION_TOGGLE_COMPASS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.applicationContext.registerReceiver(zoomReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                @Suppress("UnspecifiedRegisterReceiverFlag")
                context.applicationContext.registerReceiver(zoomReceiver, filter)
            }
        }

        if (karooSystem == null) {
            val system = KarooSystemService(context)
            system.connect { connected ->
                if (connected) {
                    // Consumidor 1: Polilínea e Itinerario de Ruta (GPX y Rutas Dinámicas)
                    system.addConsumer<OnNavigationState> { navEvent ->
                        val state = navEvent.state
                        
                        if (state is OnNavigationState.NavigationState.NavigatingRoute) {
                            Log.d(TAG, "NAV: ruta='${state.name}' dist=${state.routeDistance}m pois=${state.pois.size}")
                            calculator.isNavigatingRoute = true
                            calculator.activeRouteName = state.name
                            calculator.fallbackRemainingDistance = state.routeDistance
                            calculator.setRouteFromPolyline(state.routePolyline)
                            
                            var elevPoly = state.routeElevationPolyline
                            if (elevPoly.isNullOrEmpty()) {
                                elevPoly = (state.javaClass.methods.find { it.name == "getElevationPolyline" }?.invoke(state) as? String)
                            }
                            calculator.setRouteElevationProfile(elevPoly)
                            
                            calculator.setRoutePois(state.pois)
                            
                            // Sincronizar lista de puertos (Mountain Gates)
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
                            
                            // Usar routeDistance como fallback si no tenemos puntos GPS
                            calculator.fallbackRemainingDistance = state.routeDistance
                            
                        } else if (state is OnNavigationState.NavigationState.NavigatingToDestination) {
                            val dist = (state.javaClass.methods.find { it.name == "getDestinationDistance" || it.name == "getDistance" }?.invoke(state) as? Double) ?: 0.0
                            Log.d(TAG, "NAV: Destino dinámico detectado, dist=${dist}m")
                            calculator.isNavigatingRoute = true
                            // Rutas a destino no suelen tener routePolyline, pero sí elevationPolyline
                            calculator.setRouteElevationProfile(state.elevationPolyline)
                            calculator.fallbackRemainingDistance = dist
                            
                        } else {
                            if (state.javaClass.simpleName == "Idle") {
                                Log.d(TAG, "NAV: Idle (sin ruta cargada)")
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
                            Log.d(TAG, "BARO_ELEV: ${elev}m")
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
                            Log.d(TAG, "GRADE: ${grade}%")
                            calculator.updateLiveGrade(grade)
                        }
                    }

                    // Consumidor 5: Posición GPS en tiempo real (Latitud / Longitud)
                    system.addConsumer<OnLocationChanged> { locEvent ->
                        calculator.updateCurrentLocation(locEvent.lat, locEvent.lng)
                    }

                    // ── Consumidores 6-10: Datos del Climber nativo de Karoo ──────────────────
                    // El SDK calcula estos valores directamente desde el archivo GPX/FIT de la ruta.
                    // Son los mismos datos que usa el módulo Climber de Hammerhead internamente.
                    // Permiten que ALTGRAPH construya un perfil 3D con la altimetría REAL del climb.

                    // Consumidor 6: Distancia al Cima del climb actual
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.DISTANCE_TO_TOP)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val d = streamState.dataPoint.values[DataType.Field.DISTANCE_TO_TOP]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            Log.d(TAG, "CLIMBER distToTop=${d}m elev=${calculator.currentElevation}m")
                            calculator.updateClimbData(distToTop = d)
                        }
                    }

                    // Consumidor 7: Desnivel hasta el Cima del climb actual
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.ELEVATION_TO_TOP)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val e = streamState.dataPoint.values[DataType.Field.ELEVATION_TO_TOP]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            Log.d(TAG, "CLIMBER elevToTop=${e}m")
                            calculator.updateClimbData(elevToTop = e)
                        }
                    }

                    // Consumidor 8: Distancia desde la Base del climb actual
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.DISTANCE_FROM_BOTTOM)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val d = streamState.dataPoint.values[DataType.Field.DISTANCE_TO_TOP]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            calculator.updateClimbData(distFromBottom = d)
                        }
                    }

                    // Consumidor 9: Desnivel desde la Base del climb actual
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.ELEVATION_FROM_BOTTOM)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val e = streamState.dataPoint.values[DataType.Field.ELEVATION_TO_TOP]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            calculator.updateClimbData(elevFromBottom = e)
                        }
                    }

                    // Consumidor 10: Desnivel restante total de la ruta
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.ELEVATION_REMAINING)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val e = streamState.dataPoint.values[DataType.Field.ELEVATION_REMAINING]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            calculator.updateClimbData(elevRemaining = e)
                        }
                    }
                }
            }
            karooSystem = system
        }

        val altimetria3DView = Altimetria3DView(context)
        val w = if (config.viewSize.first > 0) config.viewSize.first else 480
        val h = if (config.viewSize.second > 0) config.viewSize.second else 240

        altimetria3DView.measure(
            View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
        )
        altimetria3DView.layout(0, 0, w, h)

        var cachedBitmap: Bitmap? = null
        var cachedCanvas: Canvas? = null

        val viewJob = scope.launch {
            while (true) {
                val prefs = AppPreferences.getInstance(context)
                val strategy = calculator.calculateStrategy(context)
                val startElev = if (strategy.windowStartElevation > 0.0) strategy.windowStartElevation else calculator.currentElevation
                altimetria3DView.oasisDistanceToNextCrucible = strategy.oasisDistanceToNextCrucible
                altimetria3DView.virtualPacerRelativeDistance = strategy.virtualPacerRelativeDistance
                altimetria3DView.energyBatteryLevel = strategy.energyBatteryLevel
                
                // ELITE
                altimetria3DView.stravaSegmentDistance = strategy.stravaSegmentDistance
                altimetria3DView.stravaPrGhostDistance = strategy.stravaPrGhostDistance
                altimetria3DView.windEffectIntensity = strategy.windEffectIntensity
                val isHeadingUp = prefs.routeMapHeadingUp
                val mapRotAngle = if (isHeadingUp) {
                    // Si el heading es 90, la cámara apunta al este, así que rotamos -90 para que apunte arriba
                    -calculator.currentHeading 
                } else {
                    0.0 
                }
                
                altimetria3DView.update3DData(
                    blocks = strategy.nextBlocks,
                    elevation = startElev,
                    maxElev = 727.0,
                    grade = strategy.avgGrade,
                    remainingDist = strategy.remainingDistance,
                    blockSizeMeters = strategy.blockSizeMeters,
                    fontScale = prefs.fontSize3dScale,
                    lookaheadMeters = prefs.lookaheadMeters3d,
                    showCotas = prefs.show3dCotas,
                    showRamps = prefs.show3dRamps,
                    showMaxGrade = prefs.showMaxGradient,
                    fontFamilyKey = prefs.fontFamilyKey,
                    rotate90 = prefs.rotate90Clockwise,
                    rampMinSlope = prefs.rampMinSlopePct,
                    rampMaxSlope = prefs.rampMaxSlopePct,
                    showHairpins = prefs.showHairpins,
                    showPois = (prefs.showPoiTowns || prefs.showPoiWater || prefs.showPoiViewpoints || prefs.showPoiSummits),
                    hairpins = strategy.hairpins,
                    pois = strategy.pois,
                    showZoomControls = prefs.show3dZoomControls,
                    curvatureOffsets = strategy.curvatureOffsets,
                    riderProgress = strategy.riderProgress,
                    windowStartMeters = strategy.windowStartMeters,
                    subBlocks = strategy.subBlocks,
                    subBlockSizeMeters = strategy.subBlockSizeMeters,
                    majorBlockSizeMeters = strategy.majorBlockSizeMeters,
                    profileElevations = strategy.profileElevations,
                    showBlockPercentages = prefs.showBlockPercentages,
                    altimetriaStyle = prefs.altimetriaStyle,
                    targetVam = prefs.targetVam,
                    activeClimbs = strategy.activeClimbs,
                    visibleAvgGrade = strategy.visibleAvgGrade,
                    visibleMaxGrade = strategy.visibleMaxGrade,
                    routeName = strategy.routeName,
                    routeCoords = strategy.routeCoords,
                    mapRotationAngle = mapRotAngle
                )

                if (cachedBitmap == null || cachedBitmap?.width != w || cachedBitmap?.height != h) {
                    cachedBitmap?.recycle()
                    val newBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                    cachedBitmap = newBmp
                    cachedCanvas = Canvas(newBmp)
                }

                val currentBmp = cachedBitmap!!
                val currentCanvas = cachedCanvas!!
                altimetria3DView.draw(currentCanvas)

                // --- DRAW COMPASS ---
                if (strategy.routeCoords.isNotEmpty()) {
                    val compassX = w - 40f
                    val compassY = 40f
                    val cx = compassX
                    val cy = compassY
                    
                    val compassPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.WHITE
                        style = android.graphics.Paint.Style.STROKE
                        strokeWidth = 3f
                    }
                    currentCanvas.drawCircle(cx, cy, 20f, compassPaint)
                    
                    val arrowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.RED
                        style = android.graphics.Paint.Style.FILL
                    }
                    val bgArrowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.GRAY
                        style = android.graphics.Paint.Style.FILL
                    }
                    
                    val angleOffset = if (isHeadingUp) -calculator.currentHeading else 0.0
                    
                    currentCanvas.save()
                    currentCanvas.rotate(angleOffset.toFloat(), cx, cy)
                    
                    // Draw north arrow (red)
                    val pathNorth = android.graphics.Path()
                    pathNorth.moveTo(cx, cy - 20f)
                    pathNorth.lineTo(cx + 8f, cy)
                    pathNorth.lineTo(cx - 8f, cy)
                    pathNorth.close()
                    currentCanvas.drawPath(pathNorth, arrowPaint)
                    
                    // Draw south arrow (gray)
                    val pathSouth = android.graphics.Path()
                    pathSouth.moveTo(cx, cy + 20f)
                    pathSouth.lineTo(cx + 8f, cy)
                    pathSouth.lineTo(cx - 8f, cy)
                    pathSouth.close()
                    currentCanvas.drawPath(pathSouth, bgArrowPaint)
                    
                    currentCanvas.restore()
                    
                    // Draw text N
                    val textPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                        color = android.graphics.Color.WHITE
                        textSize = 14f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    
                    if (!isHeadingUp) {
                        currentCanvas.drawText("N", cx, cy - 25f, textPaint)
                    } else {
                        // Letra "R" de Rumbo o "H" de Heading
                        currentCanvas.drawText("H", cx, cy - 25f, textPaint)
                    }
                }

                val remoteViews = RemoteViews(context.packageName, R.layout.view_remote_3d)
                remoteViews.setImageViewBitmap(R.id.img_graphic, currentBmp)

                if (prefs.show3dZoomControls) {
                    val intent = Intent(ACTION_CYCLE_3D_ZOOM).apply {
                        setPackage(context.packageName)
                    }
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    remoteViews.setOnClickPendingIntent(R.id.img_graphic, pendingIntent)
                }
                
                val intentCompass = Intent(ACTION_TOGGLE_COMPASS).apply {
                    setPackage(context.packageName)
                }
                val pendingIntentCompass = PendingIntent.getBroadcast(
                    context,
                    1,
                    intentCompass,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                remoteViews.setOnClickPendingIntent(R.id.btn_toggle_compass, pendingIntentCompass)

                emitter.updateView(remoteViews)

                delay(1000)
            }
        }

        emitter.setCancellable {
            viewJob.cancel()
            cachedBitmap?.recycle()
            cachedBitmap = null
            cachedCanvas = null
            zoomReceiver?.let {
                try {
                    context.applicationContext.unregisterReceiver(it)
                } catch (e: Exception) {}
            }
            zoomReceiver = null
            karooSystem?.disconnect()
            karooSystem = null
        }
    }
}