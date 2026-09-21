import re

file_path = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\AltimetriaStrategyCalculator.kt'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update the top of calculateStrategy
original_start = '''    fun calculateStrategy(context: Context? = null): StrategyData {
        val prefs = context?.let { AppPreferences.getInstance(it) }

        val blockSize = prefs?.blockSizeMeters ?: 100.0
        val lookaheadDist = (prefs?.lookaheadMeters3d ?: 350).toDouble()
        val thresholdAttack = prefs?.thresholdAttackPct ?: 10.0
        val attackAlertsEnabled = prefs?.attackAlertEnabled ?: true
        val asphaltFactor = prefs?.asphaltFactor ?: 0.5
        val useTopographicCalculation = prefs?.useTopographicCalculation ?: false'''

new_start = '''    fun calculateStrategy(context: Context? = null): StrategyData {
        val prefs = context?.let { AppPreferences.getInstance(it) }

        val blockSize = prefs?.blockSizeMeters ?: 100.0
        val baseLookahead = (prefs?.lookaheadMeters3d ?: 350).toDouble()
        val thresholdAttack = prefs?.thresholdAttackPct ?: 10.0
        val attackAlertsEnabled = prefs?.attackAlertEnabled ?: true
        val asphaltFactor = prefs?.asphaltFactor ?: 0.5
        val useTopographicCalculation = prefs?.useTopographicCalculation ?: false

        // PRO Features Settings
        val smartZoomEnabled = prefs?.smartZoomEnabled ?: true
        val oasisTrackingEnabled = prefs?.oasisTrackingEnabled ?: true
        val virtualPacerEnabled = prefs?.virtualPacerEnabled ?: true
        val energyManagementEnabled = prefs?.energyManagementEnabled ?: true

        // SMART ZOOM LOGIC
        var lookaheadDist = baseLookahead
        if (smartZoomEnabled && baseLookahead < 100000.0) {
            val targetLookahead = when {
                instantBarometricGrade >= 8.0 -> baseLookahead.coerceAtMost(350.0) // Crucible
                instantBarometricGrade >= 4.0 -> baseLookahead.coerceAtLeast(1000.0) // Normal climb
                else -> baseLookahead.coerceAtLeast(3000.0) // Flat or descent
            }
            smartLookahead = smartLookahead + (targetLookahead - smartLookahead) * 0.05
            lookaheadDist = smartLookahead
        } else {
            smartLookahead = baseLookahead
        }

        // VIRTUAL PACER LOGIC
        if (virtualPacerEnabled) {
            val targetVam = (prefs?.targetVam ?: 900).toDouble()
            val vz = targetVam / 3600.0
            val gradeForPacer = instantBarometricGrade.coerceAtLeast(1.0)
            var vx = 100.0 * vz / gradeForPacer
            if (instantBarometricGrade < 1.0) {
                vx = 10.0 // 36 km/h cap in flat
            }
            virtualPacerDistance += vx * 1.0 // Assume ~1s tick
        }'''
content = content.replace(original_start, new_start)

# 2. Update Free Ride StrategyData return
free_ride_return_orig = '''            return StrategyData(
                remainingDistance = 0.0,
                timeToSummit = 0,
                avgGrade = instantBarometricGrade,
                nextBlocks = freeMajorBlocks,
                attackAlert = attackAlert,
                blockSizeMeters = majorBlockSize,
                totalFatigueGrade = liveFatigue.toInt(),
                hairpins = emptyList(),
                pois = emptyList(),
                curvatureOffsets = curvatureOffsets,
                riderProgress = riderProgress,
                windowStartMeters = windowStartDist,
                windowStartElevation = windowStartElevation,
                subBlocks = freeSubBlocks,
                subBlockSizeMeters = subBlockSize,
                majorBlockSizeMeters = majorBlockSize,
                profileElevations = freeElevations,
                activeClimbs = emptyList(),
                visibleAvgGrade = instantBarometricGrade,
                visibleMaxGrade = instantBarometricGrade,
                routeName = null
            )'''
free_ride_return_new = '''            return StrategyData(
                remainingDistance = 0.0,
                timeToSummit = 0,
                avgGrade = instantBarometricGrade,
                nextBlocks = freeMajorBlocks,
                attackAlert = attackAlert,
                blockSizeMeters = majorBlockSize,
                totalFatigueGrade = liveFatigue.toInt(),
                hairpins = emptyList(),
                pois = emptyList(),
                curvatureOffsets = curvatureOffsets,
                riderProgress = riderProgress,
                windowStartMeters = windowStartDist,
                windowStartElevation = windowStartElevation,
                subBlocks = freeSubBlocks,
                subBlockSizeMeters = subBlockSize,
                majorBlockSizeMeters = majorBlockSize,
                profileElevations = freeElevations,
                activeClimbs = emptyList(),
                visibleAvgGrade = instantBarometricGrade,
                visibleMaxGrade = instantBarometricGrade,
                routeName = null,
                oasisDistanceToNextCrucible = null,
                virtualPacerRelativeDistance = if (virtualPacerEnabled) virtualPacerDistance - liveDistanceAccumulated else null
            )'''
content = content.replace(free_ride_return_orig, free_ride_return_new)

# 3. Update Route StrategyData return and add Oasis tracking
route_return_orig = '''        return StrategyData(
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
        )'''
route_return_new = '''        // OASIS TRACKING LOGIC
        var oasisDistance: Double? = null
        if (oasisTrackingEnabled && instantBarometricGrade < 3.0) {
            var distToCrucible = 0.0
            for (i in (windowStartIndex..routePoints.size-2)) {
                val pt1 = routePoints[i]
                val pt2 = routePoints[i+1]
                val dist = pt2.distance - pt1.distance
                if (dist > 0) {
                    val grade = ((pt2.elevation - pt1.elevation) / dist) * 100.0
                    if (grade >= 8.0) {
                        distToCrucible = (pt1.distance - currentRiderDistance).coerceAtLeast(0.0)
                        break
                    }
                }
            }
            if (distToCrucible > 50.0) {
                oasisDistance = distToCrucible
            }
        }

        // Virtual pacer init logic for route mode
        if (virtualPacerDistance == 0.0) virtualPacerDistance = currentRiderDistance

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
            routeName = activeRouteName,
            oasisDistanceToNextCrucible = oasisDistance,
            virtualPacerRelativeDistance = if (virtualPacerEnabled) virtualPacerDistance - currentRiderDistance else null
        )'''
content = content.replace(route_return_orig, route_return_new)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("AltimetriaStrategyCalculator patched successfully")
