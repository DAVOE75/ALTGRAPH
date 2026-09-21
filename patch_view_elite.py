import re

file_path = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\Altimetria3DView.kt'
df1_path = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\Altimetria3DGraphDataField.kt'
df2_path = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\ClimbViewerDataField.kt'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

elite_vars = '''    var stravaSegmentDistance: Double? = null
    var stravaPrGhostDistance: Double? = null
    var windEffectIntensity: Double? = null // -1 to 1

    private val stravaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FC4C02") // Strava Orange
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 0f, Color.parseColor("#FC4C02"))
    }
    
    private val windPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
'''

if 'stravaSegmentDistance' not in content:
    content = content.replace(
        '    var energyBatteryLevel: Double = 100.0',
        '    var energyBatteryLevel: Double = 100.0\n' + elite_vars
    )
    
    # We inject the rendering logic at the end of draw3DStraightRibbon
    render_logic = '''        // ELITE FEATURE: Wind Vectors
        windEffectIntensity?.let { wind ->
            val wColor = if (wind < 0) Color.RED else Color.GREEN
            windPaint.color = wColor
            windPaint.alpha = (Math.abs(wind) * 255).toInt().coerceIn(50, 255)
            // Draw wind arrows on the ribbon
            for (i in 0 until totalMicroSamples step totalMicroSamples / 10) {
                val px = xFront[i]
                val py = yFront[i] - 30f
                val dx = if (wind < 0) -20f else 20f
                canvas.drawLine(px, py, px + dx, py, windPaint)
                canvas.drawLine(px + dx, py, px + dx - Math.signum(dx)*5f, py - 5f, windPaint)
                canvas.drawLine(px + dx, py, px + dx - Math.signum(dx)*5f, py + 5f, windPaint)
            }
        }

        // ELITE FEATURE: Strava Live Segment & PR Ghost
        if (stravaSegmentDistance != null) {
            val dist = stravaSegmentDistance!!
            // Highlight road
            val segmentStartIdx = ((dist - windowStartMeters) / lookaheadMeters * totalMicroSamples).toInt().coerceIn(0, totalMicroSamples - 1)
            val segmentEndIdx = totalMicroSamples - 1
            if (segmentStartIdx < totalMicroSamples) {
                for (i in segmentStartIdx until segmentEndIdx) {
                    canvas.drawCircle(xFront[i], yFront[i], 3f, stravaPaint)
                }
            }
            
            // Draw PR Ghost
            stravaPrGhostDistance?.let { relDist ->
                if (Math.abs(relDist) < lookaheadMeters) {
                    val gX = startX + ((relDist + windowStartMeters - windowStartMeters) / lookaheadMeters * totalMicroSamples * microStepX).toFloat() - panOffsetX
                    val gIdx = ((relDist + windowStartMeters - windowStartMeters) / lookaheadMeters * totalMicroSamples).toInt().coerceIn(0, totalMicroSamples - 1)
                    val gY = yFront[gIdx] - 15f
                    canvas.drawCircle(gX, gY, 8f, stravaPaint)
                    canvas.drawLine(gX, gY, gX, gY - 40f, stravaPaint)
                    val prTextPaint = Paint(stravaPaint).apply { textSize = 12f; textAlign = Paint.Align.CENTER }
                    canvas.drawText("KOM", gX, gY - 45f, prTextPaint)
                }
            }
        }
'''
    
    content = content.replace(
        '        // PRO FEATURE: Energy Battery HUD',
        render_logic + '\n        // PRO FEATURE: Energy Battery HUD'
    )
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

# Patch DataFields
for df in [df1_path, df2_path]:
    with open(df, 'r', encoding='utf-8') as f:
        c = f.read()
    
    if 'stravaSegmentDistance' not in c:
        c = c.replace(
            'altimetria3DView.energyBatteryLevel = strategy.energyBatteryLevel',
            '''altimetria3DView.energyBatteryLevel = strategy.energyBatteryLevel
                altimetria3DView.stravaSegmentDistance = strategy.stravaSegmentDistance
                altimetria3DView.stravaPrGhostDistance = strategy.stravaPrGhostDistance
                altimetria3DView.windEffectIntensity = strategy.windEffectIntensity'''
        )
        with open(df, 'w', encoding='utf-8') as f:
            f.write(c)

print("View and DataFields patched")

