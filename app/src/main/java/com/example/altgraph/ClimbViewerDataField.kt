package com.example.altgraph

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
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
import kotlin.math.roundToInt

private const val TAG = "ALTGRAPH_VIEWER"

class ClimbViewerDataType(extension: String) : DataTypeImpl(extension, "climb_3d") {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var streamJob: Job? = null
    private val calculator by lazy { AltimetriaStrategyCalculator() }
    private var karooSystem: KarooSystemService? = null
    private var interactionReceiver: BroadcastReceiver? = null

    private var currentClimbIndex = 0
    private var currentZoomQuarter: Int = 0

    companion object {
        const val ACTION_CLIMB_NEXT = "com.example.altgraph.ACTION_CLIMB_NEXT"
        const val ACTION_CLIMB_PREV = "com.example.altgraph.ACTION_CLIMB_PREV"
        const val ACTION_ZOOM_IN = "com.example.altgraph.ACTION_ZOOM_IN"
        const val ACTION_ZOOM_OUT = "com.example.altgraph.ACTION_ZOOM_OUT"
    }

    override fun startStream(emitter: Emitter<StreamState>) {
        streamJob = scope.launch {
            while (true) {
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to 0.0)
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

        if (interactionReceiver == null) {
            interactionReceiver = object : BroadcastReceiver() {
                override fun onReceive(ctx: Context?, intent: Intent?) {
                    val climbs = calculator.routeClimbs
                    val currentClimb = if (climbs.isNotEmpty()) climbs[currentClimbIndex % climbs.size] else null

                    when (intent?.action) {
                        ACTION_CLIMB_NEXT -> {
                            if (climbs.isNotEmpty()) {
                                currentClimbIndex = (currentClimbIndex + 1) % climbs.size
                                currentZoomQuarter = 0
                            }
                        }
                        ACTION_CLIMB_PREV -> {
                            if (climbs.isNotEmpty()) {
                                currentClimbIndex = if (currentClimbIndex - 1 < 0) climbs.size - 1 else currentClimbIndex - 1
                                currentZoomQuarter = 0
                            }
                        }
                        ACTION_ZOOM_IN -> {
                            if (currentZoomQuarter < 4) currentZoomQuarter++
                        }
                        ACTION_ZOOM_OUT -> {
                            if (currentZoomQuarter > 0) currentZoomQuarter--
                        }
                    }
                }
            }
            val filter = IntentFilter().apply {
                addAction(ACTION_CLIMB_NEXT)
                addAction(ACTION_CLIMB_PREV)
                addAction(ACTION_ZOOM_IN)
                addAction(ACTION_ZOOM_OUT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.applicationContext.registerReceiver(interactionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                @Suppress("UnspecifiedRegisterReceiverFlag")
                context.applicationContext.registerReceiver(interactionReceiver, filter)
            }
        }

        if (karooSystem == null) {
            val system = KarooSystemService(context)
            system.connect { connected ->
                if (connected) {
                    system.addConsumer<OnNavigationState> { navEvent ->
                        val state = navEvent.state
                        if (state is OnNavigationState.NavigationState.NavigatingRoute) {
                            calculator.isNavigatingRoute = true
                            calculator.activeRouteName = state.name
                            val routeLength = calculator.routePoints.lastOrNull()?.distance ?: (calculator.routeElevationProfile.lastOrNull()?.distance ?: 0.0)
                            if (routeLength > 0.0) {
                                calculator.currentRouteDistance = routeLength - state.routeDistance
                            }
                            calculator.setRouteFromPolyline(state.routePolyline)
                            
                            var elevPoly = state.routeElevationPolyline
                            if (elevPoly.isNullOrEmpty()) {
                                elevPoly = (state.javaClass.methods.find { it.name == "getElevationPolyline" }?.invoke(state) as? String)
                            }
                            calculator.setRouteElevationProfile(elevPoly)
                            
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
                            calculator.isNavigatingRoute = true
                            val dist = (state.javaClass.methods.find { it.name == "getDestinationDistance" || it.name == "getDistance" }?.invoke(state) as? Double) ?: 0.0
                            val routeLength = calculator.routePoints.lastOrNull()?.distance ?: (calculator.routeElevationProfile.lastOrNull()?.distance ?: 0.0)
                            if (routeLength > 0.0) {
                                calculator.currentRouteDistance = routeLength - dist
                            }
                            val elevPoly = state.elevationPolyline
                            calculator.setRouteElevationProfile(elevPoly)
                        } else if (state.javaClass.simpleName == "Idle") {
                            calculator.clearRoute()
                            currentClimbIndex = 0
                        }
                    }
                    
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.ELEVATION_GRADE)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val grade = streamState.dataPoint.values[DataType.Field.ELEVATION_GRADE] as? Double 
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE] as? Double 
                                ?: 0.0
                            calculator.updateLiveGrade(grade)
                        }
                    }
                }
            }
            karooSystem = system
        }

        val altimetria3DView = Altimetria3DView(context).apply {
            isClimbMode = true
        }
        val w = if (config.viewSize.first > 0) config.viewSize.first else 480
        val h = if (config.viewSize.second > 0) config.viewSize.second else 240

        altimetria3DView.measure(
            View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
        )
        altimetria3DView.layout(0, 0, w, h)

        var cachedBitmap: Bitmap? = null
        var cachedCanvas: Canvas? = null

        val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            setShadowLayer(4f, 0f, 2f, Color.BLACK)
        }
        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E2E8F0")
            textSize = 18f
            textAlign = Paint.Align.CENTER
            setShadowLayer(3f, 0f, 1f, Color.BLACK)
        }
        val bgPaint = Paint().apply { color = Color.BLACK }

        val viewJob = scope.launch {
            while (true) {
                if (cachedBitmap == null || cachedBitmap!!.width != w || cachedBitmap!!.height != h) {
                    cachedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                    cachedCanvas = Canvas(cachedBitmap!!)
                }

                val currentBmp = cachedBitmap!!
                val currentCanvas = cachedCanvas!!
                currentCanvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

                val climbs = calculator.routeClimbs
                if (climbs.isEmpty()) {
                    val isRotated = AppPreferences.getInstance(context).climbRotate90Clockwise
                    var drawW = w.toFloat()
                    var drawH = h.toFloat()
                    if (isRotated) {
                        currentCanvas.save()
                        currentCanvas.translate(w.toFloat(), 0f)
                        currentCanvas.rotate(90f)
                        drawW = h.toFloat()
                        drawH = w.toFloat()
                    }
                    currentCanvas.drawText("Sin puertos detectados / No climbs", drawW / 2f, drawH / 2f, overlayPaint)
                    if (isRotated) {
                        currentCanvas.restore()
                    }
                } else {
                    if (currentClimbIndex >= climbs.size) currentClimbIndex = 0
                    val climb = climbs[currentClimbIndex]
                    
                    val quarterLength = climb.length / 4.0
                    val customStartDist = if (currentZoomQuarter > 0) climb.startDistance + (currentZoomQuarter - 1) * quarterLength else climb.startDistance
                    val customLength = if (currentZoomQuarter > 0) quarterLength else climb.length
                    
                    val strategy = calculator.getStrategyDataForClimb(climb, customStartDist, customLength)
                    val prefs = AppPreferences.getInstance(context)
                    
                    val lookahead = customLength.toInt().coerceAtLeast(100)
                    
                    // Calculamos progreso del ciclista
                    val riderProgress = if (calculator.currentRouteDistance >= customStartDist && calculator.currentRouteDistance <= customStartDist + customLength) {
                        ((calculator.currentRouteDistance - customStartDist) / customLength).toFloat()
                    } else {
                        -1f // -1f ocultará la baliza en la gráfica
                    }
                    
                    altimetria3DView.oasisDistanceToNextCrucible = strategy.oasisDistanceToNextCrucible
                altimetria3DView.virtualPacerRelativeDistance = strategy.virtualPacerRelativeDistance
                altimetria3DView.energyBatteryLevel = strategy.energyBatteryLevel
                
                // ELITE
                altimetria3DView.stravaSegmentDistance = strategy.stravaSegmentDistance
                altimetria3DView.stravaPrGhostDistance = strategy.stravaPrGhostDistance
                altimetria3DView.windEffectIntensity = strategy.windEffectIntensity
                altimetria3DView.update3DData(
                        blocks = strategy.nextBlocks,
                        elevation = strategy.windowStartElevation,
                        maxElev = 0.0, // Let view calculate it
                        grade = strategy.avgGrade,
                        remainingDist = strategy.remainingDistance,
                        blockSizeMeters = strategy.blockSizeMeters,
                        fontScale = prefs.fontSize3dScale,
                        lookaheadMeters = lookahead,
                        showCotas = prefs.show3dCotas,
                        showRamps = prefs.show3dRamps,
                        showMaxGrade = prefs.showMaxGradient,
                        fontFamilyKey = prefs.fontFamilyKey,
                        rotate90 = prefs.climbRotate90Clockwise,
                        rotateMinus90 = false,
                        rampMinSlope = prefs.rampMinSlopePct,
                        rampMaxSlope = prefs.rampMaxSlopePct,
                        showHairpins = false,
                        showPois = false,
                        hairpins = emptyList(),
                        pois = emptyList(),
                        showZoomControls = false, // We use screen tap to cycle climbs
                        curvatureOffsets = emptyList(),
                        riderProgress = riderProgress,
                        windowStartMeters = strategy.windowStartMeters,
                        subBlocks = strategy.subBlocks,
                        subBlockSizeMeters = strategy.subBlockSizeMeters,
                        majorBlockSizeMeters = strategy.majorBlockSizeMeters,
                        profileElevations = strategy.profileElevations,
                        showBlockPercentages = prefs.showBlockPercentages,
                        altimetriaStyle = prefs.climbAltimetriaStyle,
                        targetVam = prefs.targetVam,
                        activeClimbs = emptyList(),
                        visibleAvgGrade = strategy.visibleAvgGrade,
                        visibleMaxGrade = strategy.visibleMaxGrade,
                        routeName = "",
                        customTitle = "",
                        showHeaderStats = false,
                        routeCoords = strategy.routeCoords
                    )
                    
                    altimetria3DView.draw(currentCanvas)
                    

                    // Draw Header Overlay
                    val isRotated = prefs.climbRotate90Clockwise
                    var drawW = w.toFloat()
                    var drawH = h.toFloat()

                    if (isRotated) {
                        currentCanvas.save()
                        currentCanvas.translate(w.toFloat(), 0f)
                        currentCanvas.rotate(90f)
                        drawW = h.toFloat()
                        drawH = w.toFloat()
                    }
                    overlayPaint.textSize = (drawH * 0.12f).coerceIn(26f, 42f)
                    FontHelper.applyFontToPaint(overlayPaint, prefs.fontFamilyKey)
                    
                    subPaint.textSize = (drawH * 0.06f).coerceIn(16f, 24f)
                    subPaint.textAlign = Paint.Align.LEFT
                    FontHelper.applyFontToPaint(subPaint, prefs.fontFamilyKey)
                    
                    val maxGradePaint = Paint(subPaint).apply {
                        color = Color.parseColor("#EF4444") // Red
                    }
                    FontHelper.applyFontToPaint(maxGradePaint, prefs.fontFamilyKey)
                    
                    val climbTitle = "◀   Puerto ${currentClimbIndex + 1}/${climbs.size} - ${climb.category}   ▶"
                    currentCanvas.drawText(climbTitle, drawW / 2f, drawH * 0.10f, overlayPaint)
                    
                    // Line 1: Length | Elevation | Status
                    val distStr = if (calculator.isNavigatingRoute) {
                        val distanceToStart = climb.startDistance - calculator.currentRouteDistance
                        val km = (Math.abs(distanceToStart) / 1000).toInt()
                        val m = (Math.abs(distanceToStart) % 1000).toInt()
                        if (distanceToStart > 0) {
                            if (km > 0) " | Faltan ${km}km ${m}m" else " | Faltan ${m}m"
                        } else if (distanceToStart <= 0 && calculator.currentRouteDistance < climb.endDistance) {
                            " | En puerto"
                        } else {
                            " | Superado"
                        }
                    } else ""
                    
                    val line1 = "${String.format("%.1f", climb.length / 1000f)}km | +${climb.totalElevation.toInt()}m$distStr"
                    
                    // Line 2: Pend. Actual | P.Med | P.Max
                    val liveGrade = calculator.instantBarometricGrade
                    val maxG = if (strategy.visibleMaxGrade > 0) strategy.visibleMaxGrade else climb.avgGrade
                    val line2Part1 = "Pend. Actual: ${String.format("%.1f", liveGrade)}%  |  P.Med: ${String.format("%.1f", climb.avgGrade)}%  |  P.Max: "
                    val line2Part2 = "${String.format("%.1f", maxG)}%"
                    
                    val wLine2P1 = subPaint.measureText(line2Part1)
                    val wLine2P2 = maxGradePaint.measureText(line2Part2)
                    
                    val statsY1 = drawH * 0.18f
                    val statsY2 = statsY1 + 40f
                    
                    subPaint.textAlign = Paint.Align.CENTER
                    currentCanvas.drawText(line1, drawW / 2f, statsY1, subPaint)
                    
                    subPaint.textAlign = Paint.Align.LEFT
                    val line2TotalW = wLine2P1 + wLine2P2
                    var currentX = (drawW / 2f) - (line2TotalW / 2f)
                    currentCanvas.drawText(line2Part1, currentX, statsY2, subPaint)
                    currentX += wLine2P1
                    currentCanvas.drawText(line2Part2, currentX, statsY2, maxGradePaint)
                    if (isRotated) {
                        currentCanvas.restore()
                    }
                }

                val isRotatedOuter = AppPreferences.getInstance(context).climbRotate90Clockwise
                val layoutRes = if (isRotatedOuter) R.layout.view_remote_climb_land else R.layout.view_remote_climb
                val remoteViews = RemoteViews(context.packageName, layoutRes)
                remoteViews.setImageViewBitmap(R.id.img_graphic, currentBmp)


                // Navigation Intents
                val intentNext = Intent(ACTION_CLIMB_NEXT).apply { setPackage(context.packageName) }
                val pIntentNext = PendingIntent.getBroadcast(context, 0, intentNext, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                remoteViews.setOnClickPendingIntent(R.id.btn_next, pIntentNext)
                
                val intentPrev = Intent(ACTION_CLIMB_PREV).apply { setPackage(context.packageName) }
                val pIntentPrev = PendingIntent.getBroadcast(context, 3, intentPrev, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                remoteViews.setOnClickPendingIntent(R.id.btn_prev, pIntentPrev)
                
                // Zoom Intents
                val intentZIn = Intent(ACTION_ZOOM_IN).apply { setPackage(context.packageName) }
                val pIntentZIn = PendingIntent.getBroadcast(context, 1, intentZIn, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                remoteViews.setOnClickPendingIntent(R.id.btn_zoom_in, pIntentZIn)
                
                val btnText = if (currentZoomQuarter > 0) "+$currentZoomQuarter" else "+"
                remoteViews.setTextViewText(R.id.btn_zoom_in, btnText)
                
                val intentZOut = Intent(ACTION_ZOOM_OUT).apply { setPackage(context.packageName) }
                val pIntentZOut = PendingIntent.getBroadcast(context, 2, intentZOut, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                remoteViews.setOnClickPendingIntent(R.id.btn_zoom_out, pIntentZOut)

                emitter.onNext(UpdateGraphicConfig(showHeader = false))
                emitter.updateView(remoteViews)

                delay(1000)
            }
        }

        emitter.setCancellable {
            viewJob.cancel()
            cachedBitmap?.recycle()
            cachedBitmap = null
            cachedCanvas = null
            karooSystem?.disconnect()
            karooSystem = null
            interactionReceiver?.let {
                context.applicationContext.unregisterReceiver(it)
                interactionReceiver = null
            }
        }
    }
}
