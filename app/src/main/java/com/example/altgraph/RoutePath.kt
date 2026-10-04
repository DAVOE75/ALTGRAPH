package com.example.altgraph

import kotlin.math.*

/**
 * Ruta (lat, lng) con distancia acumulada, para recortar tramos por distancia
 * a lo largo de la ruta. Las distancias se escalan a routeDistance para que
 * coincidan con las del perfil de elevación del Karoo.
 */
class RoutePath private constructor(
    private val points: List<Pair<Double, Double>>,
    private val cumDist: DoubleArray
) {
    /** Puntos entre startDist y endDist, con los extremos interpolados. */
    fun subPath(startDist: Double, endDist: Double): List<Pair<Double, Double>> {
        if (endDist <= startDist) return emptyList()
        val result = mutableListOf(pointAt(startDist))
        var i = lowerBound(startDist)
        while (i < points.size && cumDist[i] < endDist) {
            if (cumDist[i] > startDist) result.add(points[i])
            i++
        }
        result.add(pointAt(endDist))
        return result
    }

    private fun pointAt(dist: Double): Pair<Double, Double> {
        val d = dist.coerceIn(0.0, cumDist.last())
        val hi = lowerBound(d).coerceIn(1, points.size - 1)
        val lo = hi - 1
        val span = cumDist[hi] - cumDist[lo]
        val t = if (span > 0) (d - cumDist[lo]) / span else 0.0
        val (lat1, lng1) = points[lo]
        val (lat2, lng2) = points[hi]
        return (lat1 + t * (lat2 - lat1)) to (lng1 + t * (lng2 - lng1))
    }

    /** Primer índice con cumDist >= d. */
    private fun lowerBound(d: Double): Int {
        var lo = 0
        var hi = cumDist.size
        while (lo < hi) {
            val mid = (lo + hi) ushr 1
            if (cumDist[mid] < d) lo = mid + 1 else hi = mid
        }
        return lo
    }

    companion object {
        fun fromPolyline(encoded: String, routeDistance: Double?): RoutePath? {
            val points = PolylineCodec.decode(encoded, 1e5)
            if (points.size < 2) return null
            val cum = DoubleArray(points.size)
            for (i in 1 until points.size) {
                val (lat1, lng1) = points[i - 1]
                val (lat2, lng2) = points[i]
                cum[i] = cum[i - 1] + haversine(lat1, lng1, lat2, lng2)
            }
            val total = cum.last()
            if (total <= 0.0) return null
            if (routeDistance != null && routeDistance > 0.0) {
                val scale = routeDistance / total
                for (i in cum.indices) cum[i] *= scale
            }
            return RoutePath(points, cum)
        }

        private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val dPhi = (lat2 - lat1) * PI / 180.0
            val dLambda = (lon2 - lon1) * PI / 180.0
            val a = sin(dPhi / 2).pow(2) +
                    cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLambda / 2).pow(2)
            return 6371000.0 * 2 * atan2(sqrt(a), sqrt(1 - a))
        }
    }
}
