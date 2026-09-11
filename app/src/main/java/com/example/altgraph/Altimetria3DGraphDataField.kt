package com.example.altgraph

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
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
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Altimetria3DGraphDataType(extension: String) : DataTypeImpl(extension, "altimetria_3d") {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var streamJob: Job? = null
    private val calculator = AltimetriaStrategyCalculator()
    private var karooSystem: KarooSystemService? = null
    private var zoomReceiver: BroadcastReceiver? = null

    companion object {
        const val ACTION_CYCLE_3D_ZOOM = "com.example.altgraph.ACTION_CYCLE_3D_ZOOM"
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
                            else -> 200
                        }
                        prefs.lookaheadMeters3d = nextVal
                    }
                }
            }
            val filter = IntentFilter(ACTION_CYCLE_3D_ZOOM)
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
                    system.addConsumer<OnNavigationState> { navEvent ->
                        val state = navEvent.state
                        if (state is OnNavigationState.NavigationState.NavigatingRoute) {
                            calculator.setRouteFromPolyline(state.routePolyline)
                        } else {
                            calculator.clearRoute()
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

        val viewJob = scope.launch {
            while (true) {
                val prefs = AppPreferences.getInstance(context)
                val strategy = calculator.calculateStrategy(context)
                altimetria3DView.update3DData(
                    blocks = strategy.nextBlocks,
                    elevation = calculator.currentElevation,
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
                    showZoomControls = prefs.show3dZoomControls
                )

                val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                altimetria3DView.draw(canvas)

                val remoteViews = RemoteViews(context.packageName, R.layout.view_remote_graphic)
                remoteViews.setImageViewBitmap(R.id.img_graphic, bitmap)

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

                emitter.updateView(remoteViews)

                delay(1000)
            }
        }

        emitter.setCancellable {
            viewJob.cancel()
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