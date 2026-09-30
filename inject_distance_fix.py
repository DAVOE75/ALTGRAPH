import re

with open('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/AltimetriaStrategyCalculator.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Add polylineScaleFactor to variables
var_injection = """
    private var lastRoutePolyline: String = ""
    private var lastElevationPolyline: String = ""
    private var polylineScaleFactor: Double = 1.0
"""
content = content.replace('private var lastRoutePolyline: String = ""\n    private var lastElevationPolyline: String = ""', var_injection.strip())

# Reset polylineScaleFactor in clearRoute()
clear_injection = """
        this.lastRoutePolyline = ""
        this.lastElevationPolyline = ""
        this.polylineScaleFactor = 1.0
"""
content = content.replace('this.lastRoutePolyline = ""\n        this.lastElevationPolyline = ""', clear_injection.strip())

# Reset polylineScaleFactor in setRouteFromPolyline()
setroute_injection = """
        if (polyline == lastRoutePolyline) return
        lastRoutePolyline = polyline
        polylineScaleFactor = 1.0
"""
content = content.replace('if (polyline == lastRoutePolyline) return\n        lastRoutePolyline = polyline', setroute_injection.strip())

# Inject calibration in syncRouteDistance()
sync_target = """    fun syncRouteDistance(karooRemainingDistance: Double) {
        this.fallbackRemainingDistance = karooRemainingDistance"""

sync_injection = """    fun syncRouteDistance(karooRemainingDistance: Double) {
        this.fallbackRemainingDistance = karooRemainingDistance

        if (routePoints.isEmpty()) return

        // [BUGFIX] Calibrate compressed WGS84 polyline distances to real Karoo distances
        if (polylineScaleFactor == 1.0 && currentLatitude != 0.0 && currentLongitude != 0.0 && karooRemainingDistance > 500.0 && nearestIndex < routePoints.size) {
            val rawPolylineTotal = routePoints.last().distance
            val currentPolylineDist = routePoints[nearestIndex].distance
            val polylineRemaining = rawPolylineTotal - currentPolylineDist
            
            if (polylineRemaining > 500.0) {
                polylineScaleFactor = karooRemainingDistance / polylineRemaining
                // Scale all distance arrays to match reality
                routePoints = routePoints.map { it.copy(distance = it.distance * polylineScaleFactor) }
                if (routeElevationProfile.isNotEmpty()) {
                    routeElevationProfile = routeElevationProfile.map { it.copy(distance = it.distance * polylineScaleFactor) }
                }
                if (absoluteHairpins.isNotEmpty()) {
                    absoluteHairpins = absoluteHairpins.map { it * polylineScaleFactor }.toMutableList()
                }
                android.util.Log.d("AltiCalc", "Calibrated distances. Factor: $polylineScaleFactor")
            }
        }"""

content = content.replace(sync_target, sync_injection)

# What if setRouteElevationProfile is called AFTER the calibration has already run?
# We must scale it immediately upon decoding.
setelev_target = """        if (result is ElevationPolylineDecoder.DecodeResult.Success) {
            this.routeElevationProfile = ElevationPolylineDecoder.smooth(result.points)"""

setelev_injection = """        if (result is ElevationPolylineDecoder.DecodeResult.Success) {
            var pts = ElevationPolylineDecoder.smooth(result.points)
            if (polylineScaleFactor != 1.0) {
                pts = pts.map { it.copy(distance = it.distance * polylineScaleFactor) }
            }
            this.routeElevationProfile = pts"""

content = content.replace(setelev_target, setelev_injection)

with open('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/AltimetriaStrategyCalculator.kt', 'w', encoding='utf-8') as f:
    f.write(content)

print('Strategy distance calibration injected.')
