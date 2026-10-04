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

class AltimetriaGraphDataType(extension: String) : DataTypeImpl(extension, "altimetria_graph") {

    override fun startStream(emitter: Emitter<StreamState>) {
        AltgraphRepository.hold(emitter)
        val job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val snap = AltgraphRepository.snapshot.value
                if (snap != null) {
                    val strategy = snap.strategy
                    safeUpdate(onDead = { cancel(); AltgraphRepository.release(emitter) }) {
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
                    }
                }
                delay(1000)
            }
        }
        emitter.setCancellable {
            job.cancel()
            AltgraphRepository.release(emitter)
        }
    }

    override fun startView(context: Context, config: ViewConfig, emitter: ViewEmitter) {
        emitter.onNext(UpdateGraphicConfig(showHeader = false))
        AltgraphRepository.hold(emitter)

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

        val job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val snap = AltgraphRepository.snapshot.value
                if (snap != null) {
                    safeUpdate(onDead = { cancel(); AltgraphRepository.release(emitter) }) {
                        val prefs = AppPreferences.getInstance(context)
                        val strategy = snap.strategy
                        val zoneColor = snap.zoneColor

                        // Nada ha cambiado (parado, sin ruta...): no redibujar ni reenviar el bitmap
                        val frame = listOf(strategy, zoneColor, prefs.showBlockPercentages, prefs.visibleBlocksCount)
                        if (frame != lastFrame) {
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
                            // Solo tras enviar con éxito: si el frame falla, el siguiente tick lo reintenta
                            lastFrame = frame
                        }
                    }
                }
                delay(1000)
            }
        }

        emitter.setCancellable {
            job.cancel()
            AltgraphRepository.release(emitter)
        }
    }
}
