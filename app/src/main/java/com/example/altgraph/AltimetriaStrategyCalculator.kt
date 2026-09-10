package com.example.altgraph

import android.content.Context
import kotlin.math.hypot
import kotlin.math.roundToInt

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val distance: Double
)

data class StrategyData(
    val remainingDistance: Double,
    val timeToSummit: Long,
    val avgGrade: Double,
    val nextBlocks: List<Float>,
    val attackAlert: Boolean,
    val blockSizeMeters: Double,
    val totalFatigueGrade: Int
)

class AltimetriaStrategyCalculator {

    var currentLatitude = 0.0
    var currentLongitude = 0.0
    var currentSpeed = 0.0
    var currentElevation = 350.0

    var routePoints: List<RoutePoint> = emptyList()

    fun calculateStrategy(context: Context? = null): StrategyData {
        val prefs = context?.let { AppPreferences.getInstance(it) }

        val blockSize = prefs?.blockSizeMeters ?: 100.0
        val thresholdAttack = prefs?.thresholdAttackPct ?: 10.0
        val attackAlertsEnabled = prefs?.attackAlertEnabled ?: true
        val asphaltFactor = prefs?.asphaltFactor ?: 0.5

        // MODO DEMO / SIMULACIÓN: Cuando no hay ruta cargada o el vehículo está detenido
        if (routePoints.isEmpty() || currentSpeed <= 0.1) {
            val demoBlocks = listOf(3.5f, 5.0f, 7.5f, 11.2f, 12.8f, 9.0f, 6.5f, 8.2f, 10.5f, 4.0f)
            val hasAttack = attackAlertsEnabled && demoBlocks.any { it > thresholdAttack }
            val demoFatigue = 68

            ClimbStateManager.updateApm(demoFatigue)

            return StrategyData(
                remainingDistance = 8500.0,
                timeToSummit = 1620L,
                avgGrade = 7.2,
                nextBlocks = demoBlocks,
                attackAlert = hasAttack,
                blockSizeMeters = blockSize,
                totalFatigueGrade = demoFatigue
            )
        }

        var nearestIndex = 0
        var minDistance = Double.MAX_VALUE
        routePoints.forEachIndexed { index, point ->
            val dist = hypot(point.latitude - currentLatitude, point.longitude - currentLongitude)
            if (dist < minDistance) {
                minDistance = dist
                nearestIndex = index
            }
        }

        val totalDistanceRemaining = routePoints.drop(nearestIndex).sumOf { it.distance }
        val endElevation = routePoints.last().elevation
        val elevationGainRemaining = (endElevation - currentElevation).coerceAtLeast(0.0)
        val avgGradeRemaining = if (totalDistanceRemaining > 0) (elevationGainRemaining / totalDistanceRemaining) * 100.0 else 0.0
        val secondsRemaining = if (currentSpeed > 0.1) (totalDistanceRemaining / currentSpeed).toLong() else 0L

        val nextBlocks = mutableListOf<Float>()
        var attack = false
        var accumulatedHardness = 0.0
        var maxRampPct = 0.0

        var currentBlockStartDistance = routePoints[nearestIndex].distance
        var currentBlockStartElevation = routePoints[nearestIndex].elevation

        for (i in nearestIndex + 1 until routePoints.size) {
            val point = routePoints[i]
            val distanceDiff = point.distance - currentBlockStartDistance

            if (distanceDiff >= blockSize) {
                val grade = ((point.elevation - currentBlockStartElevation) / distanceDiff) * 100.0
                val distanceKm = distanceDiff / 1000.0

                if (grade > maxRampPct) {
                    maxRampPct = grade
                }

                accumulatedHardness += FatigueGradeCalculator.calculateSegmentHardness(grade, distanceKm)

                if (nextBlocks.size < 10) {
                    nextBlocks.add(grade.toFloat())
                    if (attackAlertsEnabled && grade > thresholdAttack) {
                        attack = true
                    }
                }

                currentBlockStartDistance = point.distance
                currentBlockStartElevation = point.elevation
            }
        }

        val totalGf = FatigueGradeCalculator.calculateTotalFatigueGrade(
            totalHardness = accumulatedHardness,
            asphaltFactor = asphaltFactor,
            maxRampPct = maxRampPct
        ).roundToInt()

        ClimbStateManager.updateApm(totalGf)

        return StrategyData(
            remainingDistance = totalDistanceRemaining,
            timeToSummit = secondsRemaining,
            avgGrade = avgGradeRemaining,
            nextBlocks = nextBlocks,
            attackAlert = attack,
            blockSizeMeters = blockSize,
            totalFatigueGrade = totalGf
        )
    }

    fun getZoneColor(currentElevation: Double): String {
        return when {
            currentElevation > 2000 -> "VIOLETA"
            currentElevation > 1500 -> "ROJO"
            currentElevation > 1000 -> "NARANJA"
            currentElevation > 500 -> "AMARILLO"
            else -> "VERDE"
        }
    }
}