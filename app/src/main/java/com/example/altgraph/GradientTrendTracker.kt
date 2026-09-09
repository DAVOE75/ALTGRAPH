package com.example.altgraph

enum class GradientTrend {
    STEEPENING,
    STEADY,
    EASING
}

data class GradientTrendResult(
    val currentGrade: Double,
    val trend: GradientTrend,
    val maxRampPeak: Double
)

class GradientTrendTracker {

    private val samples = ArrayDeque<Double>()
    private val sampleCapacity = 15 // 15 segundos
    var maxRampPeak: Double = 0.0
        private set

    fun addSample(gradientPct: Double): GradientTrendResult {
        if (samples.size >= sampleCapacity) {
            samples.removeFirst()
        }
        samples.addLast(gradientPct)

        if (gradientPct > maxRampPeak) {
            maxRampPeak = gradientPct
        }

        val shortTermAvg = if (samples.size >= 3) {
            samples.takeLast(3).average()
        } else {
            gradientPct
        }

        val longTermAvg = samples.average()

        val diff = shortTermAvg - longTermAvg

        val trend = when {
            diff > 0.8 -> GradientTrend.STEEPENING
            diff < -0.8 -> GradientTrend.EASING
            else -> GradientTrend.STEADY
        }

        return GradientTrendResult(
            currentGrade = gradientPct,
            trend = trend,
            maxRampPeak = maxRampPeak
        )
    }

    fun resetMaxRamp() {
        maxRampPeak = 0.0
    }
}