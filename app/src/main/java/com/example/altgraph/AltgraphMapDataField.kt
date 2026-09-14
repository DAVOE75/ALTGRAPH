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

class AltgraphMapDataField(extension: String) : DataTypeImpl(extension, "altgraph_map") {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var streamJob: Job? = null
    private val calculator = AltimetriaStrategyCalculator()
    private var karooSystem: KarooSystemService? = null

    override fun startStream(emitter: Emitter<StreamState>) {
        streamJob = scope.launch {
            while (true) {
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(
                                DataType.Field.SINGLE to calculator.currentElevation,
                                "speed_mps" to calculator.currentSpeed,
                                "latitude" to calculator.currentLatitude,
                                "longitude" to calculator.currentLongitude
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
                            calculator.setRouteFromPolyline(state.routePolyline)
                        } else {
                            calculator.clearRoute()
                        }
                    }

                    system.addConsumer(OnStreamState.StartStreaming("SPEED")) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val spd = streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                            calculator.currentSpeed = spd
                        }
                    }

                    system.addConsumer(OnStreamState.StartStreaming("ELEVATION")) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val elev = streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                            calculator.updateLiveElevation(elev)
                        }
                    }

                    system.addConsumer(OnStreamState.StartStreaming("POSITION")) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val lat = streamState.dataPoint.values["latitude"] ?: streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                            val lng = streamState.dataPoint.values["longitude"] ?: 0.0
                            calculator.updateCurrentLocation(lat, lng)
                        }
                    }
                }
            }
            karooSystem = system
        }

        val mapView = AltgraphMapView(context)
        val w = if (config.viewSize.first > 0) config.viewSize.first else 480
        val h = if (config.viewSize.second > 0) config.viewSize.second else 240

        mapView.measure(
            View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
        )
        mapView.layout(0, 0, w, h)

        val viewJob = scope.launch {
            while (true) {
                val polyPoints = calculator.routePoints.map { Pair(it.latitude, it.longitude) }
                mapView.updateMapData(
                    lat = calculator.currentLatitude,
                    lng = calculator.currentLongitude,
                    elevation = calculator.currentElevation,
                    speedMps = calculator.currentSpeed,
                    routePoints = polyPoints
                )

                val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                mapView.draw(canvas)

                val remoteViews = RemoteViews(context.packageName, R.layout.view_remote_graphic)
                remoteViews.setImageViewBitmap(R.id.img_graphic, bitmap)

                emitter.updateView(remoteViews)

                delay(1000)
            }
        }

        emitter.setCancellable {
            viewJob.cancel()
            karooSystem?.disconnect()
            karooSystem = null
        }
    }
}