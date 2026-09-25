package com.example.altgraph

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

class RouteBarDataType(extension: String) : DataTypeImpl(extension, "route_bar") {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var streamJob: Job? = null
    private var viewJob: Job? = null
    private val calculator = AltimetriaStrategyCalculator()
    private var karooSystem: KarooSystemService? = null

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
                                "remaining_distance" to strategy.remainingDistance
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

        if (karooSystem == null) {
            val system = KarooSystemService(context)
            system.connect { connected ->
                if (connected) {
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

                    system.addConsumer<OnLocationChanged> { locEvent ->
                        calculator.updateCurrentLocation(locEvent.lat, locEvent.lng)
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

        val routeBarView = RouteBarView(context)
        // If it's a very tall and thin view, it's likely on the side. 


        val w = if (config.viewSize.first > 0) config.viewSize.first else 120
        val h = if (config.viewSize.second > 0) config.viewSize.second else 600

        routeBarView.measure(
            View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
        )
        routeBarView.layout(0, 0, w, h)

        var cachedBitmap: Bitmap? = null
        var cachedCanvas: Canvas? = null
        val bgPaint = Paint().apply { color = Color.TRANSPARENT }

        viewJob = scope.launch {
            while (true) {
                if (cachedBitmap == null || cachedBitmap!!.width != w || cachedBitmap!!.height != h) {
                    cachedBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                    cachedCanvas = Canvas(cachedBitmap!!)
                }

                val currentBmp = cachedBitmap!!
                val currentCanvas = cachedCanvas!!
                
                // Clear the canvas with transparent background so map shows through
                currentCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
                
                val isElite = AppPreferences.getInstance(context).eliteRadarEnabled
                
                if (isElite) {
                    val strategy = calculator.calculateStrategy(context)
                    routeBarView.strategyData = strategy
                    routeBarView.draw(currentCanvas)
                } else {
                    val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = Color.RED
                        textSize = 14f
                        textAlign = Paint.Align.CENTER
                    }
                    currentCanvas.drawText("ELITE", w/2f, h/2f, p)
                }

                val remoteViews = RemoteViews(context.packageName, R.layout.view_remote_graphic)
                remoteViews.setImageViewBitmap(R.id.img_graphic, currentBmp)

                emitter.onNext(UpdateGraphicConfig(showHeader = false))
                emitter.updateView(remoteViews)

                delay(1000)
            }
        }

        emitter.setCancellable {
            viewJob?.cancel()
            cachedBitmap?.recycle()
            cachedBitmap = null
            cachedCanvas = null
            karooSystem?.disconnect()
            karooSystem = null
        }
    }
}
