package com.example.altgraph

import android.content.Context
import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.internal.ViewEmitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import io.hammerhead.karooext.models.ViewConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AltimetriaGraphDataType(extension: String) : DataTypeImpl(extension, "altimetria_graph") {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var streamJob: Job? = null
    private val calculator = AltimetriaStrategyCalculator()

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
        val altimetriaView = AltimetriaView(context)

        val viewJob = scope.launch {
            while (true) {
                val strategy = calculator.calculateStrategy(context)
                altimetriaView.updateStrategyData(
                    remainingDistance = strategy.remainingDistance,
                    timeToSummit = strategy.timeToSummit,
                    avgGrade = strategy.avgGrade,
                    currentZoneColor = calculator.getZoneColor(calculator.currentElevation),
                    nextBlocks = strategy.nextBlocks,
                    attackAlert = strategy.attackAlert,
                    blockSizeMeters = strategy.blockSizeMeters
                )
                delay(1000)
            }
        }

        emitter.setCancellable {
            viewJob.cancel()
        }
    }
}