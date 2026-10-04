package com.example.altgraph

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.RemoteViews
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.UpdateGraphicConfig
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ClimbPacingDataField(extension: String) : DataTypeImpl(extension, "climb_pacing") {

    override fun startStream(emitter: Emitter<StreamState>) {
        val latch = CancelLatch()
        emitter.setCancellable { latch.cancel() }
        AltgraphRepository.hold(emitter)
        val streamJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val snap = AltgraphRepository.snapshot.value
                if (snap != null) {
                    safeUpdate(onDead = { cancel(); AltgraphRepository.release(emitter) }) {
                        val targetVam = 900
                        val result = ClimbPacingCalculator.calculatePacing(
                            currentSpeedMps = snap.currentSpeed,
                            currentGradientPct = snap.instantBarometricGrade,
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
                    }
                }
                delay(1000)
            }
        }

        latch.attach(streamJob) {
            AltgraphRepository.release(emitter)
        }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        val latch = CancelLatch()
        emitter.setCancellable { latch.cancel() }
        emitter.onNext(UpdateGraphicConfig(showHeader = false))

        AltgraphRepository.hold(emitter)

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

        val viewJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val snap = AltgraphRepository.snapshot.value
                if (snap != null) {
                    safeUpdate(onDead = { cancel(); AltgraphRepository.release(emitter) }) {
                        val prefs = AppPreferences.getInstance(context)
                        val targetVam = prefs.targetVam
                        val result = ClimbPacingCalculator.calculatePacing(
                            currentSpeedMps = snap.currentSpeed,
                            currentGradientPct = snap.instantBarometricGrade,
                            userTargetVam = targetVam
                        )

                        // Mismo formato que muestra la vista: no redibujar si no cambia lo que se ve
                        val frame = listOf(result.currentVam, result.targetVam, "%.1f".format(result.targetSpeedKmh), result.status)
                        if (frame == lastFrame) return@safeUpdate

                        pacingView.updatePacingData(result)

                        bitmap.eraseColor(android.graphics.Color.TRANSPARENT)
                        pacingView.draw(canvas)

                        val remoteViews = RemoteViews(context.packageName, R.layout.view_remote_graphic)
                        remoteViews.setImageViewBitmap(R.id.img_graphic, bitmap)

                        emitter.updateView(remoteViews)
                        // Solo tras enviar con éxito: si el frame falla, el siguiente tick lo reintenta
                        lastFrame = frame
                    }
                }
                delay(1000)
            }
        }

        latch.attach(viewJob) {
            AltgraphRepository.release(emitter)
        }
    }
}
