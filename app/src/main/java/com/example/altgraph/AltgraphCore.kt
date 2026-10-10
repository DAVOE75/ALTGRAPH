package com.example.altgraph

import android.content.Context

/** Foto inmutable del estado compartido; las vistas la leen sin tocar el calculador. */
data class Snapshot(
    val strategy: StrategyData,
    val currentElevation: Double,
    val instantBarometricGrade: Double,
    // ELEVATION_GRADE tal cual llega del stream (instantBarometricGrade lo mezcla además con la EMA de elevación)
    val rawGrade: Double,
    val currentHeading: Double,
    val currentSpeed: Double,
    val currentRouteDistance: Double,
    val routeTotalLength: Double,
    val isNavigatingRoute: Boolean,
    val zoneColor: String,
    val routeClimbs: List<RouteClimb>,
    // Hueco con el fantasma de KGhost (null si KGhost no publica)
    val ghostGap: GhostGap? = null
)

/**
 * Lógica pura (sin hilos ni Karoo). Sin sincronización interna: quien la usa
 * garantiza que siempre se llama desde el mismo hilo.
 */
class AltgraphCore(val calculator: AltimetriaStrategyCalculator = AltimetriaStrategyCalculator()) {

    var mapZoomLevel: Double? = null
    var pan3dMeters: Double = 0.0
    var rawGrade: Double = 0.0
    // Últimas partes del hueco de KGhost (tiempo y distancia); null = sin dato
    var kghostTime: GapPart? = null
    var kghostDist: GapPart? = null

    /** Único punto que avanza el calculador (una vez por segundo). */
    fun tick(context: Context?): Snapshot {
        calculator.mapZoomLevel = null
        calculator.manualPanOffsetMeters = 0.0
        val strategy = calculator.calculateStrategy(context, advance = true)
        return Snapshot(
            strategy = strategy,
            currentElevation = calculator.currentElevation,
            instantBarometricGrade = calculator.instantBarometricGrade,
            rawGrade = rawGrade,
            currentHeading = calculator.currentHeading,
            currentSpeed = calculator.currentSpeed,
            currentRouteDistance = calculator.currentRouteDistance,
            routeTotalLength = if (calculator.routePoints.isNotEmpty()) calculator.routePoints.last().distance else 0.0,
            isNavigatingRoute = calculator.isNavigatingRoute,
            zoneColor = calculator.getZoneColor(calculator.currentElevation),
            routeClimbs = calculator.routeClimbs.toList(),
            ghostGap = combineGhostGap(kghostTime, kghostDist)
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
        
    fun getFullRouteCoords(): List<Pair<Double, Double>> {
        return calculator.routePoints.map { Pair(it.latitude, it.longitude) }
    }

    fun getFullProfileElevations(): FloatArray {
        val pts = if (calculator.routeElevationProfile.isNotEmpty()) {
            calculator.routeElevationProfile
        } else {
            calculator.routePoints.map { com.example.altgraph.ElevationPolylineDecoder.ElevationPoint(it.distance, it.elevation) }
        }
        val count = pts.size
        if (count == 0) return FloatArray(0)
        
        val maxPoints = 1000
        if (count <= maxPoints) {
            return FloatArray(count) { pts[it].elevation.toFloat() }
        }
        
        val result = FloatArray(maxPoints)
        for (i in 0 until maxPoints) {
            val idx = (i * count) / maxPoints
            result[i] = pts[idx].elevation.toFloat()
        }
        return result
    }
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
