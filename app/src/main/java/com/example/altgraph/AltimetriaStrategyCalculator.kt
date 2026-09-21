package com.example.altgraph

import android.content.Context
import io.hammerhead.karooext.models.Symbol
import android.location.Location
import android.util.Log
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

data class RouteClimb(
    val startDistance: Double,
    val endDistance: Double,
    val length: Double,
    val totalElevation: Double,
    val avgGrade: Double,
    var apm: Double = 0.0,
    var category: String = ""
)

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
    val riderProgress: Float = 0.0f,
    val windowStartMeters: Double = 0.0,
    val windowStartElevation: Double = 0.0,
    val subBlocks: List<Float> = emptyList(),
    val subBlockSizeMeters: Double = 50.0,
    val majorBlockSizeMeters: Double = 100.0,
    val profileElevations: List<Float> = emptyList(),
    val activeClimbs: List<RouteClimb> = emptyList(),
    val visibleAvgGrade: Double = 0.0,
    val visibleMaxGrade: Double = 0.0,
    val routeName: String? = null
)

class AltimetriaStrategyCalculator {

    var currentLatitude = 0.0
    var currentLongitude = 0.0
    var currentSpeed = 0.0
    var currentElevation = 350.0

    var nearestIndex = 0
    var isNavigatingRoute = false
    var activeRouteName: String? = null
    var routePoints: List<RoutePoint> = emptyList()

    // Historial y buffer de altitud barométrica instantánea en tiempo real
    private val liveElevationHistory = mutableListOf<Double>()
    private var lastElevationSample = 350.0
    private var liveDistanceAccumulated = 0.0
    private val freeRideHistory = mutableListOf<Pair<Double, Double>>() // Distancia, Elevación
    var instantBarometricGrade = 0.0
    
    private fun getFreeRideElevationAt(dist: Double): Double {
        if (freeRideHistory.isEmpty()) return currentElevation
        if (dist <= freeRideHistory.first().first) return freeRideHistory.first().second
        if (dist >= freeRideHistory.last().first) return freeRideHistory.last().second

        var low = 0
        var high = freeRideHistory.size - 1
        while (low <= high) {
            val mid = (low + high) ushr 1
            val p = freeRideHistory[mid]
            if (p.first < dist) low = mid + 1
            else if (p.first > dist) high = mid - 1
            else return p.second
        }
        val p1 = freeRideHistory[low - 1]
        val p2 = freeRideHistory[low]
        val t = (dist - p1.first) / (p2.first - p1.first).coerceAtLeast(0.01)
        return p1.second + t * (p2.second - p1.second)
    }

    // ── Datos del Climber nativo de Karoo (DISTANCE_TO_TOP, ELEVATION_TO_TOP, …) ──
    // El SDK expone estos valores calculados directamente desde el archivo de ruta (GPX/FIT).
    // Son la fuente de verdad para la altimetría real del climb activo.
    var distanceToTop: Double = 0.0      // metros hasta la cima del climb actual
    var elevationToTop: Double = 0.0     // metros de desnivel hasta la cima
    var distanceFromBottom: Double = 0.0 // metros desde la base del climb
    var elevationFromBottom: Double = 0.0 // metros de desnivel desde la base
    var elevationRemaining: Double = 0.0 // desnivel restante total de la ruta
    
    // Distancia exacta reportada por Karoo en la ruta
    var currentRouteDistance: Double = 0.0

    // Distancia exacta restante reportada por Karoo en la ruta (útil si no hay puntos GPS)
    var fallbackRemainingDistance: Double = 0.0

    // GPX Elevation Profile Completo (SDK 1.1.7+)
    var routeElevationProfile: List<ElevationPolylineDecoder.ElevationPoint> = emptyList()

    // Caches para evitar decodificación intensiva en cada emisión del SDK
    private var lastRoutePolyline: String = ""
    private var lastElevationPolyline: String = ""

    // Lista de puertos (climbs) de la ruta para marcar Mountain Gates en el 3D
    var routeClimbs: List<RouteClimb> = emptyList()
    private var routeKeyForClimbs: String? = null

    // Curvas de herradura (distancias absolutas detectadas)
    private val absoluteHairpins = mutableListOf<Double>()
    private val routePois = mutableListOf<Poi>()

    fun updateLiveGrade(grade: Double) {
        this.instantBarometricGrade = grade
    }

    /**
     * Actualiza los datos del climb activo recibidos de los streams nativos del SDK:
     * DISTANCE_TO_TOP, ELEVATION_TO_TOP, DISTANCE_FROM_BOTTOM, ELEVATION_FROM_BOTTOM.
     * Estos reflejan la altimetría real del archivo de ruta cargado (GPX/FIT),
     * exactamente como lo hace el módulo Climber de Hammerhead internamente.
     */
    fun updateClimbData(
        distToTop: Double = distanceToTop,
        elevToTop: Double = elevationToTop,
        distFromBottom: Double = distanceFromBottom,
        elevFromBottom: Double = elevationFromBottom,
        elevRemaining: Double = elevationRemaining
    ) {
        if (distToTop >= 0.0) this.distanceToTop = distToTop
        if (elevToTop >= 0.0) this.elevationToTop = elevToTop
        if (distFromBottom >= 0.0) this.distanceFromBottom = distFromBottom
        if (elevFromBottom >= 0.0) this.elevationFromBottom = elevFromBottom
        if (elevRemaining >= 0.0) this.elevationRemaining = elevRemaining
    }

    fun updateLiveElevation(elev: Double) {
        if (elev <= 0.0) return

        // Calcular la pendiente barométrica instantánea real (deltaE / deltaD)
        if (lastElevationSample > 0.0 && currentSpeed > 0.1) {
            val deltaE = elev - lastElevationSample
            val deltaD = (currentSpeed * 1.0).coerceAtLeast(0.5) // Medido cada segundo
            val calcGrade = (deltaE / deltaD) * 100.0

            // Suavizado por media móvil exponencial (EMA) para eliminar ruido del altímetro
            instantBarometricGrade = (0.3 * calcGrade) + (0.7 * instantBarometricGrade)
        }
        lastElevationSample = elev
        this.currentElevation = elev

        if (!isNavigatingRoute && routePoints.isEmpty()) {
            if (liveElevationHistory.size < 20) {
                liveElevationHistory.add(elev)
            } else {
                liveElevationHistory.removeAt(0)
                liveElevationHistory.add(elev)
            }
        }

        // En modo ruta: recalibrar el perfil con la altitud barométrica real
        if (isNavigatingRoute && routePoints.isNotEmpty()) {
            recalibrateElevationProfile(elev)
        }
    }

    fun getSubBlockSize(lookaheadDist: Double): Double {
        return when {
            lookaheadDist <= 500.0 -> 50.0
            lookaheadDist <= 5000.0 -> 100.0
            lookaheadDist <= 20000.0 -> 500.0
            lookaheadDist <= 50000.0 -> 1000.0
            lookaheadDist <= 100000.0 -> 1000.0
            lookaheadDist <= 200000.0 -> 1000.0
            else -> 1000.0
        }
    }

    fun getMajorBlockSize(lookaheadDist: Double): Double {
        return when {
            lookaheadDist <= 500.0 -> 100.0
            lookaheadDist <= 5000.0 -> 500.0
            lookaheadDist <= 20000.0 -> 2000.0
            lookaheadDist <= 50000.0 -> 5000.0
            lookaheadDist <= 100000.0 -> 10000.0
            lookaheadDist <= 200000.0 -> 20000.0
            else -> 20000.0
        }
    }

    fun getElevationAtDistance(dist: Double): Double {
        if (routeElevationProfile.isNotEmpty()) {
            return getTrueElevationAtDistance(dist)
        }
        
        if (routePoints.isEmpty()) return currentElevation
        if (dist <= routePoints.first().distance) return routePoints.first().elevation
        if (dist >= routePoints.last().distance) return routePoints.last().elevation

        var low = 0
        var high = routePoints.size - 1
        while (low <= high) {
            val mid = (low + high).ushr(1)
            val p = routePoints[mid]
            if (p.distance < dist) {
                low = mid + 1
            } else if (p.distance > dist) {
                high = mid - 1
            } else {
                return p.elevation
            }
        }
        val p0 = routePoints[high.coerceIn(0, routePoints.size - 1)]
        val p1 = routePoints[low.coerceIn(0, routePoints.size - 1)]
        val span = (p1.distance - p0.distance).coerceAtLeast(0.01)
        val frac = ((dist - p0.distance) / span).coerceIn(0.0, 1.0)
        return p0.elevation + frac * (p1.elevation - p0.elevation)
    }

    fun updateCurrentLocation(lat: Double, lng: Double) {
        if (lat == 0.0 && lng == 0.0) return
        this.currentLatitude = lat
        this.currentLongitude = lng
    }

    fun setRoutePois(symbols: List<io.hammerhead.karooext.models.Symbol.POI>) {
        if (routePoints.isEmpty() || symbols.isEmpty()) {
            this.routePois.clear()
            return
        }
        routePois.clear()
        for (sym in symbols) {
            var nearestDist = 0.0
            var minDist = Double.MAX_VALUE
            for (pt in routePoints) {
                val d = hypot(pt.latitude - sym.lat, pt.longitude - sym.lng)
                if (d < minDist) {
                    minDist = d
                    nearestDist = pt.distance
                }
            }
            var name = sym.name ?: "Hito"
            // Limpiamos cualquier "XXX m" previo y le añadimos la altitud real
            name = name.replace(Regex("\\b\\d+\\s*m\\b", RegexOption.IGNORE_CASE), "").trim()
            val realElevation = getTrueElevationAtDistance(nearestDist).toInt()
            name = "$name ${realElevation}m"
            val icon = when {
                name.contains("agua", ignoreCase = true) || name.contains("font", ignoreCase = true) || name.contains("fuente", ignoreCase = true) -> "💧"
                name.contains("mirador", ignoreCase = true) || name.contains("vista", ignoreCase = true) -> "📸"
                name.contains("puerto", ignoreCase = true) || name.contains("cima", ignoreCase = true) || name.contains("alto", ignoreCase = true) || name.contains("col", ignoreCase = true) -> "📡"
                else -> "☕"
            }
            val type = when (icon) {
                "💧" -> PoiType.WATER
                "📸" -> PoiType.VIEWPOINT
                "📡" -> PoiType.SUMMIT
                else -> PoiType.TOWN
            }
            routePois.add(Poi(nearestDist, icon, name, type))
        }
    }

    /**
     * Sincroniza la lista de climbs. Como Karoo puede borrar los climbs superados de la lista,
     * hacemos un merge para mantenerlos (y poder verlos al hacer re-ride).
     * Además, cataloga los puertos calculando su APM.
     */
    fun syncRouteClimbs(routeKey: String, incoming: List<RouteClimb>) {
        if (routeKeyForClimbs != routeKey) {
            routeKeyForClimbs = routeKey
            if (incoming.isNotEmpty()) {
                routeClimbs = incoming.map { calculateClimbCategory(it) }
            }
        } else if (incoming.isNotEmpty()) {
            val currentStartDists = routeClimbs.map { it.startDistance }.toSet()
            val newClimbs = incoming.filter { it.startDistance !in currentStartDists }.map { calculateClimbCategory(it) }
            if (newClimbs.isNotEmpty()) {
                routeClimbs = (routeClimbs + newClimbs).sortedBy { it.startDistance }
            }
        }
    }

    private fun calculateClimbCategory(climb: RouteClimb): RouteClimb {
        var accumulatedHardness = 0.0
        var maxGrade = 0.0
        val defaultAsphaltFactor = 0.5 // Standard TA

        if (routeElevationProfile.isNotEmpty()) {
            val points = routeElevationProfile.filter { it.distance in climb.startDistance..climb.endDistance }
            if (points.size >= 2) {
                var lastPt = points.first()
                for (i in 1 until points.size) {
                    val pt = points[i]
                    val dDist = pt.distance - lastPt.distance
                    if (dDist > 5.0) {
                        val dElev = pt.elevation - lastPt.elevation
                        val rawGrade = (dElev / dDist) * 100.0
                        val grade = rawGrade.coerceAtMost(35.0) // Cap anomalies at 35%
                        if (grade > maxGrade) maxGrade = grade
                        if (grade > 0.0) {
                            accumulatedHardness += FatigueGradeCalculator.calculateSegmentHardness(grade, dDist / 1000.0)
                        }
                    }
                    lastPt = pt
                }
            }
        }
        
        val apm = FatigueGradeCalculator.calculateTotalFatigueGrade(accumulatedHardness, defaultAsphaltFactor, maxGrade)
        val category = FatigueGradeCalculator.getClimbCategoryName(apm)
        
        return climb.copy(apm = apm, category = category)
    }

    fun setRouteElevationProfile(encoded: String?) {
        val safeEncoded = encoded ?: ""
        if (safeEncoded == lastElevationPolyline) return
        lastElevationPolyline = safeEncoded
        
        Log.e("AltiCalc", "setRouteElevationProfile called, encoded length=${encoded?.length}")
        val expectedLength = routePoints.lastOrNull()?.distance ?: 0.0
        val result = ElevationPolylineDecoder.decodeSafe(encoded, expectedLength)
        if (result is ElevationPolylineDecoder.DecodeResult.Success) {
            this.routeElevationProfile = ElevationPolylineDecoder.smooth(result.points)
            Log.e("AltiCalc", "Route elevation decoded successfully, points=${routeElevationProfile.size}")
            applyTrueElevationsToRoutePoints()
            detectCustomClimbsFromProfile()
        } else {
            Log.e("AltiCalc", "Route elevation decode failed!")
            this.routeElevationProfile = emptyList()
        }
    }

    private fun detectCustomClimbsFromProfile() {
        if (routeElevationProfile.isEmpty()) return
        
        val customClimbs = mutableListOf<RouteClimb>()
        
        val minClimbDistance = 500.0
        val minClimbElevation = 25.0
        
        var climbStartIndex = -1
        var localMaxIndex = -1
        
        var i = 0
        while (i < routeElevationProfile.size - 1) {
            val pt = routeElevationProfile[i]
            
            if (climbStartIndex == -1) {
                // Look for a solid start (next 200m averages >= 2.5%)
                val lookaheadDist = 200.0
                var j = i + 1
                while (j < routeElevationProfile.size && routeElevationProfile[j].distance - pt.distance < lookaheadDist) {
                    j++
                }
                if (j < routeElevationProfile.size) {
                    val endPt = routeElevationProfile[j]
                    val dE = endPt.elevation - pt.elevation
                    val dD = endPt.distance - pt.distance
                    val grade = if (dD > 0) (dE / dD) * 100.0 else 0.0
                    
                    if (grade >= 2.5) {
                        climbStartIndex = i
                        localMaxIndex = i
                    } else {
                        // Skip forward to avoid micro-checking flats
                        i = (i + 5).coerceAtMost(routeElevationProfile.size - 2)
                        continue
                    }
                } else {
                    break
                }
            } else {
                if (pt.elevation > routeElevationProfile[localMaxIndex].elevation) {
                    localMaxIndex = i
                }
                
                val drop = routeElevationProfile[localMaxIndex].elevation - pt.elevation
                val distanceSinceMax = pt.distance - routeElevationProfile[localMaxIndex].distance
                val isFlat = distanceSinceMax > 500.0 // 500m without establishing a new peak
                val isEndOfRoute = i == routeElevationProfile.size - 2
                
                if (drop > 20.0 || isFlat || isEndOfRoute) {
                    val startPt = routeElevationProfile[climbStartIndex]
                    val maxPt = routeElevationProfile[localMaxIndex]
                    
                    val climbDist = maxPt.distance - startPt.distance
                    val climbElev = maxPt.elevation - startPt.elevation
                    
                    if (climbDist >= 1000.0) {
                        val avgGrade = (climbElev / climbDist) * 100.0
                        
                        val isStandardClimb = climbDist >= 3000.0 && avgGrade >= 3.0
                        val isMuroClimb = climbDist >= 1000.0 && avgGrade >= 12.0
                        
                        if (isStandardClimb || isMuroClimb) {
                            val potentialClimb = RouteClimb(
                                startDistance = startPt.distance,
                                endDistance = maxPt.distance,
                                length = climbDist,
                                totalElevation = climbElev,
                                avgGrade = avgGrade
                            )
                            val categorizedClimb = calculateClimbCategory(potentialClimb)
                            customClimbs.add(categorizedClimb)
                        }
                    }
                    
                    i = localMaxIndex
                    climbStartIndex = -1
                    localMaxIndex = -1
                }
            }
            i++
        }
        
        if (customClimbs.isNotEmpty() && this.routeClimbs.isEmpty()) {
            this.routeClimbs = customClimbs.sortedBy { it.startDistance }
            Log.e("AltiCalc", "Detected ${customClimbs.size} custom climbs!")
        }
    }

    private fun applyTrueElevationsToRoutePoints() {
        if (routePoints.isEmpty() || routeElevationProfile.isEmpty()) return

        val newPoints = routePoints.map { pt ->
            // Interpolate true elevation from routeElevationProfile based on distance
            val trueElev = getTrueElevationAtDistance(pt.distance)
            pt.copy(elevation = trueElev)
        }
        this.routePoints = newPoints
    }

    private fun getTrueElevationAtDistance(dist: Double): Double {
        if (routeElevationProfile.isEmpty()) return 0.0
        if (dist <= routeElevationProfile.first().distance) return routeElevationProfile.first().elevation
        if (dist >= routeElevationProfile.last().distance) return routeElevationProfile.last().elevation

        var low = 0
        var high = routeElevationProfile.size - 1

        while (low <= high) {
            val mid = (low + high) ushr 1
            val midVal = routeElevationProfile[mid].distance
            if (midVal < dist) low = mid + 1
            else if (midVal > dist) high = mid - 1
            else return routeElevationProfile[mid].elevation
        }

        if (low == 0) return routeElevationProfile.first().elevation
        if (low >= routeElevationProfile.size) return routeElevationProfile.last().elevation

        val p1 = routeElevationProfile[low - 1]
        val p2 = routeElevationProfile[low]

        val t = (dist - p1.distance) / (p2.distance - p1.distance)
        return p1.elevation + t * (p2.elevation - p1.elevation)
    }

    fun setRouteFromPolyline(polyline: String) {
        if (polyline == lastRoutePolyline) return
        lastRoutePolyline = polyline
        
        val points = decodePolyline(polyline)
        if (points.isEmpty()) {
            routePoints = emptyList()
            activeRouteName = null
            lastRoutePolyline = ""
            return
        }

        // Build a list of raw (lat, lng, distAcumulada) first so we can apply real elevation.
        // The Karoo polyline encodes ONLY lat/lng — no altitude. We anchor the profile on the
        // current barometric elevation and accumulate vertical gain from the live grade stream.
        // Any subsequent live elevation update will correct currentElevation in real time.
        var accumulatedDist = 0.0
        val result = mutableListOf<RoutePoint>()

        // First pass: compute accumulated distances using accurate Haversine/Android Location
        val rawDistances = DoubleArray(points.size)
        val results = FloatArray(1)
        for (i in points.indices) {
            if (i > 0) {
                val prev = points[i - 1]
                val pt  = points[i]
                android.location.Location.distanceBetween(prev.first, prev.second, pt.first, pt.second, results)
                accumulatedDist += results[0]
            }
            rawDistances[i] = accumulatedDist
        }

        // Second pass: assign elevation anchored at the current barometric altitude.
        // We project forward using the live grade (m of climb per metre of distance).
        // If we have no live grade yet we assume a flat road — far better than fake sines.
        val anchorElev = if (currentElevation > 0.0) currentElevation else 100.0
        // gradePerMetre: e.g. 8% → 0.08; negative for descents
        val gradePerMetre = instantBarometricGrade / 100.0

        for (i in points.indices) {
            val pt = points[i]
            val dist = rawDistances[i]
            // Simple linear projection from anchor at the rider's current position.
            // The delta distance from dist=0 (start of polyline) could be huge if the
            // rider is already mid-route, so we keep it relative to current position.
            // updateCurrentLocation() + nearestIndex logic corrects this at render time.
            val projectedElev = anchorElev + dist * gradePerMetre
            result.add(RoutePoint(pt.first, pt.second, projectedElev, dist))
        }

        this.routePoints = result
        this.isNavigatingRoute = true
        
        // If we already received the elevation profile, apply it now
        if (routeElevationProfile.isNotEmpty()) {
            applyTrueElevationsToRoutePoints()
        }

        detectHairpins()
    }

    /**
     * Called whenever we get a fresh barometric altitude reading.
     * In route mode we recalibrate the elevation profile so that the point nearest to
     * the rider's current GPS position matches the real barometric altitude, and all
     * surrounding points are shifted by the same delta. This keeps the profile truthful.
     */
    fun recalibrateElevationProfile(realElev: Double) {
        if (routePoints.isEmpty() || realElev <= 0.0) return
        // Find the nearest point to the current GPS position
        var nearestIndex = 0
        var minDist = Double.MAX_VALUE
        routePoints.forEachIndexed { index, point ->
            val d = hypot(point.latitude - currentLatitude, point.longitude - currentLongitude)
            if (d < minDist) { minDist = d; nearestIndex = index }
        }
        val currentProfileElev = routePoints[nearestIndex].elevation
        val delta = realElev - currentProfileElev
        if (abs(delta) < 0.5) return  // negligible correction
        // Shift all points by the same delta (rigid translation keeps relative shape)
        this.routePoints = routePoints.map { it.copy(elevation = it.elevation + delta) }
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
        this.routeElevationProfile = emptyList()
        this.routeClimbs = emptyList()
        this.routeKeyForClimbs = null
        this.absoluteHairpins.clear()
        this.routePois.clear()
        this.isNavigatingRoute = false
        this.activeRouteName = null
        // Resetear datos del climb al salir de la navegación
        this.distanceToTop = 0.0
        this.elevationToTop = 0.0
        this.distanceFromBottom = 0.0
        this.elevationFromBottom = 0.0
        this.elevationRemaining = 0.0
        this.lastRoutePolyline = ""
        this.lastElevationPolyline = ""
    }

    fun calculateStrategy(context: Context? = null): StrategyData {
        val prefs = context?.let { AppPreferences.getInstance(it) }

        val blockSize = prefs?.blockSizeMeters ?: 100.0
        val lookaheadDist = (prefs?.lookaheadMeters3d ?: 350).toDouble()
        val thresholdAttack = prefs?.thresholdAttackPct ?: 10.0
        val attackAlertsEnabled = prefs?.attackAlertEnabled ?: true
        val asphaltFactor = prefs?.asphaltFactor ?: 0.5
        val useTopographicCalculation = prefs?.useTopographicCalculation ?: false

        val isNavigating = isNavigatingRoute && (routePoints.isNotEmpty() || routeElevationProfile.isNotEmpty())

        // MODO LIBRE / ENTRENAMIENTO REAL SIN RUTA PRECARGADA:
        // Genera el perfil 3D con el historial acumulado en tiempo real en lugar de proyectar al futuro
        if (!isNavigating) {
            val baseGrade = instantBarometricGrade
            if (currentSpeed > 0.1) {
                liveDistanceAccumulated += currentSpeed * 1.0 // Medido cada segundo
            }

            // Acumular el punto actual en el historial si avanzamos más de 5 metros
            if (freeRideHistory.isEmpty() || liveDistanceAccumulated - freeRideHistory.last().first >= 5.0) {
                freeRideHistory.add(Pair(liveDistanceAccumulated, currentElevation))
            }

            // La ventana ahora mira hacia atrás desde la posición actual
            val windowStartDist = (liveDistanceAccumulated - lookaheadDist).coerceAtLeast(0.0)
            
            // El ciclista avanza hacia la derecha al empezar, y luego se queda en el borde derecho
            val riderDistInWindow = (liveDistanceAccumulated - windowStartDist).coerceAtLeast(0.0)
            val riderProgress = (riderDistInWindow / lookaheadDist).toFloat().coerceIn(0f, 1f)

            val subBlockSize = getSubBlockSize(lookaheadDist)
            val majorBlockSize = getMajorBlockSize(lookaheadDist)
            val numSubBlocks = (lookaheadDist / subBlockSize).toInt().coerceIn(2, 60)

            val freeSubBlocks = mutableListOf<Float>()
            val freeElevations = mutableListOf<Float>()

            // Remuestrear el historial en los sub-bloques de la ventana
            for (idx in 0..numSubBlocks) {
                val ptDist = windowStartDist + (idx * subBlockSize)
                val ptElev = getFreeRideElevationAt(ptDist)
                freeElevations.add(ptElev.toFloat())
                
                if (idx > 0) {
                    val prevElev = freeElevations[idx - 1]
                    val dE = ptElev - prevElev
                    val dD = subBlockSize
                    val grade = if (dD > 0) (dE / dD) * 100.0 else 0.0
                    freeSubBlocks.add(grade.toFloat())
                }
            }

            val windowStartElevation = freeElevations.first().toDouble()

            val hasAttack = attackAlertsEnabled && freeSubBlocks.any { it > thresholdAttack }
            val liveFatigue = (freeSubBlocks.average() * 8.5 + asphaltFactor * 10).roundToInt().coerceIn(10, 250)

            ClimbStateManager.updateApm(liveFatigue)

            val liveHairpins = emptyList<Double>()
            val livePois = emptyList<Poi>()

            val liveCurvatures = List(50) { idx ->
                val distAlongWindow = idx * (lookaheadDist / 49.0)
                (sin((windowStartDist + distAlongWindow) / 75.0) * 0.75 + sin((windowStartDist + distAlongWindow) / 200.0) * 0.25).toFloat()
            }

            return StrategyData(
                remainingDistance = 0.0,
                timeToSummit = 0L,
                avgGrade = baseGrade,
                nextBlocks = freeSubBlocks.take(10),
                attackAlert = hasAttack,
                blockSizeMeters = subBlockSize,
                totalFatigueGrade = liveFatigue,
                hairpins = liveHairpins,
                pois = livePois,
                curvatureOffsets = liveCurvatures,
                riderProgress = riderProgress,
                windowStartMeters = windowStartDist,
                windowStartElevation = windowStartElevation,
                subBlocks = freeSubBlocks,
                subBlockSizeMeters = subBlockSize,
                majorBlockSizeMeters = majorBlockSize,
                profileElevations = freeElevations,
                activeClimbs = emptyList(),
                routeName = activeRouteName
            )
        }

        // 1. Encuentra el índice más cercano en una ventana local para inicializar la ventana
        if (currentLatitude != 0.0 && currentLongitude != 0.0 && routePoints.isNotEmpty()) {
            var minDistance = Double.MAX_VALUE
            var realMinDistance = Double.MAX_VALUE
            val dists = FloatArray(1)
            
            val searchStart = if (nearestIndex == 0) 0 else (nearestIndex - 20).coerceAtLeast(0)
            val searchEnd = if (nearestIndex == 0) routePoints.size else (nearestIndex + 500).coerceAtMost(routePoints.size)
            
            var newNearestIdx = nearestIndex
            for (i in searchStart until searchEnd) {
                val pt = routePoints[i]
                Location.distanceBetween(currentLatitude, currentLongitude, pt.latitude, pt.longitude, dists)
                val dist = dists[0].toDouble()
                if (dist < minDistance) {
                    minDistance = dist
                    realMinDistance = dist
                    newNearestIdx = i
                }
            }
            nearestIndex = newNearestIdx
            
            // PREVIEW FIX: Si el punto más cercano está a más de 5km, asumimos que está en casa probando.
            if (realMinDistance > 5000.0) {
                nearestIndex = 0
            }
        }

        // 1. Usar la distancia restante reportada por el Karoo para un seguimiento perfecto,
        // pero si aún no está disponible (ej. arranque inicial o rutas sin valhalla), usamos
        // el índice más cercano del escáner geográfico (que ahora tiene protección contra cruces).
        val currentRiderDistance = if (fallbackRemainingDistance > 0.0) {
            val totalLength = routeElevationProfile.lastOrNull()?.distance ?: (routePoints.lastOrNull()?.distance ?: 0.0)
            (totalLength - fallbackRemainingDistance).coerceAtLeast(0.0)
        } else if (routePoints.isNotEmpty()) {
            routePoints[nearestIndex].distance
        } else {
            0.0
        }

        // 2. Ventana deslizante en bloques cuánticos de 50 metros
        val quantumMeters = 50.0
        var windowStartDist = (currentRiderDistance / quantumMeters).toLong() * quantumMeters
        var actualLookahead = lookaheadDist
        
        // PANORAMIC FIX: Si el zoom es de 100km o más (ultra panorámico), forzamos
        // el inicio de la ventana al kilómetro 0 para mostrar la ruta completa, 
        // tal y como se ve en el Hammerhead Dashboard.
        if (lookaheadDist >= 100000.0) {
            windowStartDist = 0.0
            // Si estamos en zoom panorámico, escalar la gráfica a la longitud total de la ruta
            // para que no quede aplastada con una línea plana si la ruta es más corta que el zoom.
            val totalLength = routeElevationProfile.lastOrNull()?.distance ?: (routePoints.lastOrNull()?.distance ?: 0.0)
            if (totalLength > 0 && totalLength < lookaheadDist) {
                actualLookahead = totalLength
            }
        }
        
        val windowEndDist = windowStartDist + actualLookahead

        val riderOffsetInWindow = (currentRiderDistance - windowStartDist).coerceAtLeast(0.0)
        val riderProgress = (riderOffsetInWindow / actualLookahead).toFloat().coerceIn(0f, 1f)

        // 3. Localizar el punto de ruta correspondiente al inicio de la ventana (windowStartDist)
        var windowStartIndex = nearestIndex
        while (windowStartIndex > 0 && routePoints[windowStartIndex].distance > windowStartDist) {
            windowStartIndex--
        }
        while (windowStartIndex < routePoints.size - 1 && routePoints[windowStartIndex + 1].distance <= windowStartDist) {
            windowStartIndex++
        }
        val windowStartElevation = getElevationAtDistance(windowStartDist)

        // 4. Distancia y desnivel restantes
        // Preferimos los datos nativos del SDK (distanceToTop / elevationRemaining) sobre la
        // interpolación polilineal, porque los nativos vienen del archivo GPX/FIT real.
        val totalDistanceRemaining: Double
        val elevationGainRemaining: Double
        if (distanceToTop > 0.0 && elevationToTop > 0.0) {
            // ✅ Modo Climber real: usamos los datos exactos del SDK
            totalDistanceRemaining = distanceToTop
            elevationGainRemaining = elevationToTop
        } else {
            totalDistanceRemaining = if (routePoints.isNotEmpty()) {
                (routePoints.last().distance - currentRiderDistance).coerceAtLeast(0.0)
            } else 0.0
            val endElevation = routePoints.last().elevation
            elevationGainRemaining = (endElevation - currentElevation).coerceAtLeast(0.0)
        }

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

        val secondsRemaining = if (currentSpeed > 0.1) (totalDistanceRemaining / currentSpeed).toLong() else 0L

        // 5. Cálculo de resolución adaptativa según escala (Lookahead)
        val subBlockSize = getSubBlockSize(actualLookahead)
        val majorBlockSize = getMajorBlockSize(actualLookahead)

        val numSubBlocks = (actualLookahead / subBlockSize).roundToInt().coerceIn(2, 2000)
        val routeSubBlocks = mutableListOf<Float>()
        val routeElevations = mutableListOf<Float>()
        routeElevations.add(windowStartElevation.toFloat())

        var attack = false
        var accumulatedHardness = 0.0
        var maxRampPct = 0.0

        // ── Estrategia de construcción del perfil ──────────────────────────────────
        // Si tenemos datos nativos del climb (del SDK, fuente directa del archivo GPX/FIT):
        //   → Usamos la interpolación a lo largo del climb PERO si además tenemos el
        //     PERFIL DE ELEVACIÓN COMPLETO (routeElevationProfile), entonces usamos
        //     esos datos con precisión punto a punto.
        // ──────────────────────────────────────────────────────────────────────────
        val useClimberData = distanceToTop > 50.0 && elevationToTop > 0.5
        val summitElevation = if (useClimberData) currentElevation + elevationToTop else 0.0
        val hasTrueProfile = routeElevationProfile.isNotEmpty()

        for (j in 0 until numSubBlocks) {
            val sDistStart = windowStartDist + (j * subBlockSize)
            val sDistEnd = windowStartDist + ((j + 1) * subBlockSize)

            val sElevStart: Double
            val sElevEnd: Double

            if (hasTrueProfile) {
                // Si tenemos el perfil real, leemos la altitud exacta!
                sElevStart = getElevationAtDistance(sDistStart)
                sElevEnd   = getElevationAtDistance(sDistEnd)
            } else if (useClimberData) {
                // Fallback a interpolación lineal si no hay perfil detallado
                val fracStart = (sDistStart - windowStartDist).coerceAtLeast(0.0) / distanceToTop.coerceAtLeast(1.0)
                val fracEnd   = (sDistEnd   - windowStartDist).coerceAtLeast(0.0) / distanceToTop.coerceAtLeast(1.0)
                sElevStart = currentElevation + fracStart.coerceIn(0.0, 1.0) * elevationToTop
                sElevEnd   = currentElevation + fracEnd.coerceIn(0.0, 1.0)   * elevationToTop
            } else {
                // Fallback a polilínea 2D calibrada barométricamente
                sElevStart = getElevationAtDistance(sDistStart)
                sElevEnd   = getElevationAtDistance(sDistEnd)
            }

            val sElevDiff = sElevEnd - sElevStart
            val sDist = (sDistEnd - sDistStart).coerceAtLeast(1.0)
            val grade = if (useTopographicCalculation) {
                val distRealSq = sDist * sDist
                val elevSq = sElevDiff * sElevDiff
                val horizontalDist = if (distRealSq > elevSq) sqrt(distRealSq - elevSq) else sDist
                (sElevDiff / horizontalDist) * 100.0
            } else {
                (sElevDiff / sDist) * 100.0
            }

            routeSubBlocks.add(grade.toFloat())
            routeElevations.add(sElevEnd.toFloat())
        }

        // Construcción de bloques mayores para telemetría y rótulos
        val numMajorBlocks = (actualLookahead / majorBlockSize).roundToInt().coerceIn(1, 200)
        val routeMajorBlocks = mutableListOf<Float>()
        for (m in 0 until numMajorBlocks) {
            val mDistStart = windowStartDist + (m * majorBlockSize)
            val mDistEnd = windowStartDist + ((m + 1) * majorBlockSize)
            val mElevStart = getElevationAtDistance(mDistStart)
            val mElevEnd = getElevationAtDistance(mDistEnd)
            val mElevDiff = mElevEnd - mElevStart
            val mDist = (mDistEnd - mDistStart).coerceAtLeast(1.0)
            val mGrade = if (useTopographicCalculation) {
                val distRealSq = mDist * mDist
                val elevSq = mElevDiff * mElevDiff
                val horizontalDist = if (distRealSq > elevSq) sqrt(distRealSq - elevSq) else mDist
                (mElevDiff / horizontalDist) * 100.0
            } else {
                (mElevDiff / mDist) * 100.0
            }
            routeMajorBlocks.add(mGrade.toFloat())
        }


        val visibleHairpins = absoluteHairpins
            .map { it - windowStartDist }
            .filter { it in 0.0..actualLookahead }

        val showPoiTowns = prefs?.showPoiTowns ?: true
        val showPoiWater = prefs?.showPoiWater ?: true
        val showPoiViewpoints = prefs?.showPoiViewpoints ?: true
        val showPoiSummits = prefs?.showPoiSummits ?: true

        // Solo mostrar POIs reales de la ruta SDK. Si la ruta no tiene POIs no fabricamos ninguno.
        val rawPois = if (routePois.isNotEmpty()) {
            routePois.map { Poi(it.relativeDistance - windowStartDist, it.icon, it.name, it.type) }
        } else {
            emptyList()
        }

        val visiblePois = rawPois.filter { poi ->
            val matchesCategory = when (poi.type) {
                PoiType.TOWN -> showPoiTowns
                PoiType.WATER -> showPoiWater
                PoiType.VIEWPOINT -> showPoiViewpoints
                PoiType.SUMMIT -> showPoiSummits
            }
            matchesCategory && poi.relativeDistance in 0.0..actualLookahead
        }

        // Cálculo de curvatura real de la carretera GPS por orientación vectorial muestreado a 50 puntos
        val rawOffsets = mutableListOf<Pair<Double, Double>>()
        rawOffsets.add(Pair(windowStartDist, 0.0))

        if (routePoints.size > windowStartIndex + 1) {
            val startP = routePoints[windowStartIndex]
            var initialBearing = 0.0
            if (windowStartIndex < routePoints.size - 1) {
                val p1 = routePoints[windowStartIndex + 1]
                initialBearing = bearing(startP.latitude, startP.longitude, p1.latitude, p1.longitude)
            }

            var cumOffset = 0.0
            for (i in windowStartIndex + 1 until routePoints.size) {
                val prevP = routePoints[i - 1]
                val currP = routePoints[i]
                val segDist = (currP.distance - prevP.distance).coerceAtLeast(1.0)

                val curBearing = bearing(prevP.latitude, prevP.longitude, currP.latitude, currP.longitude)
                var angleDiff = curBearing - initialBearing
                while (angleDiff > 180.0) angleDiff -= 360.0
                while (angleDiff < -180.0) angleDiff += 360.0

                val lateralMeters = sin(Math.toRadians(angleDiff)) * (segDist / 25.0)
                cumOffset += lateralMeters
                rawOffsets.add(Pair(currP.distance, cumOffset))

                if (currP.distance >= windowEndDist) {
                    break
                }
            }
        }

        // Muestreo uniforme en 50 puntos a lo largo de la ventana [windowStartDist, windowEndDist]
        val curvatureOffsets = List(50) { idx ->
            val targetDist = windowStartDist + idx * (actualLookahead / 49.0)
            if (rawOffsets.size >= 2) {
                val nextIdx = rawOffsets.indexOfFirst { it.first >= targetDist }
                if (nextIdx == -1) {
                    rawOffsets.last().second.coerceIn(-1.0, 1.0).toFloat()
                } else if (nextIdx == 0) {
                    rawOffsets.first().second.coerceIn(-1.0, 1.0).toFloat()
                } else {
                    val p0 = rawOffsets[nextIdx - 1]
                    val p1 = rawOffsets[nextIdx]
                    val span = (p1.first - p0.first).coerceAtLeast(0.1)
                    val frac = ((targetDist - p0.first) / span).coerceIn(0.0, 1.0)
                    (p0.second + frac * (p1.second - p0.second)).coerceIn(-1.0, 1.0).toFloat()
                }
            } else {
                0.0f
            }
        }

        // ── Cálculo de Pendiente Máxima y Media REAL del Tramo Visible ────────────────
        val rawSource = if (routeElevationProfile.isNotEmpty()) routeElevationProfile else routePoints.map { ElevationPolylineDecoder.ElevationPoint(it.distance, it.elevation) }

        var trueMaxGrade = 0.0
        var visibleElevationGain = 0.0
        var visibleAvgGrade = avgGradeRemaining // Fallback
        
        var trueAccumulatedHardness = 0.0
        var attackAlert = false

        if (rawSource.isNotEmpty()) {
            var lastPt: ElevationPolylineDecoder.ElevationPoint? = null
            var firstPt: ElevationPolylineDecoder.ElevationPoint? = null
            var endPt: ElevationPolylineDecoder.ElevationPoint? = null

            var totalAscent = 0.0
            var ascentDistance = 0.0

            for (pt in rawSource) {
                if (pt.distance < windowStartDist) {
                    lastPt = pt
                    continue
                }
                if (firstPt == null) firstPt = pt

                if (lastPt != null) {
                    val dDist = pt.distance - lastPt.distance
                    if (dDist > 5.0) { // Ignorar distancias microscópicas para evitar ruido
                        val dElev = pt.elevation - lastPt.elevation
                        val grade = (dElev / dDist) * 100.0
                        if (grade > trueMaxGrade) trueMaxGrade = grade
                        
                        if (grade > 0.0) {
                            trueAccumulatedHardness += FatigueGradeCalculator.calculateSegmentHardness(grade, dDist / 1000.0)
                        }
                        if (attackAlertsEnabled && grade > thresholdAttack) attackAlert = true
                        
                        if (dElev > 0.0) {
                            totalAscent += dElev
                            ascentDistance += dDist
                        }
                    }
                }

                lastPt = pt
                endPt = pt

                if (pt.distance > windowEndDist) {
                    break
                }
            }
            
            if (ascentDistance > 0.0) {
                visibleAvgGrade = (totalAscent / ascentDistance) * 100.0
            } else {
                visibleAvgGrade = 0.0
            }
        }

        val totalGf = FatigueGradeCalculator.calculateTotalFatigueGrade(
            totalHardness = trueAccumulatedHardness,
            asphaltFactor = asphaltFactor,
            maxRampPct = trueMaxGrade
        ).roundToInt()

        ClimbStateManager.updateApm(totalGf)

        // 12. Filtrar puertos visibles en la ventana 3D actual
        val visibleClimbs = routeClimbs.filter { climb ->
            climb.startDistance < windowEndDist && climb.endDistance > windowStartDist
        }

        return StrategyData(
            remainingDistance = totalDistanceRemaining,
            timeToSummit = secondsRemaining,
            avgGrade = avgGradeRemaining,
            nextBlocks = routeMajorBlocks,
            attackAlert = attackAlert,
            blockSizeMeters = majorBlockSize,
            totalFatigueGrade = totalGf,
            hairpins = visibleHairpins,
            pois = visiblePois,
            curvatureOffsets = curvatureOffsets,
            riderProgress = riderProgress,
            windowStartMeters = windowStartDist,
            windowStartElevation = windowStartElevation,
            subBlocks = routeSubBlocks,
            subBlockSizeMeters = subBlockSize,
            majorBlockSizeMeters = majorBlockSize,
            profileElevations = routeElevations,
            activeClimbs = visibleClimbs,
            visibleAvgGrade = visibleAvgGrade,
            visibleMaxGrade = trueMaxGrade,
            routeName = activeRouteName
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
        return try {
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
            poly
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getStrategyDataForClimb(climb: RouteClimb, customStartDist: Double? = null, customLength: Double? = null): StrategyData {
        val windowLength = customLength ?: climb.length
        val majorBlock = getMajorBlockSize(windowLength)
        val subBlock = getSubBlockSize(windowLength)
        
        val subBlocks = mutableListOf<Float>()
        val profileElevs = mutableListOf<Float>()
        
        val startDist = customStartDist ?: climb.startDistance
        val endDist = (startDist + windowLength).coerceAtMost(climb.endDistance)
        var currentDist = startDist
        
        var minElev = Double.MAX_VALUE
        var maxElev = Double.MIN_VALUE
        var trueMaxGrade = 0.0
        var totalAscent = 0.0
        var ascentDistance = 0.0
        
        // Sampling loop for the static climb
        while (currentDist < endDist) {
            val chunkEnd = (currentDist + subBlock).coerceAtMost(endDist)
            val e1 = getElevationAtDistance(currentDist)
            val e2 = getElevationAtDistance(chunkEnd)
            val dDist = chunkEnd - currentDist
            
            var grade = 0.0
            if (dDist > 0.0) {
                grade = ((e2 - e1) / dDist) * 100.0
                if (grade > trueMaxGrade) trueMaxGrade = grade
                if (grade > 0.0) {
                    totalAscent += (e2 - e1)
                    ascentDistance += dDist
                }
            }
            
            subBlocks.add(grade.toFloat())
            profileElevs.add(e1.toFloat())
            
            if (e1 < minElev) minElev = e1
            if (e2 > maxElev) maxElev = e2
            
            currentDist += subBlock
        }
        
        // Add final elevation point to close the profile
        val finalElev = getElevationAtDistance(endDist)
        profileElevs.add(finalElev.toFloat())
        if (finalElev < minElev) minElev = finalElev
        if (finalElev > maxElev) maxElev = finalElev
        
        val avgGrade = if (ascentDistance > 0) (totalAscent / ascentDistance) * 100.0 else climb.avgGrade
        
        return StrategyData(
            remainingDistance = climb.length,
            timeToSummit = 0L,
            avgGrade = avgGrade,
            nextBlocks = emptyList(), // Not used in this static view
            attackAlert = trueMaxGrade >= 12.0,
            blockSizeMeters = majorBlock,
            totalFatigueGrade = climb.apm.roundToInt(),
            hairpins = emptyList(), // Can be added later if needed
            pois = emptyList(),
            curvatureOffsets = emptyList(),
            riderProgress = 0f,
            windowStartMeters = startDist,
            windowStartElevation = minElev,
            subBlocks = subBlocks,
            subBlockSizeMeters = subBlock,
            majorBlockSizeMeters = majorBlock,
            profileElevations = profileElevs,
            activeClimbs = listOf(climb),
            visibleAvgGrade = avgGrade,
            visibleMaxGrade = trueMaxGrade,
            routeName = climb.category // Passing category as routeName to display in the header
        )
    }
}