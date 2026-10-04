package com.example.altgraph

import android.content.Context
import android.graphics.Color
import android.util.Log
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.internal.Emitter
import io.hammerhead.karooext.models.HidePolyline
import io.hammerhead.karooext.models.MapEffect
import io.hammerhead.karooext.models.OnNavigationState
import io.hammerhead.karooext.models.ShowPolyline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.math.*

class MapOverlayManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val karooSystem: KarooSystemService
) {
    private val prefs = AppPreferences(context)
    private val calculator = AltimetriaStrategyCalculator()
    private var lastRouteId: String? = null
    private var activePolylines = mutableSetOf<String>()

    data class GradeSegment(val startDist: Double, val endDist: Double, val grade: Double)

    fun start(emitter: Emitter<MapEffect>) {
        karooSystem.addConsumer<OnNavigationState> { navEvent ->
            updateOverlay(navEvent, emitter)
        }
    }

    fun stop() {
        // Consumer will be cleared when KarooSystem disconnects
    }

    private fun updateOverlay(navEvent: OnNavigationState, emitter: Emitter<MapEffect>) {
        val state = navEvent.state as? OnNavigationState.NavigationState.NavigatingRoute
        
        val mode = prefs.eliteMapOverlayMode
        if (mode == "off" || !prefs.isEliteUnlocked || state == null) {
            clearPolylines(emitter)
            return
        }

        // Just use polyline hash as routeId since NavigatingRoute doesn't expose routeId natively here
        val routeId = state.routePolyline.take(30)
        if (routeId.isEmpty()) {
            clearPolylines(emitter)
            lastRouteId = null
            return
        }

        if (routeId == lastRouteId) return
        lastRouteId = routeId

        val elevEncoded = state.routeElevationPolyline
        val pathEncoded = state.routePolyline

        if (elevEncoded.isNullOrEmpty() || pathEncoded.isNullOrEmpty()) {
            clearPolylines(emitter)
            return
        }

        try {
            val elevResult = ElevationPolylineDecoder.decodeSafe(elevEncoded)
            if (elevResult !is ElevationPolylineDecoder.DecodeResult.Success) return

            val elevPoints = elevResult.points
            val segments = createGradeSegments(elevPoints)
            
            // Filter by climbs if needed
            val finalSegments = if (mode == "climbs") {
                calculator.setRouteElevationProfile(elevEncoded)
                val climbs = calculator.routeClimbs
                segments.filter { seg ->
                    climbs.any { climb -> seg.startDist >= climb.startDistance && seg.endDist <= climb.endDistance }
                }
            } else {
                segments.filter { it.grade >= 2.0 || it.grade <= -2.0 } // Hide flats to keep map clean
            }

            val pathLatLngs = PolylineCodec.decode(pathEncoded, 1e5)
            val pathWithDistances = assignDistancesToPath(pathLatLngs)

            clearPolylines(emitter)

            finalSegments.forEachIndexed { index, segment ->
                val subPath = pathWithDistances
                    .filter { it.distance in segment.startDist..segment.endDist }
                    .map { it.lat to it.lng }

                if (subPath.size >= 2) {
                    val encoded = PolylineCodec.encode(subPath, 1e5)
                    val colorHex = GradeColorScale.getColorHex(segment.grade)
                    val colorInt = Color.parseColor(colorHex)
                    val polylineId = "altgraph-grade-$index"
                    emitter.onNext(ShowPolyline(id = polylineId, encodedPolyline = encoded, color = colorInt, width = 8))
                    activePolylines.add(polylineId)
                }
            }
        } catch (e: Exception) {
            Log.e("MapOverlayManager", "Error processing overlay", e)
        }
    }

    private fun clearPolylines(emitter: Emitter<MapEffect>) {
        activePolylines.forEach {
            emitter.onNext(HidePolyline(it))
        }
        activePolylines.clear()
    }

    private fun createGradeSegments(points: List<ElevationPolylineDecoder.ElevationPoint>): List<GradeSegment> {
        val segments = mutableListOf<GradeSegment>()
        val step = 50.0 // 50m buckets
        
        val totalDist = points.lastOrNull()?.distance ?: return emptyList()
        var currentDist = 0.0

        while (currentDist < totalDist) {
            val endDist = min(currentDist + step, totalDist)
            
            val startElev = points.firstOrNull { it.distance >= currentDist }?.elevation
            val endElev = points.lastOrNull { it.distance <= endDist }?.elevation
            
            if (startElev != null && endElev != null && (endDist - currentDist) > 10.0) {
                val grade = ((endElev - startElev) / (endDist - currentDist)) * 100.0
                segments.add(GradeSegment(currentDist, endDist, grade))
            }
            currentDist += step
        }
        
        // Simple smoothing/merging could go here, but buckets are enough for visual overlay
        return segments
    }

    data class PathPoint(val lat: Double, val lng: Double, val distance: Double)

    private fun assignDistancesToPath(path: List<Pair<Double, Double>>): List<PathPoint> {
        val result = mutableListOf<PathPoint>()
        var totalDist = 0.0
        
        if (path.isEmpty()) return result
        result.add(PathPoint(path[0].first, path[0].second, 0.0))
        
        for (i in 1 until path.size) {
            val prev = path[i - 1]
            val curr = path[i]
            val dist = haversineDistance(prev.first, prev.second, curr.first, curr.second)
            totalDist += dist
            result.add(PathPoint(curr.first, curr.second, totalDist))
        }
        return result
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // meters
        val phi1 = lat1 * PI / 180.0
        val phi2 = lat2 * PI / 180.0
        val deltaPhi = (lat2 - lat1) * PI / 180.0
        val deltaLambda = (lon2 - lon1) * PI / 180.0

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c
    }
}
