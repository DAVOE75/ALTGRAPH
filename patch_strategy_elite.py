import re

file_path = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\AltimetriaStrategyCalculator.kt'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Add new fields to StrategyData
data_class_old = '''data class StrategyData(
    val remainingDistance: Double,
    val timeToSummit: Long,
    val effectiveLookahead: Double,
    val hairpins: List<Double>,
    val curvature: List<Float>,
    val oasisDistanceToNextCrucible: Double? = null,
    val virtualPacerRelativeDistance: Double? = null,
    val energyBatteryLevel: Double = 100.0
)'''

data_class_new = '''data class StrategyData(
    val remainingDistance: Double,
    val timeToSummit: Long,
    val effectiveLookahead: Double,
    val hairpins: List<Double>,
    val curvature: List<Float>,
    val oasisDistanceToNextCrucible: Double? = null,
    val virtualPacerRelativeDistance: Double? = null,
    val energyBatteryLevel: Double = 100.0,
    val stravaSegmentDistance: Double? = null,
    val stravaPrGhostDistance: Double? = null,
    val windEffectIntensity: Double? = null // -1 to 1 (-1 headwind, 1 tailwind)
)'''

if 'stravaSegmentDistance' not in content:
    content = content.replace(data_class_old, data_class_new)

    # In free ride return
    content = content.replace(
        '''                virtualPacerRelativeDistance = null,
                energyBatteryLevel = energyBatteryLevel''',
        '''                virtualPacerRelativeDistance = null,
                energyBatteryLevel = energyBatteryLevel,
                stravaSegmentDistance = null,
                stravaPrGhostDistance = null,
                windEffectIntensity = null'''
    )
    
    # Define state variables
    state_vars_old = '''    private var energyBatteryLevel: Double = 100.0'''
    state_vars_new = '''    private var energyBatteryLevel: Double = 100.0
    
    // Elite State
    private var isStravaSegmentActive = false
    private var stravaSegmentStartDist = 0.0
    private var stravaPrGhostDist = 0.0
    private var lastWindCheckTime = 0L'''
    
    content = content.replace(state_vars_old, state_vars_new)

    # Add Logic inside Navigation loop
    # We will inject the logic right before the StrategyData return in route mode.
    # The return in route mode looks like:
    #         return StrategyData(
    #             remainingDistance = totalDistance - currentRiderDistance,
    # ...
    
    elite_logic = '''        // ELITE FEATURE: Wind Overlay
        var windEffect: Double? = null
        if (prefs?.weatherOverlayEnabled == true) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastWindCheckTime > 5 * 60 * 1000) { // Check every 5 mins
                if (routePoints.isNotEmpty()) {
                    val lat = routePoints[windowStartIndex].lat
                    val lng = routePoints[windowStartIndex].lng
                    WeatherService.fetchWeatherData(lat, lng)
                }
                lastWindCheckTime = currentTime
            }
            if (WeatherService.isDataFresh && windowStartIndex < routePoints.size - 10) {
                // Calculate heading
                val pt1 = routePoints[windowStartIndex]
                val pt2 = routePoints[windowStartIndex + 10]
                val dLon = (pt2.lng - pt1.lng)
                val y = Math.sin(Math.toRadians(dLon)) * Math.cos(Math.toRadians(pt2.lat))
                val x = Math.cos(Math.toRadians(pt1.lat)) * Math.sin(Math.toRadians(pt2.lat)) - Math.sin(Math.toRadians(pt1.lat)) * Math.cos(Math.toRadians(pt2.lat)) * Math.cos(Math.toRadians(dLon))
                var heading = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
                
                val windDir = WeatherService.currentWindDirectionDegrees.toDouble()
                val diff = Math.abs(heading - windDir)
                // If diff is 0, wind is coming from the same direction as we are going (tailwind? No, wind direction is where it comes FROM)
                // If wind is coming from North (0) and we head North (0), it's a headwind (-1).
                val tailwindComponent = -Math.cos(Math.toRadians(heading - windDir))
                windEffect = tailwindComponent * (WeatherService.currentWindSpeedKmh / 50.0).coerceIn(0.0, 1.0)
            }
        }

        // ELITE FEATURE: Strava Live Segments Mock
        var stravaRelDist: Double? = null
        var stravaGhostRelDist: Double? = null
        if (prefs?.stravaSegmentsEnabled == true) {
            if (!isStravaSegmentActive && instantBarometricGrade > 7.0 && currentRiderDistance > stravaSegmentStartDist + 3000) {
                // Trigger a mock segment
                isStravaSegmentActive = true
                stravaSegmentStartDist = currentRiderDistance
                stravaPrGhostDist = currentRiderDistance
            }
            
            if (isStravaSegmentActive) {
                val distInSegment = currentRiderDistance - stravaSegmentStartDist
                if (distInSegment > 2000.0) {
                    isStravaSegmentActive = false // Segment finished
                } else {
                    stravaRelDist = distInSegment
                    // Ghost runs at 1300 VAM (approx 5.5 m/s at 7%)
                    stravaPrGhostDist += (5.5 * 1.0) // 1s tick
                    stravaGhostRelDist = stravaPrGhostDist - currentRiderDistance
                }
            }
        }

        return StrategyData('''
    
    content = content.replace('        return StrategyData(', elite_logic)
    
    # Finally, add the fields to the return
    content = content.replace(
        '''                virtualPacerRelativeDistance = if (virtualPacerEnabled) virtualPacerDistance - currentRiderDistance else null,
            energyBatteryLevel = energyBatteryLevel
        )''',
        '''                virtualPacerRelativeDistance = if (virtualPacerEnabled) virtualPacerDistance - currentRiderDistance else null,
            energyBatteryLevel = energyBatteryLevel,
            stravaSegmentDistance = stravaRelDist,
            stravaPrGhostDistance = stravaGhostRelDist,
            windEffectIntensity = windEffect
        )'''
    )

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Elite Strategy patched")
else:
    print("Elite Strategy already patched")
