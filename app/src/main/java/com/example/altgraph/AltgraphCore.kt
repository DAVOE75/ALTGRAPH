package com.example.altgraph

import android.content.Context

/** Foto inmutable del estado compartido; las vistas la leen sin tocar el calculador. */
data class Snapshot(
    val strategy: StrategyData,
    val currentElevation: Double,
    val instantBarometricGrade: Double,
    val currentHeading: Double,
    val currentSpeed: Double,
    val currentRouteDistance: Double,
    val isNavigatingRoute: Boolean,
    val zoneColor: String,
    val routeClimbs: List<RouteClimb>
)

/**
 * Lógica pura (sin hilos ni Karoo). Sin sincronización interna: quien la usa
 * garantiza que siempre se llama desde el mismo hilo.
 */
class AltgraphCore(val calculator: AltimetriaStrategyCalculator = AltimetriaStrategyCalculator()) {

    var mapZoomLevel: Double? = null
    var pan3dMeters: Double = 0.0

    /** Único punto que avanza el calculador (una vez por segundo). */
    fun tick(context: Context?): Snapshot {
        calculator.mapZoomLevel = null
        calculator.manualPanOffsetMeters = 0.0
        val strategy = calculator.calculateStrategy(context, advance = true)
        return Snapshot(
            strategy = strategy,
            currentElevation = calculator.currentElevation,
            instantBarometricGrade = calculator.instantBarometricGrade,
            currentHeading = calculator.currentHeading,
            currentSpeed = calculator.currentSpeed,
            currentRouteDistance = calculator.currentRouteDistance,
            isNavigatingRoute = calculator.isNavigatingRoute,
            zoneColor = calculator.getZoneColor(calculator.currentElevation),
            routeClimbs = calculator.routeClimbs.toList()
        )
    }

    fun strategyForRouteBar(context: Context?, viewWidth: Int, base: Snapshot): StrategyData {
        if (mapZoomLevel == null) return base.strategy
        calculator.viewWidth = viewWidth
        calculator.mapZoomLevel = mapZoomLevel
        try {
            return calculator.calculateStrategy(context, advance = false)
        } finally {
            calculator.mapZoomLevel = null
        }
    }

    fun strategyFor3D(context: Context?, base: Snapshot): StrategyData {
        if (pan3dMeters == 0.0) return base.strategy
        calculator.manualPanOffsetMeters = pan3dMeters
        try {
            return calculator.calculateStrategy(context, advance = false)
        } finally {
            calculator.manualPanOffsetMeters = 0.0
        }
    }

    fun climbStrategy(climb: RouteClimb, start: Double?, length: Double?): StrategyData =
        calculator.getStrategyDataForClimb(climb, start, length)
}

/** Holders activos del tick; se llama desde hilos Binder. */
class TickGate {
    private val holders = mutableSetOf<Any>()

    @Synchronized
    fun add(token: Any): Boolean = holders.add(token) && holders.size == 1

    @Synchronized
    fun remove(token: Any): Boolean = holders.remove(token) && holders.isEmpty()

    val isActive: Boolean @Synchronized get() = holders.isNotEmpty()
}
