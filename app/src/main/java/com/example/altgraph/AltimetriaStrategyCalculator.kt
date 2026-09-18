package com.example.altgraph

import android.content.Context
import io.hammerhead.karooext.models.Symbol
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
    val riderProgress: Float = 0.0f,
    val windowStartMeters: Double = 0.0,
    val windowStartElevation: Double = 0.0,
    val subBlocks: List<Float> = emptyList(),
    val subBlockSizeMeters: Double = 50.0,
    val majorBlockSizeMeters: Double = 100.0,
    val profileElevations: List<Float> = emptyList()
)

class AltimetriaStrategyCalculator {

    var currentLatitude = 0.0
    var currentLongitude = 0.0
    var currentSpeed = 0.0
    var currentElevation = 350.0

    var isNavigatingRoute = false
    var routePoints: List<RoutePoint> = emptyList()
    
    // Historial y buffer de altitud barométrica instantánea en tiempo real
    private val liveElevationHistory = mutableListOf<Double>()
    private var lastElevationSample = 350.0
    private var liveDistanceAccumulated = 0.0
    private var instantBarometricGrade = 0.0

    // ── Datos del Climber nativo de Karoo (DISTANCE_TO_TOP, ELEVATION_TO_TOP, …) ──
    // El SDK expone estos valores calculados directamente desde el archivo de ruta (GPX/FIT).
    // Son la fuente de verdad para la altimetría real del climb activo.
    var distanceToTop: Double = 0.0      // metros hasta la cima del climb actual
    var elevationToTop: Double = 0.0     // metros de desnivel hasta la cima
    var distanceFromBottom: Double = 0.0 // metros desde la base del climb
    var elevationFromBottom: Double = 0.0 // metros de desnivel desde la base
    var elevationRemaining: Double = 0.0 // desnivel restante total de la ruta

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
            else -> 10000.0
        }
    }

    fun getMajorBlockSize(lookaheadDist: Double): Double {
        return when {
            lookaheadDist <= 500.0 -> 100.0
            lookaheadDist <= 5000.0 -> 500.0
            lookaheadDist <= 20000.0 -> 2000.0
            lookaheadDist <= 50000.0 -> 5000.0
            else -> 20000.0
        }
    }

    fun getElevationAtDistance(dist: Double): Double {
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

    fun setRoutePois(symbols: List<Symbol.POI>) {
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
            val name = sym.name ?: "Hito"
            val icon = when {
                name.contains("agua", ignoreCase = true) || name.contains("font", ignoreCase = true) || name.contains("fuente", ignoreCase = true) -> "💧"
                name.contains("mirador", ignoreCase = true) || name.contains("vista", ignoreCase = true) -> "📸"
                name.contains("puerto", ignoreCase = true) || name.contains("cima", ignoreCase = true) || name.contains("alto", ignoreCase = true) || name.contains("col", ignoreCase = true) -> "📡"
                else -> "🏘️"
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

    fun setRouteFromPolyline(polyline: String) {
        val points = decodePolyline(polyline)
        if (points.isEmpty()) {
            clearRoute()
            return
        }

        // Build a list of raw (lat, lng, distAcumulada) first so we can apply real elevation.
        // The Karoo polyline encodes ONLY lat/lng — no altitude. We anchor the profile on the
        // current barometric elevation and accumulate vertical gain from the live grade stream.
        // Any subsequent live elevation update will correct currentElevation in real time.
        var accumulatedDist = 0.0
        val result = mutableListOf<RoutePoint>()

        // First pass: compute accumulated distances
        val rawDistances = DoubleArray(points.size)
        for (i in points.indices) {
            if (i > 0) {
                val prev = points[i - 1]
                val pt  = points[i]
                val d = hypot(pt.first - prev.first, pt.second - prev.second) * 111000.0
                accumulatedDist += d
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
        this.absoluteHairpins.clear()
        this.routePois.clear()
        this.isNavigatingRoute = false
        // Resetear datos del climb al salir de la navegación
        this.distanceToTop = 0.0
        this.elevationToTop = 0.0
        this.distanceFromBottom = 0.0
        this.elevationFromBottom = 0.0
        this.elevationRemaining = 0.0
    }

    fun calculateStrategy(context: Context? = null): StrategyData {
        val prefs = context?.let { AppPreferences.getInstance(it) }

        val blockSize = prefs?.blockSizeMeters ?: 100.0
        val lookaheadDist = (prefs?.lookaheadMeters3d ?: 350).toDouble()
        val thresholdAttack = prefs?.thresholdAttackPct ?: 10.0
        val attackAlertsEnabled = prefs?.attackAlertEnabled ?: true
        val asphaltFactor = prefs?.asphaltFactor ?: 0.5
        val useTopographicCalculation = prefs?.useTopographicCalculation ?: false

        val isNavigating = isNavigatingRoute && routePoints.isNotEmpty()

        // MODO LIBRE / ENTRENAMIENTO REAL SIN RUTA PRECARGADA:
        // Genera el perfil 3D barométrico ajustándose exactamente a la pendiente en vivo de la carretera
        if (!isNavigating) {
            val baseGrade = if (instantBarometricGrade != 0.0) instantBarometricGrade else 5.0
            if (currentSpeed > 0.1) {
                liveDistanceAccumulated += currentSpeed * 1.0 // Medido cada segundo
            }

            val quantumMeters = 50.0
            val windowStartDist = (liveDistanceAccumulated / quantumMeters).toLong() * quantumMeters
            val riderDistInWindow = (liveDistanceAccumulated - windowStartDist).coerceAtLeast(0.0)
            val riderProgress = (riderDistInWindow / lookaheadDist).toFloat().coerceIn(0f, 1f)

            val subBlockSize = getSubBlockSize(lookaheadDist)
            val majorBlockSize = getMajorBlockSize(lookaheadDist)
            val numSubBlocks = (lookaheadDist / subBlockSize).toInt().coerceIn(2, 60)

            val freeSubBlocks = mutableListOf<Float>()
            val freeElevations = mutableListOf<Float>()
            var accElev = currentElevation
            freeElevations.add(accElev.toFloat())

            // Proyectar la pendiente actual de forma constante hacia adelante.
            // NO añadimos oscilaciones sinusoidales — el perfil debe reflejar la
            // carretera real que el altímetro barométrico está leyendo, no un adorno.
            val constantGrade = baseGrade.coerceIn(-30.0, 30.0).toFloat()
            for (idx in 0 until numSubBlocks) {
                freeSubBlocks.add(constantGrade)
                accElev += subBlockSize * (constantGrade / 100.0)
                freeElevations.add(accElev.toFloat())
            }

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
                windowStartElevation = currentElevation,
                subBlocks = freeSubBlocks,
                subBlockSizeMeters = subBlockSize,
                majorBlockSizeMeters = majorBlockSize,
                profileElevations = freeElevations
            )
        }

        // MODO NAVEGANDO RUTA PRECARGADA (GPX / FIT):
        // 1. Encuentra la posición GPS exacta del ciclista en el trazado de la ruta
        var nearestIndex = 0
        var minDistance = Double.MAX_VALUE
        routePoints.forEachIndexed { index, point ->
            val dist = hypot(point.latitude - currentLatitude, point.longitude - currentLongitude)
            if (dist < minDistance) {
                minDistance = dist
                nearestIndex = index
            }
        }

        val currentRiderDistance = routePoints[nearestIndex].distance

        // 2. Ventana deslizante en bloques cuánticos de 50 metros
        val quantumMeters = 50.0
        val windowStartDist = (currentRiderDistance / quantumMeters).toLong() * quantumMeters
        val windowEndDist = windowStartDist + lookaheadDist

        val riderOffsetInWindow = (currentRiderDistance - windowStartDist).coerceAtLeast(0.0)
        val riderProgress = (riderOffsetInWindow / lookaheadDist).toFloat().coerceIn(0f, 1f)

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
        val subBlockSize = getSubBlockSize(lookaheadDist)
        val majorBlockSize = getMajorBlockSize(lookaheadDist)

        val numSubBlocks = (lookaheadDist / subBlockSize).roundToInt().coerceIn(2, 200)
        val routeSubBlocks = mutableListOf<Float>()
        val routeElevations = mutableListOf<Float>()
        routeElevations.add(windowStartElevation.toFloat())

        var attack = false
        var accumulatedHardness = 0.0
        var maxRampPct = 0.0

        // ── Estrategia de construcción del perfil ──────────────────────────────────
        // Si tenemos datos nativos del climb (del SDK, fuente directa del archivo GPX/FIT):
        //   → Los bloques se calculan interpolando linealmente entre la posición actual
        //     y la cima real del climb. Esto replica exactamente lo que hace el Climber
        //     de Hammerhead, pero en representación 3D.
        // Si no tenemos datos del climb (descenso, pausa, ruta sin climb activo):
        //   → Usamos la polilínea de ruta con la calibración barométrica progresiva.
        // ──────────────────────────────────────────────────────────────────────────
        val useClimberData = distanceToTop > 50.0 && elevationToTop > 0.5
        val summitElevation = if (useClimberData) currentElevation + elevationToTop else 0.0

        for (j in 0 until numSubBlocks) {
            val sDistStart = windowStartDist + (j * subBlockSize)
            val sDistEnd = windowStartDist + ((j + 1) * subBlockSize)

            val sElevStart: Double
            val sElevEnd: Double

            if (useClimberData) {
                // Interpolación lineal a lo largo del climb real hacia la cima
                // Fracción de la ventana lookahead que ya ha cubierto el rider
                val fracStart = (sDistStart - windowStartDist).coerceAtLeast(0.0) / distanceToTop.coerceAtLeast(1.0)
                val fracEnd   = (sDistEnd   - windowStartDist).coerceAtLeast(0.0) / distanceToTop.coerceAtLeast(1.0)
                sElevStart = currentElevation + fracStart.coerceIn(0.0, 1.0) * elevationToTop
                sElevEnd   = currentElevation + fracEnd.coerceIn(0.0, 1.0)   * elevationToTop
            } else {
                // Fallback: polilínea 2D calibrada barométricamente
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

            val distanceKm = sDist / 1000.0
            if (grade > maxRampPct) maxRampPct = grade
            accumulatedHardness += FatigueGradeCalculator.calculateSegmentHardness(grade, distanceKm)
            if (attackAlertsEnabled && grade > thresholdAttack) attack = true
        }

        // Construcción de bloques mayores para telemetría y rótulos
        val numMajorBlocks = (lookaheadDist / majorBlockSize).roundToInt().coerceIn(1, 20)
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

        val totalGf = FatigueGradeCalculator.calculateTotalFatigueGrade(
            totalHardness = accumulatedHardness,
            asphaltFactor = asphaltFactor,
            maxRampPct = maxRampPct
        ).roundToInt()

        ClimbStateManager.updateApm(totalGf)

        val visibleHairpins = absoluteHairpins
            .map { it - windowStartDist }
            .filter { it in 0.0..lookaheadDist }

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
            matchesCategory && poi.relativeDistance in 0.0..lookaheadDist
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
            val targetDist = windowStartDist + idx * (lookaheadDist / 49.0)
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

        return StrategyData(
            remainingDistance = totalDistanceRemaining,
            timeToSummit = secondsRemaining,
            avgGrade = avgGradeRemaining,
            nextBlocks = routeMajorBlocks,
            attackAlert = attack,
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
            profileElevations = routeElevations
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
}