package com.example.altgraph

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AltgraphCalculationsTest {

    @Test
    fun testRemainingDistanceDoesNotSumCumulativeDistances() {
        val calculator = AltimetriaStrategyCalculator()
        
        // Simular una ruta de 1000 metros con 5 puntos con distancias acumuladas
        calculator.routePoints = listOf(
            RoutePoint(38.8000, -0.2000, 100.0, 0.0),
            RoutePoint(38.8010, -0.2000, 120.0, 250.0),
            RoutePoint(38.8020, -0.2000, 150.0, 500.0),
            RoutePoint(38.8030, -0.2000, 180.0, 750.0),
            RoutePoint(38.8040, -0.2000, 200.0, 1000.0)
        )
        calculator.isNavigatingRoute = true
        calculator.currentElevation = 150.0
        
        // El ciclista se encuentra exactamente en el punto 2 (500m acumulados)
        calculator.updateCurrentLocation(38.8020, -0.2000)

        val strategy = calculator.calculateStrategy()

        // La distancia restante DEBE ser 1000.0 - 500.0 = 500.0 metros.
        // Con el error previo sumOf { it.distance }, habría sumado 500 + 750 + 1000 = 2250 metros.
        assertEquals(500.0, strategy.remainingDistance, 0.1)

        // Desnivel restante: 200m - 150m = 50m en 500m de distancia -> pendiente = 10.0%
        // Con el error previo, habría dividido 50m / 2250m = 2.2% erróneo.
        assertEquals(10.0, strategy.avgGrade, 0.2)
    }

    @Test
    fun testRollingWindow50MetersAndRiderProgression() {
        val calculator = AltimetriaStrategyCalculator()

        // Ruta de 1000m con puntos cada 25m
        val points = mutableListOf<RoutePoint>()
        for (i in 0..40) {
            val dist = i * 25.0
            val lat = 38.8000 + (i * 0.0002)
            points.add(RoutePoint(lat, -0.2000, 100.0 + (dist * 0.08), dist))
        }
        calculator.routePoints = points
        calculator.isNavigatingRoute = true

        // Caso 1: Ciclista en 25m (lookahead por defecto 350m)
        // Ventana base: 0m. Avance del punto: 25m / 350m
        calculator.updateCurrentLocation(points[1].latitude, points[1].longitude)
        var strategy = calculator.calculateStrategy()
        assertEquals(0.0, strategy.windowStartMeters, 0.1)
        assertEquals(25.0 / 350.0, strategy.riderProgress.toDouble(), 0.01)

        // Caso 2: Ciclista avanza a 50m
        // La ventana se desplaza 50 metros hacia la izquierda (windowStart = 50m)
        // El punto se ubica al inicio de la nueva ventana (riderProgress = 0.0)
        calculator.updateCurrentLocation(points[2].latitude, points[2].longitude)
        strategy = calculator.calculateStrategy()
        assertEquals(50.0, strategy.windowStartMeters, 0.1)
        assertEquals(0.0, strategy.riderProgress.toDouble(), 0.01)

        // Caso 3: Ciclista avanza a 75m
        // Ventana se mantiene en 50m, punto avanza a (75m - 50m) = 25m
        calculator.updateCurrentLocation(points[3].latitude, points[3].longitude)
        strategy = calculator.calculateStrategy()
        assertEquals(50.0, strategy.windowStartMeters, 0.1)
        assertEquals(25.0 / 350.0, strategy.riderProgress.toDouble(), 0.01)

        // Caso 4: Ciclista avanza a 100m
        // Ventana se desplaza a 100m
        calculator.updateCurrentLocation(points[4].latitude, points[4].longitude)
        strategy = calculator.calculateStrategy()
        assertEquals(100.0, strategy.windowStartMeters, 0.1)
        assertEquals(0.0, strategy.riderProgress.toDouble(), 0.01)
    }

    @Test
    fun testClimbPacingStationaryAndActive() {
        // 1. En parado (Modo Demo de prueba en interiores)
        val stationary = ClimbPacingCalculator.calculatePacing(
            currentSpeedMps = 0.0,
            currentGradientPct = 0.0,
            userTargetVam = 900
        )
        assertEquals(850, stationary.currentVam)
        assertEquals(ClimbPacingCalculator.PacingStatus.ON_PACE, stationary.status)

        // 2. En llano o descenso (pedaleando a 30 km/h / 8.3 m/s con pendiente <= 0.5%)
        val flatDescent = ClimbPacingCalculator.calculatePacing(
            currentSpeedMps = 8.33,
            currentGradientPct = -1.5,
            userTargetVam = 900
        )
        assertEquals(0, flatDescent.currentVam)
        assertEquals(0.0, flatDescent.targetSpeedKmh, 0.01)
        assertEquals(ClimbPacingCalculator.PacingStatus.ON_PACE, flatDescent.status)

        // 3. Subiendo puerto al 8% a 12.6 km/h (3.5 m/s) con VAM objetivo de 900 m/h
        // VAM calculada = 3.5 * 3600 * 0.08 = 1008 m/h
        // Velocidad objetivo = 900 / (8 * 10) = 11.25 km/h
        val climb = ClimbPacingCalculator.calculatePacing(
            currentSpeedMps = 3.5,
            currentGradientPct = 8.0,
            userTargetVam = 900
        )
        assertEquals(1008, climb.currentVam)
        assertEquals(11.25, climb.targetSpeedKmh, 0.1)
        assertEquals(ClimbPacingCalculator.PacingStatus.ON_PACE, climb.status)
    }

    @Test
    fun testGradientTrendTracker() {
        val tracker = GradientTrendTracker()

        // 1. Añadir pendientes en aumento fuerte
        tracker.addSample(5.0)
        tracker.addSample(6.0)
        tracker.addSample(8.0)
        tracker.addSample(10.0)
        val steepening = tracker.addSample(13.5)

        assertEquals(13.5, steepening.currentGrade, 0.1)
        assertEquals(13.5, steepening.maxRampPeak, 0.1)
        assertEquals(GradientTrend.STEEPENING, steepening.trend)

        // 2. Pendiente estabilizada
        for (i in 1..15) {
            tracker.addSample(8.0)
        }
        val steady = tracker.addSample(8.0)
        assertEquals(GradientTrend.STEADY, steady.trend)

        // 3. Pendiente suavizando
        tracker.addSample(6.0)
        tracker.addSample(4.0)
        val easing = tracker.addSample(2.0)
        assertEquals(GradientTrend.EASING, easing.trend)
    }

    @Test
    fun testFatigueGradeScientificModel() {
        // Dureza de un tramo de 5km al 6% de pendiente media
        // DU = (6^2 / 3.00) * 5 = 12 * 5 = 60.0
        val du = FatigueGradeCalculator.calculateSegmentHardness(6.0, 5.0)
        assertEquals(60.0, du, 0.01)

        // GF Total con asfalto bueno (TA = 0.5) y rampa máxima del 12%
        // GF = 60.0 + 0.5 + (12 / 5) = 60.0 + 0.5 + 2.4 = 62.9
        val gf = FatigueGradeCalculator.calculateTotalFatigueGrade(du, 0.5, 12.0)
        assertEquals(62.9, gf, 0.01)

        // Clasificación de puerto según GF
        val cat = FatigueGradeCalculator.getClimbCategoryName(gf)
        assertEquals("3ª Cat", cat)
    }

    @Test
    fun testAdaptiveSubBlockAndMajorBlockScaling() {
        val calculator = AltimetriaStrategyCalculator()

        // 1. Escala <= 500m: bloques intermedios de 50m dentro de bloques de 100m
        assertEquals(50.0, calculator.getSubBlockSize(200.0), 0.1)
        assertEquals(50.0, calculator.getSubBlockSize(350.0), 0.1)
        assertEquals(50.0, calculator.getSubBlockSize(500.0), 0.1)
        assertEquals(100.0, calculator.getMajorBlockSize(500.0), 0.1)

        // 2. Escala 500m a 5km: bloques de 100 en 100 metros
        assertEquals(100.0, calculator.getSubBlockSize(1000.0), 0.1)
        assertEquals(100.0, calculator.getSubBlockSize(2000.0), 0.1)
        assertEquals(100.0, calculator.getSubBlockSize(5000.0), 0.1)
        assertEquals(500.0, calculator.getMajorBlockSize(5000.0), 0.1)

        // 3. Escala 5km a 20km: bloques de 500 en 500 metros
        assertEquals(500.0, calculator.getSubBlockSize(10000.0), 0.1)
        assertEquals(500.0, calculator.getSubBlockSize(20000.0), 0.1)
        assertEquals(2000.0, calculator.getMajorBlockSize(20000.0), 0.1)

        // 4. Escala 20km a 50km: bloques de 1km en 1km
        assertEquals(1000.0, calculator.getSubBlockSize(30000.0), 0.1)
        assertEquals(1000.0, calculator.getSubBlockSize(50000.0), 0.1)
        assertEquals(5000.0, calculator.getMajorBlockSize(50000.0), 0.1)

        // 5. Escala > 100km: tramos de 10 en 10 km
        assertEquals(10000.0, calculator.getSubBlockSize(120000.0), 0.1)
        assertEquals(20000.0, calculator.getMajorBlockSize(120000.0), 0.1)
    }

    @Test
    fun testProfileFidelityPreservesRampsAndFlatsWithinOneKm() {
        val calculator = AltimetriaStrategyCalculator()

        // Crear una ruta de 1000m:
        // - 0m a 500m: rampa dura al 10% (+50 metros de elevación)
        // - 500m a 1000m: tramo totalmente llano al 0% (+0 metros de elevación)
        // Media global del kilómetro: 50m / 1000m = 5.0%
        val points = mutableListOf<RoutePoint>()
        for (i in 0..10) {
            val d = i * 50.0
            val elev = 100.0 + (d * 0.10) // 10%
            points.add(RoutePoint(38.8000 + (d * 0.0001), -0.2000, elev, d))
        }
        for (i in 11..20) {
            val d = i * 50.0
            val elev = 150.0 // 0%
            points.add(RoutePoint(38.8000 + (d * 0.0001), -0.2000, elev, d))
        }
        calculator.routePoints = points
        calculator.isNavigatingRoute = true
        calculator.updateCurrentLocation(points[0].latitude, points[0].longitude)

        // Con escala de 500m (lookahead = 500m), subBlockSize = 50m
        // Debe capturar los sub-bloques al 10% exactamente sin aplanarlos
        val strategy = calculator.calculateStrategy()

        assertTrue(strategy.subBlocks.isNotEmpty())
        assertEquals(50.0, strategy.subBlockSizeMeters, 0.1)
        assertEquals(100.0, strategy.majorBlockSizeMeters, 0.1)

        // El primer sub-bloque de 50m debe tener pendiente del 10.0%, no la media
        assertEquals(10.0, strategy.subBlocks[0].toDouble(), 0.2)

        // Las elevaciones del perfil deben reflejar la subida real
        assertEquals(100.0, strategy.profileElevations.first().toDouble(), 0.1)
        assertEquals(105.0, strategy.profileElevations[1].toDouble(), 0.2)
    }

    @Test
    fun testGradeColorScaleProgression() {
        // 1. Descenso o pendientes negativas: SIEMPRE AZUL
        assertEquals(GradeColorScale.COLOR_DESCENSO, GradeColorScale.getColorHex(-8.0))
        assertEquals(GradeColorScale.COLOR_DESCENSO, GradeColorScale.getColorHex(-2.5))
        assertEquals(GradeColorScale.COLOR_DESCENSO, GradeColorScale.getColorHex(-0.1))

        // 2. 15 tramos de uno en uno desde el 0% al 15% (Blanco -> Amarillo claro -> Amarillo oscuro -> Naranja -> Rojo intenso)
        assertEquals("#FFFFFF", GradeColorScale.getColorHex(0.0))  // 0% a 1%: Blanco
        assertEquals("#FFFFFF", GradeColorScale.getColorHex(0.8))
        assertEquals("#FEF9C3", GradeColorScale.getColorHex(1.2))  // 1% a 2%: Crema
        assertEquals("#FEF08A", GradeColorScale.getColorHex(2.5))  // 2% a 3%: Amarillo muy claro
        assertEquals("#FDE047", GradeColorScale.getColorHex(3.5))  // 3% a 4%: Amarillo claro
        assertEquals("#FACC15", GradeColorScale.getColorHex(4.9))  // 4% a 5%: Amarillo medio
        assertEquals("#EAB308", GradeColorScale.getColorHex(5.1))  // 5% a 6%: Amarillo más oscuro / dorado
        assertEquals("#F59E0B", GradeColorScale.getColorHex(6.7))  // 6% a 7%: Amarillo anaranjado
        assertEquals("#FB923C", GradeColorScale.getColorHex(7.3))  // 7% a 8%: Naranja claro
        assertEquals("#F97316", GradeColorScale.getColorHex(8.4))  // 8% a 9%: Naranja
        assertEquals("#EA580C", GradeColorScale.getColorHex(9.6))  // 9% a 10%: Naranja intenso
        assertEquals("#E03E1A", GradeColorScale.getColorHex(10.2)) // 10% a 11%: Naranja rojizo
        assertEquals("#EA2E1A", GradeColorScale.getColorHex(11.8)) // 11% a 12%: Rojo anaranjado
        assertEquals("#E02424", GradeColorScale.getColorHex(12.5)) // 12% a 13%: Rojo vivo
        assertEquals("#DC2626", GradeColorScale.getColorHex(13.9)) // 13% a 14%: Rojo puro
        assertEquals("#B91C1C", GradeColorScale.getColorHex(14.7)) // 14% a 15%: Rojo intenso

        // 3. Superior al 15% hasta 20%: ROJO INTENSO
        assertEquals(GradeColorScale.COLOR_ROJO_INTENSO, GradeColorScale.getColorHex(15.0))
        assertEquals(GradeColorScale.COLOR_ROJO_INTENSO, GradeColorScale.getColorHex(17.5))
        assertEquals(GradeColorScale.COLOR_ROJO_INTENSO, GradeColorScale.getColorHex(20.0))

        // 4. Superior al 20%: NEGRO
        assertEquals(GradeColorScale.COLOR_NEGRO, GradeColorScale.getColorHex(20.1))
        assertEquals(GradeColorScale.COLOR_NEGRO, GradeColorScale.getColorHex(24.0))
    }

    @Test
    fun testAltimetriaStyleEnumsAndKeys() {
        // 1. Verificación de existencia de las 5 vistas revolucionarias
        val entries = AltimetriaStyle.entries
        assertEquals(5, entries.size)

        // 2. Modelo por defecto: CLASSIC
        assertEquals(AltimetriaStyle.CLASSIC, AltimetriaStyle.fromKey("classic"))
        assertEquals(AltimetriaStyle.CLASSIC, AltimetriaStyle.fromKey("inexistente"))

        // 3. Claves y deserialización exacta de cada modelo
        assertEquals(AltimetriaStyle.CLASSIC, AltimetriaStyle.fromKey(AltimetriaStyle.CLASSIC.key))
        assertEquals(AltimetriaStyle.HORIZON_ISOMETRIC, AltimetriaStyle.fromKey("horizon_iso"))
        assertEquals(AltimetriaStyle.TACTICAL_OASES, AltimetriaStyle.fromKey("tactical_oases"))
        assertEquals(AltimetriaStyle.DYNAMIC_FORCE_FIELD, AltimetriaStyle.fromKey("force_field"))
        assertEquals(AltimetriaStyle.MONOLITHIC_OBSIDIAN, AltimetriaStyle.fromKey("monolithic"))

        // 4. Iconos y metadatos no vacíos
        entries.forEach { style ->
            assertTrue(style.icon.isNotEmpty())
            assertTrue(style.title.isNotEmpty())
            assertTrue(style.description.isNotEmpty())
        }
    }
}



