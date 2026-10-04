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
import io.hammerhead.karooext.models.OnStreamState
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClimbPacingDataField(extension: String) : DataTypeImpl(extension, "climb_pacing") {

    private val scope = CoroutineScope(Dispatchers.Main)
    // Conexión compartida entre las vistas abiertas de este campo: antes el cancel de
    // una vista la desconectaba aunque otra vista siguiera usándola
    private val systemRef = RefCounted<KarooSystemService> { it.disconnect() }

    var currentSpeedMps: Double = 0.0
    var currentGradientPct: Double = 0.0

    override fun startStream(emitter: Emitter<StreamState>) {
        val streamJob = scope.launch {
            while (true) {
                val targetVam = 900
                val result = ClimbPacingCalculator.calculatePacing(
                    currentSpeedMps = currentSpeedMps,
                    currentGradientPct = currentGradientPct,
                    userTargetVam = targetVam
                )

                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(
                                DataType.Field.SINGLE to result.currentVam.toDouble(),
                                "target_vam" to result.targetVam.toDouble(),
                                "target_speed_kmh" to result.targetSpeedKmh
                            )
                        )
                    )
                )
                delay(1000)
            }
        }

        emitter.setCancellable {
            streamJob.cancel()
        }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        emitter.onNext(UpdateGraphicConfig(showHeader = false))

        systemRef.acquire(emitter) {
            val system = KarooSystemService(context)
            system.connect { connected ->
                if (connected) {
                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.SPEED)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val spd = streamState.dataPoint.values[DataType.Field.SPEED]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            currentSpeedMps = spd
                        }
                    }

                    system.addConsumer(OnStreamState.StartStreaming(DataType.Type.ELEVATION_GRADE)) { state: OnStreamState ->
                        val streamState = state.state
                        if (streamState is StreamState.Streaming) {
                            val grade = streamState.dataPoint.values[DataType.Field.ELEVATION_GRADE]
                                ?: streamState.dataPoint.values[DataType.Field.SINGLE]
                                ?: 0.0
                            currentGradientPct = grade
                        }
                    }
                }
            }
            system
        }

        val w = if (config.viewSize.first > 0) config.viewSize.first else 480
        val h = if (config.viewSize.second > 0) config.viewSize.second else 240

        val pacingView = ClimbPacingView(context)
        pacingView.measure(
            View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
        )
        pacingView.layout(0, 0, w, h)

        // Un único bitmap por vista: updateView es síncrono, así que se puede reutilizar
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        var lastFrame: List<Any>? = null

        val viewJob = scope.launch {
            while (true) {
                val prefs = AppPreferences.getInstance(context)
                val targetVam = prefs.targetVam
                val result = ClimbPacingCalculator.calculatePacing(
                    currentSpeedMps = currentSpeedMps,
                    currentGradientPct = currentGradientPct,
                    userTargetVam = targetVam
                )

                // Mismo formato que muestra la vista: no redibujar si no cambia lo que se ve
                val frame = listOf(result.currentVam, result.targetVam, "%.1f".format(result.targetSpeedKmh), result.status)
                if (frame == lastFrame) {
                    delay(1000)
                    continue
                }
                lastFrame = frame

                pacingView.updatePacingData(result)

                bitmap.eraseColor(android.graphics.Color.TRANSPARENT)
                pacingView.draw(canvas)

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