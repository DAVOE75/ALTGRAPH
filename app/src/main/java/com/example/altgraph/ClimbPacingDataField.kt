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

class ClimbPacingDataField(extension: String) : DataTypeImpl(extension, "climb_pacing") {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var streamJob: Job? = null

    var currentSpeedMps: Double = 0.0
    var currentGradientPct: Double = 0.0
    var userTargetVam: Int = 900

    override fun startStream(emitter: Emitter<StreamState>) {
        streamJob = scope.launch {
            while (true) {
                val result = ClimbPacingCalculator.calculatePacing(
                    currentSpeedMps = currentSpeedMps,
                    currentGradientPct = currentGradientPct,
                    userTargetVam = userTargetVam
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
            streamJob?.cancel()
        }
    }
}