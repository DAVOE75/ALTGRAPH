package com.example.altgraph

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.RemoteViews
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.ViewEmitter
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
    private var renderJob: Job? = null
    private var karooSystem: KarooSystemService? = null

    private var currentSpeed = 0.0
    private var currentLat = 0.0
    private var currentLng = 0.0
    private var currentElevation = 0.0
    private var routePoints = emptyList<Pair<Double, Double>>()

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        emitter.onNext(UpdateGraphicConfig(showHeader = false))

        val mapView = AltgraphMapView(context)
        val measureWidth = if (config.viewSize.first > 0) config.viewSize.first else 400
        val measureHeight = if (config.viewSize.second > 0) config.viewSize.second else 600

        mapView.measure(
            View.MeasureSpec.makeMeasureSpec(measureWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(measureHeight, View.MeasureSpec.EXACTLY)
        )
        mapView.layout(0, 0, measureWidth, measureHeight)

        if (karooSystem == null) {
            val system = KarooSystemService(context)
            system.connect { connected ->
                if (connected) {
                    system.addConsumer<OnNavigationState> { navEvent ->
                        val state = navEvent.state
                        if (state is OnNavigationState.NavigationState.NavigatingRoute) {
                            routePoints = decodePolyline(state.routePolyline)
                        }
                    }

                    system.addConsumer(OnStreamState.StartStreaming("SPEED")) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            currentSpeed = streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                        }
                    }

                    system.addConsumer(OnStreamState.StartStreaming("POSITION")) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            currentLat = streamState.dataPoint.values["latitude"] ?: streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                            currentLng = streamState.dataPoint.values["longitude"] ?: 0.0
                        }
                    }

                    system.addConsumer(OnStreamState.StartStreaming("ELEVATION")) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            currentElevation = streamState.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                        }
                    }
                }
            }
            karooSystem = system
        }

        renderJob = scope.launch {
            while (true) {
                mapView.updateMapData(
                    lat = currentLat,
                    lng = currentLng,
                    elevation = currentElevation,
                    speedMps = currentSpeed,
                    routePoints = routePoints
                )

                val bitmap = Bitmap.createBitmap(measureWidth, measureHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                mapView.draw(canvas)

                val remoteViews = RemoteViews(context.packageName, R.layout.view_remote_graphic)
                remoteViews.setImageViewBitmap(R.id.img_graphic, bitmap)

                emitter.updateView(remoteViews)
                delay(1000)
            }
        }

        emitter.setCancellable {
            renderJob?.cancel()
            karooSystem?.disconnect()
            karooSystem = null
        }
    }

    private fun decodePolyline(encoded: String): List<Pair<Double, Double>> {
        val poly = mutableListOf<Pair<Double, Double>>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                if (index >= len) break
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                if (index >= len) break
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(Pair(lat.toDouble() / 1E5, lng.toDouble() / 1E5))
        }
        return poly
    }
}