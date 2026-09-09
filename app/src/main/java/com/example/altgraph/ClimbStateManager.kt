package com.example.altgraph

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ClimbStateManager {
    private val _currentApm = MutableStateFlow(0)
    val currentApm: StateFlow<Int> = _currentApm.asStateFlow()

    fun updateApm(apm: Int) {
        _currentApm.value = apm
    }
}