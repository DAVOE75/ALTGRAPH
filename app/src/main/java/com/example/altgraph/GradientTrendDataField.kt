package com.example.altgraph

import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GradientTrendDataField(extension: String) : DataTypeImpl(extension, "gradient_trend") {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var streamJob: Job? = null
    private val trendTracker = GradientTrendTracker()

    var currentGradientPct: Double = 0.0

    override fun startStream(emitter: Emitter<StreamState>) {
        streamJob = scope.launch {
            while (true) {
                val effectiveGrade = if (currentGradientPct > 0.1) currentGradientPct else 7.5
                val trendResult = trendTracker.addSample(effectiveGrade)

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
                delay(1000)
            }
        }

        emitter.setCancellable {
            streamJob?.cancel()
        }
    }
}