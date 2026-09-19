package com.example.altgraph

import android.util.Log

/**
 * Decodes Google Encoded Polyline format for elevation profile data.
 *
 * Karoo SDK 1.1.7 provides routeElevationPolyline as part of NavigationState.
 * Format: pairs of (distance_delta, elevation_delta) encoded with variable-length encoding.
 */
object ElevationPolylineDecoder {

    private const val TAG = "ElevPolyDecoder"
    private const val DEFAULT_PRECISION = 1.0
    private const val ALT_PRECISION = 1e5

    data class ElevationPoint(
        val distance: Double,  // meters from start
        val elevation: Double  // meters altitude
    )

    sealed class DecodeResult {
        data class Success(val points: List<ElevationPoint>) : DecodeResult()
        data class Error(val message: String) : DecodeResult()
    }

    /**
     * Decode with automatic precision detection and validation.
     */
    fun decodeSafe(encoded: String?): DecodeResult {
        if (encoded.isNullOrEmpty()) {
            return DecodeResult.Error("Empty polyline")
        }

        return try {
            val points = decodeWithPrecision(encoded, DEFAULT_PRECISION)

            if (points.isEmpty()) {
                return DecodeResult.Error("Decoded to empty list")
            }

            val hasInvalid = points.any { p ->
                p.distance < 0 || p.distance > 1_000_000 ||
                        p.elevation < -500 || p.elevation > 9000
            }

            if (hasInvalid) {
                Log.w(TAG, "Default precision gave invalid values, trying alternative")
                val altPoints = decodeWithPrecision(encoded, ALT_PRECISION)
                val altInvalid = altPoints.any { p ->
                    p.distance < 0 || p.elevation < -500
                }
                if (!altInvalid && altPoints.isNotEmpty()) {
                    return DecodeResult.Success(altPoints)
                }
                return DecodeResult.Error("Invalid decoded values with both precisions")
            }

            DecodeResult.Success(points)
        } catch (e: Exception) {
            Log.e(TAG, "Decode failed: ${e.message}")
            DecodeResult.Error(e.message ?: "Unknown error")
        }
    }

    private fun decodeWithPrecision(encoded: String, precision: Double): List<ElevationPoint> {
        val points = mutableListOf<ElevationPoint>()
        var index = 0
        var distance = 0.0
        var elevation = 0.0

        while (index < encoded.length) {
            // Decode distance delta
            var shift = 0
            var result = 0
            var byte: Int

            do {
                if (index >= encoded.length) break
                byte = encoded[index++].code - 63
                result = result or ((byte and 0x1f) shl shift)
                shift += 5
            } while (byte >= 0x20)

            val distDelta = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            distance += distDelta / precision

            // Decode elevation delta
            shift = 0
            result = 0

            do {
                if (index >= encoded.length) break
                byte = encoded[index++].code - 63
                result = result or ((byte and 0x1f) shl shift)
                shift += 5
            } while (byte >= 0x20)

            val elevDelta = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            elevation += elevDelta / precision

            points.add(ElevationPoint(distance, elevation))
        }

        return points
    }

    /**
     * Smooth elevation profile to reduce GPS noise.
     * Simple moving average over the given window.
     */
    fun smooth(points: List<ElevationPoint>, windowSize: Int = 5): List<ElevationPoint> {
        if (points.size < windowSize) return points

        return points.mapIndexed { i, point ->
            val start = maxOf(0, i - windowSize / 2)
            val end = minOf(points.size, i + windowSize / 2 + 1)
            val avg = points.subList(start, end).map { it.elevation }.average()
            point.copy(elevation = avg)
        }
    }
}
