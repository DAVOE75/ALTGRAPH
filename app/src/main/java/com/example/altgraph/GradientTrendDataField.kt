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

class GradientTrendDataField(extension: String) : DataTypeImpl(extension, "gradient_trend") {

    private val trendTracker = GradientTrendTracker()

    override fun startStream(emitter: Emitter<StreamState>) {
        val latch = CancelLatch()
        emitter.setCancellable { latch.cancel() }
        AltgraphRepository.hold(emitter)
        val streamJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val snap = AltgraphRepository.snapshot.value
                if (snap != null) {
                    safeUpdate(onDead = { cancel(); AltgraphRepository.release(emitter) }) {
                        val grade = snap.rawGrade
                        val effectiveGrade = if (grade > 0.1) grade else 7.5
                        // El tracker no es thread-safe y stream y vista corren en hilos distintos
                        val trendResult = synchronized(trendTracker) { trendTracker.addSample(effectiveGrade) }

                        val trendCode = when (trendResult.trend) {
                            GradientTrend.STEEPENING -> 1.0  // ↗️ Aumentando pendiente
                            GradientTrend.STEADY -> 0.0      // ➔ Estabilidad
                            GradientTrend.EASING -> -1.0     // ↘️ Suavizando
                        }

                        val maxRamp = if (trendResult.maxRampPeak > 0.1) trendResult.maxRampPeak else 12.8

                        emitter.onNext(
                            StreamState.Streaming(
                                DataPoint(
                                    dataTypeId = dataTypeId,
                                    values = mapOf(
                                        DataType.Field.SINGLE to trendResult.currentGrade,
                                        "trend_code" to trendCode,
                                        "max_ramp_peak" to maxRamp
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

        val trendView = GradientTrendView(context)
        trendView.measure(
            View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY)
        )
        trendView.layout(0, 0, w, h)

        // Un único bitmap por vista: updateView es síncrono, así que se puede reutilizar
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        var lastFrame: List<Any>? = null

        val viewJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val snap = AltgraphRepository.snapshot.value
                if (snap != null) {
                    safeUpdate(onDead = { cancel(); AltgraphRepository.release(emitter) }) {
                        val grade = snap.rawGrade
                        val effectiveGrade = if (grade > 0.1) grade else 7.5
                        // El tracker no es thread-safe y stream y vista corren en hilos distintos
                        val trendResult = synchronized(trendTracker) { trendTracker.addSample(effectiveGrade) }
                        val maxRamp = if (trendResult.maxRampPeak > 0.1) trendResult.maxRampPeak else 12.8

                        // Mismo formato que muestra la vista: no redibujar si no cambia lo que se ve
                        val frame = listOf("%.1f".format(trendResult.currentGrade), trendResult.trend, "%.1f".format(maxRamp))
                        if (frame == lastFrame) return@safeUpdate

                        trendView.updateTrendData(trendResult.currentGrade, trendResult.trend, maxRamp)

                        bitmap.eraseColor(android.graphics.Color.TRANSPARENT)
                        trendView.draw(canvas)

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
