package com.example.altgraph

import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FatigueGradeDataField(extension: String) : DataTypeImpl(extension, "fatigue_grade") {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var streamJob: Job? = null

    override fun startStream(emitter: Emitter<StreamState>) {
        streamJob = scope.launch {
            ClimbStateManager.currentApm.collectLatest { fatigueValue ->
                emitter.onNext(
                    StreamState.Streaming(
                        DataPoint(
                            dataTypeId = dataTypeId,
                            values = mapOf(DataType.Field.SINGLE to fatigueValue.toDouble())
                        )
                    )
                )
            }
        }
        emitter.setCancellable {
            streamJob?.cancel()
        }
    }
}