package com.example.altgraph

import android.content.Context
import android.graphics.*
import android.view.View

class RouteProfile2DView(context: Context) : View(context) {

    var profileElevations: FloatArray = FloatArray(0)
    var totalDistance: Double = 0.0
    var currentDistance: Double = 0.0
    var isRotated: Boolean = false
    var routeName: String = ""
    var altitudeGain: Double = 0.0
    var altitudeGainText: String = ""
    var fontFamilyKey: String = "sans-serif-condensed"
    var fontSizeScale: Float = 1.0f
    var activeClimbs: List<RouteClimb> = emptyList()
    
    private val pathLine = Path()
    private val pathFill = Path()
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00E676") // Bright green
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    private val futureLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#444444") // Dark grey
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }
    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
    }
    private val kmTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private val dashLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }
    private val peakTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F59E0B") // Amber color for category
        textAlign = Paint.Align.CENTER
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#22FFFFFF") // Very faint white
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }
    private val gridTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#55FFFFFF") // Faint white
        textAlign = Paint.Align.LEFT
    }

    private fun drawFlag(canvas: Canvas, cx: Float, cy: Float, colorStr: String) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL; color = Color.parseColor(colorStr) }
        val stick = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = 3f; color = Color.WHITE }
        canvas.drawLine(cx, cy, cx, cy - 30f, stick)
        val path = Path()
        path.moveTo(cx, cy - 30f)
        path.lineTo(cx + 15f, cy - 22f)
        path.lineTo(cx, cy - 15f)
        path.close()
        canvas.drawPath(path, p)
        canvas.drawCircle(cx, cy, 4f, p)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (profileElevations.isEmpty() || totalDistance <= 0) return

        val w = width.toFloat()
        val h = height.toFloat()

        canvas.save()
        var drawW = w
        var drawH = h
        if (isRotated) {
            canvas.translate(w, 0f)
            canvas.rotate(90f)
            drawW = h
            drawH = w
        }

        // Apply fonts dynamically
        FontHelper.applyFontToPaint(textPaint, fontFamilyKey, Typeface.BOLD)
        textPaint.textSize = 24f * fontSizeScale
        
        FontHelper.applyFontToPaint(subTextPaint, fontFamilyKey, Typeface.NORMAL)
        subTextPaint.textSize = 18f * fontSizeScale
        
        FontHelper.applyFontToPaint(kmTextPaint, fontFamilyKey, Typeface.BOLD)
        kmTextPaint.textSize = 22f * fontSizeScale // Larger as requested
        
        FontHelper.applyFontToPaint(peakTextPaint, fontFamilyKey, Typeface.BOLD)
        peakTextPaint.textSize = 16f * fontSizeScale
        
        FontHelper.applyFontToPaint(gridTextPaint, fontFamilyKey, Typeface.NORMAL)
        gridTextPaint.textSize = 14f * fontSizeScale

        val minElev = profileElevations.minOrNull() ?: 0f
        val maxElev = profileElevations.maxOrNull() ?: 0f
        // Add padding to elevRange to avoid exaggerating the Y axis
        val elevRange = (maxElev - minElev).coerceAtLeast(10f) * 2.5f

        val ptsCount = profileElevations.size
        
        pathLine.reset()
        pathFill.reset()
        
        val startX = 0f
        val endX = drawW
        val bottomY = drawH - 20f
        val topY = 100f // leave more space for text

        // Draw Y-Axis Grid
        val gridStep = when {
            elevRange > 2000 -> 500f
            elevRange > 1000 -> 250f
            elevRange > 500 -> 100f
            else -> 50f
        }
        val startGridElev = (minElev / gridStep).toInt() * gridStep
        var currGridElev = startGridElev
        while (currGridElev <= maxElev + gridStep) { // draw slightly above max if fits
            if (currGridElev >= minElev) {
                val gy = bottomY - ((currGridElev - minElev) / elevRange) * (bottomY - topY)
                if (gy in topY..bottomY) {
                    canvas.drawLine(0f, gy, drawW, gy, gridPaint)
                    canvas.drawText("${currGridElev.toInt()}m", 5f, gy - 5f, gridTextPaint)
                }
            }
            currGridElev += gridStep
        }

        // Build the full path
        for (i in 0 until ptsCount) {
            val elev = profileElevations[i]
            val x = startX + (i.toFloat() / (ptsCount - 1)) * drawW
            val y = bottomY - ((elev - minElev) / elevRange) * (bottomY - topY)
            
            if (i == 0) {
                pathLine.moveTo(x, y)
                pathFill.moveTo(x, bottomY)
                pathFill.lineTo(x, y)
            } else {
                pathLine.lineTo(x, y)
                pathFill.lineTo(x, y)
            }
            if (i == ptsCount - 1) {
                pathFill.lineTo(x, bottomY)
                pathFill.close()
            }
        }

        // Draw future outline
        canvas.drawPath(pathLine, futureLinePaint)

        // Calculate progress
        val progress = (currentDistance / totalDistance).toFloat().coerceIn(0f, 1f)
        val currentX = startX + progress * drawW

        // Draw filled area with gradient, clipped to current progress
        canvas.save()
        canvas.clipRect(startX, 0f, currentX, drawH)
        
        val gradient = LinearGradient(
            0f, topY, 0f, bottomY,
            Color.parseColor("#00E676"),
            Color.parseColor("#003311"),
            Shader.TileMode.CLAMP
        )
        fillPaint.shader = gradient
        canvas.drawPath(pathFill, fillPaint)
        
        // Draw the line over the filled area so it's bright
        canvas.drawPath(pathLine, linePaint)
        canvas.restore()

        // Draw peaks for climbs
        for (climb in activeClimbs) {
            val catUp = climb.category.uppercase()
            val mappedCat = when {
                catUp.contains("1") -> "1ª"
                catUp.contains("2") -> "2ª"
                catUp.contains("3") -> "3ª"
                catUp.contains("4") -> "4ª"
                catUp.contains("ESPECIAL") || catUp.contains("H.C") || catUp.contains("HC") -> "H.C."
                else -> null
            }
            if (mappedCat != null) {
                val endPct = (climb.endDistance / totalDistance).toFloat().coerceIn(0f, 1f)
                val peakIdx = (endPct * (ptsCount - 1)).toInt().coerceIn(0, ptsCount - 1)
                val px = startX + endPct * drawW
                val py = bottomY - ((profileElevations[peakIdx] - minElev) / elevRange) * (bottomY - topY)
                canvas.drawText(mappedCat, px, py - 10f, peakTextPaint)
            }
        }
        
        // Draw icon or dot at current position
        val currentElevIndex = (progress * (ptsCount - 1)).toInt().coerceIn(0, ptsCount - 1)
        val currElev = profileElevations[currentElevIndex]
        val currY = bottomY - ((currElev - minElev) / elevRange) * (bottomY - topY)
        
        // Vertical line indicating cyclist position
        val lineTopY = 30f // almost touching the top border
        canvas.drawLine(currentX, bottomY, currentX, lineTopY, dashLinePaint)
        
        // Text on vertical line (km) rotated -90 degrees
        val kmText = String.format("%.1f km", currentDistance / 1000.0)
        val textWidth = kmTextPaint.measureText(kmText)
        
        canvas.save()
        val textCenterY = 160f // lower it a bit more
        canvas.translate(currentX, textCenterY)
        // rotate -90 so it reads from bottom to top and goes to the LEFT of the line
        canvas.rotate(-90f)
        
        // background padding
        val bgPaint = Paint().apply { color = Color.parseColor("#88000000"); style = Paint.Style.FILL }
        // For -90 deg rotation, positive Y is LEFT on screen
        // So drawing from y=5 to y=35 places it to the LEFT of the line
        canvas.drawRect(-textWidth / 2f - 4f, 5f, textWidth / 2f + 4f, 35f, bgPaint)
        canvas.drawText(kmText, 0f, 30f, kmTextPaint) // text baseline at y=30
        canvas.restore()

        // Draw Dot
        canvas.drawCircle(currentX, currY, 8f, linePaint)
        val dotPaint = Paint().apply { color = Color.WHITE; style = Paint.Style.FILL }
        canvas.drawCircle(currentX, currY, 5f, dotPaint)

        // Draw Start and End Flags
        val firstY = bottomY - ((profileElevations.firstOrNull() ?: minElev) - minElev) / elevRange * (bottomY - topY)
        val lastY = bottomY - ((profileElevations.lastOrNull() ?: minElev) - minElev) / elevRange * (bottomY - topY)
        drawFlag(canvas, startX + 10f, firstY, "#00E676") // Start Flag (Green)
        drawFlag(canvas, endX - 10f, lastY, "#FFFFFF") // End Flag (White/Checkered)

        // Draw Top Texts
        val titleText = routeName.ifEmpty { "RUTA" }.uppercase()
        canvas.drawText(titleText, 20f, 30f, textPaint)
        
        val distText = String.format("Total %.1f km - %s", totalDistance / 1000.0, altitudeGainText)
        canvas.drawText(distText, 20f, 55f, subTextPaint)

        canvas.restore()
    }
}
