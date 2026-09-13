package com.example.altgraph

import android.content.Context
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val distance: Double
)

enum class PoiType { TOWN, WATER, VIEWPOINT, SUMMIT }

data class Poi(val relativeDistance: Double, val icon: String, val name: String, val type: PoiType)

data class StrategyData(
    val remainingDistance: Double,
    val timeToSummit: Long,
    val avgGrade: Double,
    val nextBlocks: List<Float>,
    val attackAlert: Boolean,
    val blockSizeMeters: Double,
    val totalFatigueGrade: Int,
    val hairpins: List<Double>,
    val pois: List<Poi>,
    val curvatureOffsets: List<Float> = emptyList(),
    val riderProgress: Float = 0.0f
)

class AltimetriaStrategyCalculator {

    var currentLatitude = 0.0
    var currentLongitude = 0.0
    var currentSpeed = 0.0
    var currentElevation = 350.0

    var isNavigatingRoute = false
    var routePoints: List<RoutePoint> = emptyList()
    
    // Historial y buffer de elevación barométrica en vivo
    private val liveElevationHistory = mutableListOf<Double>()
    private var lastRecordedElevation = 0.0
    private var liveDistanceAccumulated = 0.0
    
    // Curvas de herradura (distancias absolutas detectadas)
    private val absoluteHairpins = mutableListOf<Double>()

    init {
        // Cargar simulación de ruta de montaña GPX por defecto para pruebas
        simulateRoute()
    }

    fun updateLiveElevation(elev: Double) {
        if (elev <= 0.0) return
        this.currentElevation = elev
        
        if (!isNavigatingRoute && routePoints.isEmpty()) {
            if (lastRecordedElevation <= 0.0) {
                lastRecordedElevation = elev
            }
            if (liveElevationHistory.size < 500) {
                liveElevationHistory.add(elev)
            } else {
                liveElevationHistory.removeAt(0)
                liveElevationHistory.add(elev)
            }
        }
    }

    fun updateCurrentLocation(lat: Double, lng: Double) {
        if (lat == 0.0 && lng == 0.0) return
        this.currentLatitude = lat
        this.currentLongitude = lng
    }

    fun simulateRoute() {
        val simulatedPolylinePoints = mutableListOf<RoutePoint>()
        var accDist = 0.0
        var lat = 38.7831 // Coordenadas del Puerto Miserat-Xillibre
        var lng = -0.2114
        var elev = 350.0

        // Generar 100 puntos GPS con giros cerrados (herraduras), ascensos y trazado sinuoso
        for (i in 0..100) {
            val distStep = 85.0
            accDist += distStep

            val headingAngle = Math.toRadians((sin(i * 0.4) * 140.0) + (if (i % 12 == 0) 160.0 else 0.0))
            lat += Math.cos(headingAngle) * 0.00075
            lng += Math.sin(headingAngle) * 0.00075

            val gradeFactor = when (i) {
                in 0..15 -> 0.05.toFloat()   // 5% inicio
                in 16..30 -> 0.128.toFloat() // 12.8% rampa dura
                in 31..45 -> 0.010.toFloat() // 1.0% descansillo
                in 46..65 -> 0.085.toFloat() // 8.5%
                in 66..80 -> 0.142.toFloat() // 14.2% paredón
                else -> 0.04.toFloat()       // 4% final
            }

            elev += distStep * gradeFactor
            simulatedPolylinePoints.add(RoutePoint(lat, lng, elev, accDist))
        }

        this.routePoints = simulatedPolylinePoints
        this.isNavigatingRoute = true
        this.currentLatitude = simulatedPolylinePoints.first().latitude
        this.currentLongitude = simulatedPolylinePoints.first().longitude
        this.currentElevation = simulatedPolylinePoints.first().elevation

        // Detectar curvas de herradura reales mediante análisis del trazado GPS (vectorial)
        detectHairpins()
    }

    fun setRouteFromPolyline(polyline: String) {
        val points = decodePolyline(polyline)
        if (points.isEmpty()) return

        var accumulatedDist = 0.0
        val result = mutableListOf<RoutePoint>()

        for (i in points.indices) {
            val pt = points[i]
            if (i > 0) {
                val prev = points[i - 1]
                val d = hypot(pt.first - prev.first, pt.second - prev.second) * 111000.0
                accumulatedDist += d
            }
            val microRelief = (sin(accumulatedDist / 80.0) * 12.0) + (sin(accumulatedDist / 250.0) * 35.0)
            val approxElev = 100.0 + microRelief + (accumulatedDist / 110.0)
            result.add(RoutePoint(pt.first, pt.second, approxElev, accumulatedDist))
        }

        this.routePoints = result
        this.isNavigatingRoute = true
        
        detectHairpins()
    }

    private fun detectHairpins() {
        absoluteHairpins.clear()
        if (routePoints.size < 3) return
        
        var lastP = routePoints[0]
        for (i in 1 until routePoints.size - 1) {
            val p1 = routePoints[i]
            if (p1.distance - lastP.distance < 15.0) continue
            
            var p2 = routePoints[i+1]
            for (j in i+1 until routePoints.size) {
                if (routePoints[j].distance - p1.distance >= 15.0) {
                    p2 = routePoints[j]
                    break
                }
            }
            
            val b1 = bearing(lastP.latitude, lastP.longitude, p1.latitude, p1.longitude)
            val b2 = bearing(p1.latitude, p1.longitude, p2.latitude, p2.longitude)
            
            var diff = abs(b2 - b1)
            if (diff > 180.0) diff = 360.0 - diff
            
            if (diff > 130.0) {
                if (absoluteHairpins.isEmpty() || (p1.distance - absoluteHairpins.last() > 50.0)) {
                    absoluteHairpins.add(p1.distance)
                }
            }
            lastP = p1
        }
    }

    private fun bearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)
        val deltaLon = Math.toRadians(lon2 - lon1)

        val y = Math.sin(deltaLon) * Math.cos(lat2Rad)
        val x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLon)

        val brng = Math.toDegrees(Math.atan2(y, x))
        return (brng + 360.0) % 360.0
    }

    fun clearRoute() {
        this.routePoints = emptyList()
        this.absoluteHairpins.clear()
        this.isNavigatingRoute = false
    }

    fun calculateStrategy(context: Context? = null): StrategyData {
        val prefs = context?.let { AppPreferences.getInstance(it) }

        val blockSize = prefs?.blockSizeMeters ?: 100.0
        val lookaheadDist = (prefs?.lookaheadMeters3d ?: 350).toDouble()
        val thresholdAttack = prefs?.thresholdAttackPct ?: 10.0
        val attackAlertsEnabled = prefs?.attackAlertEnabled ?: true
        val asphaltFactor = prefs?.asphaltFactor ?: 0.5
        val useTopographicCalculation = prefs?.useTopographicCalculation ?: false

        if (routePoints.isEmpty()) {
            simulateRoute()
        }

        // MODO NAVEGANDO RUTA PRECARGADA / SIMULADA (GPX / FIT):
        // Encuentra la posición GPS exacta del ciclista en el trazado y calcula el avance en vivo
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
        
        val avgGradeRemaining = if (totalDistanceRemaining > 0) {
            if (useTopographicCalculation) {
                val distRealSq = totalDistanceRemaining * totalDistanceRemaining
                val elevSq = elevationGainRemaining * elevationGainRemaining
                val horizontalDist = if (distRealSq > elevSq) sqrt(distRealSq - elevSq) else totalDistanceRemaining
                (elevationGainRemaining / horizontalDist) * 100.0
            } else {
                (elevationGainRemaining / totalDistanceRemaining) * 100.0
            }
        } else {
            0.0
        }
        
        val secondsRemaining = if (currentSpeed > 0.1) (totalDistanceRemaining / currentSpeed).toLong() else 1620L

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
                val distReal = distanceDiff.coerceAtLeast(1.0)
                val elevDiff = point.elevation - currentBlockStartElevation
                
                val grade = if (useTopographicCalculation) {
                    val distRealSq = distReal * distReal
                    val elevSq = elevDiff * elevDiff
                    val horizontalDist = if (distRealSq > elevSq) sqrt(distRealSq - elevSq) else distReal
                    (elevDiff / horizontalDist) * 100.0
                } else {
                    (elevDiff / distReal) * 100.0
                }
                
                val distanceKm = distReal / 1000.0

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

        if (nextBlocks.size < 10 && routePoints.isNotEmpty() && currentBlockStartDistance < routePoints.last().distance) {
            val remainingDist = (routePoints.last().distance - currentBlockStartDistance).coerceAtLeast(1.0)
            if (remainingDist > 10.0) {
                val elevDiff = routePoints.last().elevation - currentBlockStartElevation
                val lastGrade = if (useTopographicCalculation) {
                    val distRealSq = remainingDist * remainingDist
                    val elevSq = elevDiff * elevDiff
                    val horizontalDist = if (distRealSq > elevSq) sqrt(distRealSq - elevSq) else remainingDist
                    (elevDiff / horizontalDist) * 100.0
                } else {
                    (elevDiff / remainingDist) * 100.0
                }
                
                nextBlocks.add(lastGrade.toFloat())
                if (attackAlertsEnabled && lastGrade > thresholdAttack) {
                    attack = true
                }
            }
        }

        val totalGf = FatigueGradeCalculator.calculateTotalFatigueGrade(
            totalHardness = accumulatedHardness,
            asphaltFactor = asphaltFactor,
            maxRampPct = maxRampPct
        ).roundToInt()

        ClimbStateManager.updateApm(totalGf)
        
        val visibleHairpins = absoluteHairpins
            .map { it - routePoints[nearestIndex].distance }
            .filter { it in 0.0..lookaheadDist }

        val showPoiTowns = prefs?.showPoiTowns ?: true
        val showPoiWater = prefs?.showPoiWater ?: true
        val showPoiViewpoints = prefs?.showPoiViewpoints ?: true
        val showPoiSummits = prefs?.showPoiSummits ?: true

        val simulatedPois = mutableListOf<Poi>()
        val startDist = routePoints[nearestIndex].distance
        
        if (showPoiTowns) simulatedPois.add(Poi(1200.0 - startDist, "🏘️", "Vall d'Ebo", PoiType.TOWN))
        if (showPoiWater) simulatedPois.add(Poi(3500.0 - startDist, "💧", "Font de la Bici", PoiType.WATER))
        if (showPoiViewpoints) simulatedPois.add(Poi(4200.0 - startDist, "📸", "Mirador del Valle", PoiType.VIEWPOINT))
        if (showPoiSummits) simulatedPois.add(Poi(8500.0 - startDist, "📡", "Miserat-Xillibre", PoiType.SUMMIT))

        val visiblePois = simulatedPois.filter { it.relativeDistance in 0.0..lookaheadDist }

        // Cálculo de curvatura real de la carretera GPS por orientación vectorial
        val curvatureOffsets = mutableListOf<Float>()
        if (routePoints.size > nearestIndex + 1) {
            val startP = routePoints[nearestIndex]
            var initialBearing = 0.0
            if (nearestIndex < routePoints.size - 1) {
                val p1 = routePoints[nearestIndex + 1]
                initialBearing = bearing(startP.latitude, startP.longitude, p1.latitude, p1.longitude)
            }

            var cumOffset = 0.0
            curvatureOffsets.add(0.0f)

            for (i in nearestIndex + 1 until routePoints.size) {
                val prevP = routePoints[i - 1]
                val currP = routePoints[i]
                val segDist = (currP.distance - prevP.distance).coerceAtLeast(1.0)

                val curBearing = bearing(prevP.latitude, prevP.longitude, currP.latitude, currP.longitude)
                var angleDiff = curBearing - initialBearing
                while (angleDiff > 180.0) angleDiff -= 360.0
                while (angleDiff < -180.0) angleDiff += 360.0

                val lateralMeters = sin(Math.toRadians(angleDiff)) * (segDist / 20.0)
                cumOffset += lateralMeters
                curvatureOffsets.add(cumOffset.coerceIn(-1.5, 1.5).toFloat())
            }
        }

        // Progreso del ciclista a lo largo de la ventana de anticipación en ruta
        val currentDistOnRoute = routePoints[nearestIndex].distance
        val routeProgressInWindow = ((currentDistOnRoute % lookaheadDist) / lookaheadDist).coerceIn(0.0, 1.0).toFloat()

        return StrategyData(
            remainingDistance = totalDistanceRemaining,
            timeToSummit = secondsRemaining,
            avgGrade = avgGradeRemaining,
            nextBlocks = nextBlocks,
            attackAlert = attack,
            blockSizeMeters = blockSize,
            totalFatigueGrade = totalGf,
            hairpins = visibleHairpins,
            pois = visiblePois,
            curvatureOffsets = curvatureOffsets,
            riderProgress = routeProgressInWindow
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

    private fun decodePolyline(encoded: String): List<Pair<Double, Double>> {
        val poly = ArrayList<Pair<Double, Double>>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val pLat = lat.toDouble() / 1E5
            val pLng = lng.toDouble() / 1E5
            poly.add(Pair(pLat, pLng))
        }
        return poly
    }
}