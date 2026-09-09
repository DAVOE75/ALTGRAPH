package com.example.altgraph

import kotlin.math.roundToInt

object ClimbPacingCalculator {

    enum class PacingStatus {
        ON_PACE,
        OVERPACING,
        UNDERPACING
    }

    data class PacingResult(
        val currentVam: Int,
        val targetVam: Int,
        val targetSpeedKmh: Double,
        val status: PacingStatus
    )

    fun calculatePacing(
        currentSpeedMps: Double,
        currentGradientPct: Double,
        userTargetVam: Int = 900
    ): PacingResult {
        if (currentSpeedMps <= 0.1 || currentGradientPct <= 0.5) {
            return PacingResult(
                currentVam = 0,
                targetVam = userTargetVam,
                targetSpeedKmh = 0.0,
                status = PacingStatus.ON_PACE
            )
        }

        // VAM instantánea = m/s * 3600 * (pendiente / 100)
        val calculatedVam = (currentSpeedMps * 3600.0 * (currentGradientPct / 100.0)).roundToInt()

        // Velocidad objetivo en km/h para mantener la VAM deseada
        val targetSpeedKmh = (userTargetVam.toDouble() / (currentGradientPct * 10.0)).coerceIn(3.0, 45.0)

        val diffRatio = if (userTargetVam > 0) calculatedVam.toDouble() / userTargetVam.toDouble() else 1.0

        val status = when {
            diffRatio > 1.15 -> PacingStatus.OVERPACING
            diffRatio < 0.85 -> PacingStatus.UNDERPACING
            else -> PacingStatus.ON_PACE
        }

        return PacingResult(
            currentVam = calculatedVam,
            targetVam = userTargetVam,
            targetSpeedKmh = targetSpeedKmh,
            status = status
        )
    }
}