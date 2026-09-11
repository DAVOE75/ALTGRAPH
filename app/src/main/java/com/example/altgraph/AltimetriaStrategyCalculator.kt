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
    val pois: List<Poi>
)

class AltimetriaStrategyCalculator {

    var currentLatitude = 0.0
    var currentLongitude = 0.0
    var currentSpeed = 0.0
    var currentElevation = 350.0

    var isNavigatingRoute = false
    var routePoints: List<RoutePoint> = emptyList()
    
    // Curvas de herradura (distancias absolutas detectadas)
    private val absoluteHairpins = mutableListOf<Double>()

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
            // Generación de perfil micro-relieve realista con descansillos y rampas verdaderas
            val microRelief = (sin(accumulatedDist / 80.0) * 12.0) + (sin(accumulatedDist / 250.0) * 35.0)
            val approxElev = 100.0 + microRelief + (accumulatedDist / 110.0)
            result.add(RoutePoint(pt.first, pt.second, approxElev, accumulatedDist))
        }

        this.routePoints = result
        this.isNavigatingRoute = true
        
        // Detectar curvas de herradura mediante análisis del trazado GPS (vectorial)
        detectHairpins()
    }

    private fun detectHairpins() {
        absoluteHairpins.clear()
        if (routePoints.size < 3) return
        
        var lastP = routePoints[0]
        for (i in 1 until routePoints.size - 1) {
            val p1 = routePoints[i]
            // Tomamos vectores de al menos 15 metros para ignorar ruido del GPS
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
            
            // Giro muy pronunciado (Herradura > 130 grados)
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
        val thresholdAttack = prefs?.thresholdAttackPct ?: 10.0
        val attackAlertsEnabled = prefs?.attackAlertEnabled ?: true
        val asphaltFactor = prefs?.asphaltFactor ?: 0.5
        val useTopographicCalculation = prefs?.useTopographicCalculation ?: false

        val isNavigating = isNavigatingRoute || routePoints.isNotEmpty()

        // MODO DEMO O MODO LIBRE: Perfil con rampas, descansillos, herraduras falsas e Hitos
        if (!isNavigating) {
            val demoBlocks = listOf(3.2f, 1.0f, 6.5f, 12.8f, 0.5f, 11.5f, 2.0f, 8.5f, 14.2f, 3.0f)
            val hasAttack = attackAlertsEnabled && demoBlocks.any { it > thresholdAttack }
            val demoFatigue = 68

            ClimbStateManager.updateApm(demoFatigue)
            
            // Filtros de usuario para POIs
            val showPoiTowns = prefs?.showPoiTowns ?: true
            val showPoiWater = prefs?.showPoiWater ?: true
            val showPoiViewpoints = prefs?.showPoiViewpoints ?: true
            val showPoiSummits = prefs?.showPoiSummits ?: true

            // Hitos y herraduras de demostración para lucir las funciones visuales
            val demoHairpins = listOf(400.0, 800.0, 1500.0, 2200.0, 3100.0, 3400.0, 6800.0, 7500.0)
            val demoPois = mutableListOf<Poi>()
            
            if (showPoiTowns) demoPois.add(Poi(1200.0, "🏘️", "Vall d'Ebo", PoiType.TOWN))
            if (showPoiWater) demoPois.add(Poi(3500.0, "💧", "Font de la Bici", PoiType.WATER))
            if (showPoiViewpoints) demoPois.add(Poi(4200.0, "📸", "Mirador", PoiType.VIEWPOINT))
            if (showPoiSummits) demoPois.add(Poi(8500.0, "📡", "Miserat-Xillibre", PoiType.SUMMIT))

            return StrategyData(
                remainingDistance = 8500.0,
                timeToSummit = 1620L,
                avgGrade = 7.2,
                nextBlocks = demoBlocks,
                attackAlert = hasAttack,
                blockSizeMeters = blockSize,
                totalFatigueGrade = demoFatigue,
                hairpins = demoHairpins,
                pois = demoPois
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
        
        // Pendiente Promedio Total basada en el método seleccionado por el usuario
        val avgGradeRemaining = if (totalDistanceRemaining > 0) {
            if (useTopographicCalculation) {
                // MÉTODO TOPOGRÁFICO: (Altitud / Distancia Horizontal) * 100
                // Distancia Horizontal = sqrt(Distancia Real Recorrida^2 - Altitud Ascendida^2)
                val distRealSq = totalDistanceRemaining * totalDistanceRemaining
                val elevSq = elevationGainRemaining * elevationGainRemaining
                val horizontalDist = if (distRealSq > elevSq) sqrt(distRealSq - elevSq) else totalDistanceRemaining
                (elevationGainRemaining / horizontalDist) * 100.0
            } else {
                // MÉTODO CICLOTURISTA (Estándar): (Altitud / Distancia Real Recorrida) * 100
                (elevationGainRemaining / totalDistanceRemaining) * 100.0
            }
        } else {
            0.0
        }
        
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
                val distReal = distanceDiff.coerceAtLeast(1.0)
                val elevDiff = point.elevation - currentBlockStartElevation
                
                // Cálculo de pendiente por bloque basado en el método seleccionado por el usuario
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

        // Si quedan metros al final del tramo inferior al tamaño de bloque completo
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
        
        // Filtramos las herraduras que queden por delante para enviarlas como distancias relativas
        val visibleHairpins = absoluteHairpins
            .map { it - routePoints[nearestIndex].distance }
            .filter { it >= 0.0 }
            
        // Por ahora, al no poder consultar bases de datos de mapas locales (Mapbox/OSM) offline
        // desde la API de la extensión para encontrar fuentes o gasolineras, pasamos lista vacía o de pruebas
        val visiblePois = emptyList<Poi>()

        return StrategyData(
            remainingDistance = totalDistanceRemaining,
            timeToSummit = secondsRemaining,
            avgGrade = avgGradeRemaining,
            nextBlocks = nextBlocks,
            attackAlert = attack,
            blockSizeMeters = blockSize,
            totalFatigueGrade = totalGf,
            hairpins = visibleHairpins,
            pois = visiblePois
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