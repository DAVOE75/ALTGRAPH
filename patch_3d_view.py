import re

file_path = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\Altimetria3DView.kt'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add class properties
props = '''    var oasisDistanceToNextCrucible: Double? = null
    var virtualPacerRelativeDistance: Double? = null
'''
content = content.replace('    var isClimbMode: Boolean = false', props + '    var isClimbMode: Boolean = false')

# 2. Add paints for Oasis and Pacer
paints_orig = '''    private val subTitleLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        typeface = Typeface.DEFAULT_BOLD
    }'''
paints_new = paints_orig + '''
    private val oasisHoloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        textSize = 14f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        setShadowLayer(6f, 0f, 0f, Color.parseColor("#800284C7"))
    }
    private val virtualPacerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8022D3EE") # Semi-transparent Cyan
        style = Paint.Style.FILL
        setShadowLayer(10f, 0f, 0f, Color.parseColor("#22D3EE"))
    }
'''
content = content.replace(paints_orig, paints_new)

# 3. Add drawing logic in onDraw (at the very end of onDraw, near drawTopographicPeaks call)
draw_orig = '''        if (showCotas) {
            drawTopographicPeaks(canvas, xFront, yFront, yBase, totalMicroSamples, microElevations)
        }
    }'''

draw_new = '''        if (showCotas) {
            drawTopographicPeaks(canvas, xFront, yFront, yBase, totalMicroSamples, microElevations)
        }
        
        // PRO FEATURE: Virtual Pacer
        virtualPacerRelativeDistance?.let { pacerRelDist ->
            val riderOffsetInWindow = riderProgress * lookaheadMeters
            val pacerDistInWindow = riderOffsetInWindow + pacerRelDist
            val pacerProg = (pacerDistInWindow / lookaheadMeters).toFloat().coerceIn(0f, 1f)
            
            val pacerIdx = (pacerProg * (totalMicroSamples - 1)).toInt().coerceIn(0, totalMicroSamples - 1)
            val px = xFront[pacerIdx]
            val py = yFront[pacerIdx]
            
            // Draw Ghost Beacon
            canvas.drawCircle(px, py - 4f, 6f, virtualPacerPaint)
            // Draw beam
            val beamPath = android.graphics.Path()
            beamPath.moveTo(px, py)
            beamPath.lineTo(px - 15f, py - 60f)
            beamPath.lineTo(px + 15f, py - 60f)
            beamPath.close()
            val oldColor = virtualPacerPaint.color
            virtualPacerPaint.color = Color.parseColor("#3022D3EE")
            canvas.drawPath(beamPath, virtualPacerPaint)
            virtualPacerPaint.color = oldColor
        }
        
        // PRO FEATURE: Oasis Tracking Hologram
        oasisDistanceToNextCrucible?.let { oasisDist ->
            val distStr = if (oasisDist >= 1000) String.format("%.1f km", oasisDist / 1000.0) else " m"
            val text = "❄️ OASIS: \ HASTA EL MURO"
            canvas.drawText(text, w / 2f, h * 0.15f, oasisHoloPaint)
        }
    }'''
content = content.replace(draw_orig, draw_new)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Altimetria3DView patched")
