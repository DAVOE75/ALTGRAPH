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
    
    private val pathLine = Path()
    private val pathFill = Path()
    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
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
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        textSize = 18f
    }
    private val kmTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 16f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }
    private val dashLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 2f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
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

        val minElev = profileElevations.minOrNull() ?: 0f
        val maxElev = profileElevations.maxOrNull() ?: 0f
        // Add 50% padding to elevRange to avoid exaggerating the Y axis
        val elevRange = (maxElev - minElev).coerceAtLeast(10f) * 1.5f

        val ptsCount = profileElevations.size
        
        pathLine.reset()
        pathFill.reset()
        
        // Add more padding at the top for the text
        val startX = 0f
        val endX = drawW
        val bottomY = drawH - 20f
        val topY = 80f // leave space for text

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
        
        // Draw icon or dot at current position
        val currentElevIndex = (progress * (ptsCount - 1)).toInt().coerceIn(0, ptsCount - 1)
        val currElev = profileElevations[currentElevIndex]
        val currY = bottomY - ((currElev - minElev) / elevRange) * (bottomY - topY)
        
        // Vertical line indicating cyclist position
        canvas.drawLine(currentX, bottomY, currentX, currY, dashLinePaint)
        
        // Text on vertical line (km)
        val kmText = String.format("%.1f km", currentDistance / 1000.0)
        // Draw background for text
        val textWidth = kmTextPaint.measureText(kmText)
        var tx = currentX
        if (tx - textWidth / 2f < 0f) tx = textWidth / 2f
        if (tx + textWidth / 2f > drawW) tx = drawW - textWidth / 2f
        
        val bgPaint = Paint().apply { color = Color.parseColor("#88000000"); style = Paint.Style.FILL }
        canvas.drawRect(tx - textWidth / 2f - 4f, currY - 30f, tx + textWidth / 2f + 4f, currY - 10f, bgPaint)
        canvas.drawText(kmText, tx, currY - 15f, kmTextPaint)

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
        val distText = String.format("%.1f km - Altitude gain %.0f mt", totalDistance / 1000.0, altitudeGain)
        canvas.drawText(distText, 20f, 55f, subTextPaint)

        canvas.restore()
    }
}
