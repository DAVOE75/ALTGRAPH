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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    private var consumerId: String? = null
    // Decodificar y recortar la ruta es pesado: fuera del hilo principal y de uno en uno
    @OptIn(ExperimentalCoroutinesApi::class)
    private val worker = Dispatchers.Default.limitedParallelism(1)

    fun start(emitter: Emitter<MapEffect>) {
        consumerId = karooSystem.addConsumer<OnNavigationState> { navEvent ->
            scope.launch(worker) { updateOverlay(navEvent, emitter) }
        }
    }

    fun stop() {
        consumerId?.let { karooSystem.removeConsumer(it) }
        consumerId = null
    }

    private fun updateOverlay(navEvent: OnNavigationState, emitter: Emitter<MapEffect>) {
        val state = navEvent.state as? OnNavigationState.NavigationState.NavigatingRoute
        
        val mode = prefs.eliteMapOverlayMode
        Log.d("MapOverlayManager", "updateOverlay: mode=$mode, unlocked=${prefs.isEliteUnlocked}, state=${state != null}")
        if (mode == "off" || !prefs.isEliteUnlocked || state == null) {
            clearPolylines(emitter)
            return
        }

        // Just use polyline hash as routeId since NavigatingRoute doesn't expose routeId natively here
        val routeId = state.routePolyline.take(30)
        Log.d("MapOverlayManager", "updateOverlay: routeId=$routeId, last=$lastRouteId")
        if (routeId.isEmpty()) {
            clearPolylines(emitter)
            lastRouteId = null
            return
        }

        if (routeId == lastRouteId) return
        lastRouteId = routeId

        var elevEncoded = state.routeElevationPolyline
        if (elevEncoded.isNullOrEmpty()) {
            elevEncoded = (state.javaClass.methods.find { it.name == "getElevationPolyline" }?.invoke(state) as? String)
        }
        val pathEncoded = state.routePolyline
        Log.d("MapOverlayManager", "updateOverlay: elevLength=${elevEncoded?.length}, pathLength=${pathEncoded?.length}")

        if (elevEncoded.isNullOrEmpty() || pathEncoded.isNullOrEmpty()) {
            clearPolylines(emitter)
            return
        }

        try {
            val safeRouteDist = state.routeDistance ?: 0.0
            val elevResult = ElevationPolylineDecoder.decodeSafe(elevEncoded, safeRouteDist)
            if (elevResult !is ElevationPolylineDecoder.DecodeResult.Success) return

            val elevPoints = ElevationPolylineDecoder.smooth(elevResult.points, 10)
            val segments = createGradeSegments(elevPoints)
            
            // Filter by climbs if needed
            val finalSegments = if (mode == "climbs") {
                calculator.setRouteElevationProfile(elevEncoded, safeRouteDist)
                val climbs = calculator.routeClimbs
                segments.filter { seg ->
                    climbs.any { climb -> seg.startDist >= climb.startDistance && seg.endDist <= climb.endDistance }
                }
            } else {
                segments // Do not filter out flats for 'entire route' mode
            }.let { mergeShortSegments(it) }

            // Escalar a la distancia del perfil: los tramos se miden sobre él
            val path = RoutePath.fromPolyline(pathEncoded, elevPoints.lastOrNull()?.distance) ?: return

            Log.d("MapOverlayManager", "updateOverlay: created ${finalSegments.size} segments to draw")
            clearPolylines(emitter)

            finalSegments.forEachIndexed { index, segment ->
                val subPath = path.subPath(segment.startDist, segment.endDist)

                if (subPath.size >= 2) {
                    val encoded = PolylineCodec.encode(subPath, 1e5)
                    val colorHex = GradeColorScale.getColorHex(segment.grade)
                    val colorInt = Color.parseColor(colorHex)
                    val polylineId = "altgraph-grade-$index"
                    emitter.onNext(ShowPolyline(id = polylineId, encodedPolyline = encoded, color = colorInt, width = 8))
                    activePolylines.add(polylineId)
                }
            }
            Log.d("MapOverlayManager", "updateOverlay: Emitted ${activePolylines.size} polylines!")
        } catch (e: Exception) {
            Log.e("MapOverlayManager", "Error processing overlay", e)
        }
    }

    private fun clearPolylines(emitter: Emitter<MapEffect>) {
        try {
            activePolylines.forEach {
                emitter.onNext(HidePolyline(it))
            }
        } catch (e: Exception) {
            // El mapa del host puede haber desaparecido (DeadObjectException)
            Log.w("MapOverlayManager", "clearPolylines failed", e)
        }
        activePolylines.clear()
    }

    private fun getElevationAt(points: List<ElevationPolylineDecoder.ElevationPoint>, distance: Double): Double {
        if (points.isEmpty()) return 0.0
        val d = distance.coerceIn(points.first().distance, points.last().distance)
        var lo = 0
        var hi = points.size - 1
        while (hi - lo > 1) {
            val mid = (lo + hi) / 2
            if (points[mid].distance <= d) lo = mid else hi = mid
        }
        val p1 = points[lo]
        val p2 = points[hi]
        if (p2.distance == p1.distance) return p1.elevation
        val t = (d - p1.distance) / (p2.distance - p1.distance)
        return p1.elevation + t * (p2.elevation - p1.elevation)
    }

    private fun createGradeSegments(points: List<ElevationPolylineDecoder.ElevationPoint>): List<GradeSegment> {
        val segments = mutableListOf<GradeSegment>()
        if (points.isEmpty()) return segments

        val totalDist = points.last().distance
        val step = 10.0

        var currentStartDist = -1.0
        var currentEndDist = -1.0
        var currentColorHex = ""
        var currentGradeSum = 0.0
        var currentWeight = 0.0

        var d = 0.0
        while (d < totalDist - 1.0) {
            val endD = minOf(d + step, totalDist)
            val dist = endD - d
            if (dist < 5.0) {
                d = endD
                continue
            }
            
            val e1 = getElevationAt(points, d)
            val e2 = getElevationAt(points, endD)
            val grade = ((e2 - e1) / dist) * 100.0
            val colorHex = GradeColorScale.getColorHex(grade)

            if (currentStartDist < 0) {
                currentStartDist = d
                currentEndDist = endD
                currentColorHex = colorHex
                currentGradeSum = grade * dist
                currentWeight = dist
            } else if (colorHex == currentColorHex) {
                currentEndDist = endD
                currentGradeSum += grade * dist
                currentWeight += dist
            } else {
                val avgGrade = if (currentWeight > 0) currentGradeSum / currentWeight else 0.0
                segments.add(GradeSegment(currentStartDist, currentEndDist, avgGrade))
                
                currentStartDist = d
                currentEndDist = endD
                currentColorHex = colorHex
                currentGradeSum = grade * dist
                currentWeight = dist
            }
            d = endD
        }
        
        if (currentStartDist >= 0) {
            val avgGrade = if (currentWeight > 0) currentGradeSum / currentWeight else 0.0
            segments.add(GradeSegment(currentStartDist, currentEndDist, avgGrade))
        }

        return segments
    }

    companion object {
        // ponytail: umbral fijo; si hiciera falta más detalle en puertos cortos, bajarlo
        private const val MIN_SEGMENT_M = 300.0

        /**
         * Con pasos de 10 m salen cientos o miles de tramos en una ruta larga, y cada uno
         * es una polilínea en el mapa del Karoo. Acumula tramos contiguos hasta MIN_SEGMENT_M;
         * un último tramo corto se une al anterior.
         */
        internal fun mergeShortSegments(segments: List<GradeSegment>): List<GradeSegment> {
            val merged = mutableListOf<GradeSegment>()
            for (seg in segments) {
                val prev = merged.lastOrNull()
                if (prev != null && prev.endDist == seg.startDist && length(prev) < MIN_SEGMENT_M) {
                    merged[merged.lastIndex] = combine(prev, seg)
                } else {
                    merged.add(seg)
                }
            }
            if (merged.size >= 2) {
                val last = merged.last()
                val prev = merged[merged.lastIndex - 1]
                if (length(last) < MIN_SEGMENT_M && prev.endDist == last.startDist) {
                    merged.removeAt(merged.lastIndex)
                    merged[merged.lastIndex] = combine(prev, last)
                }
            }
            return merged
        }

        private fun length(s: GradeSegment) = s.endDist - s.startDist

        private fun combine(a: GradeSegment, b: GradeSegment): GradeSegment {
            val la = length(a)
            val lb = length(b)
            val grade = if (la + lb > 0) (a.grade * la + b.grade * lb) / (la + lb) else a.grade
            return GradeSegment(a.startDist, b.endDist, grade)
        }
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
