package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.view.View

class RouteBarView(context: Context) : View(context) {

    var strategyData: StrategyData? = null

    private val segmentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 28f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 0f, 2f, Color.BLACK)
    }
    
    private val chevronOutline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(150, 0, 0, 0) // Semi-transparent black for inner chevrons
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeJoin = Paint.Join.ROUND
    }
    
    private val cyclistPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FBC02D")
        style = Paint.Style.FILL
        setShadowLayer(4f, 0f, 2f, Color.BLACK)
    }
    
    private val cyclistOutline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val strat = strategyData ?: return
        
        val w = width.toFloat()
        val h = height.toFloat()
        val blocks = strat.subBlocks
        if (blocks.isEmpty()) return
        
        val numBlocks = blocks.size
        
        val isHorizontal = w > h

        if (isHorizontal) {
            // HORIZONTAL MODE (e.g. bottom map slot)
            val barHeight = h * 0.4f
            val barTop = (h - barHeight) / 2f
            val barBottom = barTop + barHeight
            val blockWidth = w / numBlocks.toFloat()
            
            // Draw Blocks (Left to Right)
            for (i in blocks.indices) {
                val grade = blocks[i]
                segmentPaint.color = Color.parseColor(GradeColorScale.getColorHex(grade.toDouble()))
                val left = i * blockWidth
                val right = left + blockWidth
                canvas.drawRect(left, barTop, right + 1f, barBottom, segmentPaint) // +1f to prevent gaps
            }
            
            // Draw Outline
            canvas.drawRect(0f, barTop, w, barBottom, outlinePaint)
            
            // Draw Distance Markers (1km, 2km...)
            val blocksPerKm = (1000.0 / strat.subBlockSizeMeters).toInt()
            if (blocksPerKm > 0) {
                for (i in 0 until numBlocks step blocksPerKm) {
                    if (i == 0) continue
                    val kmLabel = "${i / blocksPerKm}k"
                    val px = i * blockWidth
                    canvas.drawLine(px, barTop, px, barBottom, outlinePaint)
                    canvas.drawText(kmLabel, px, barTop - 8f, textPaint)
                }
            }
            
            // Draw Directional Chevrons ( > > > )
            val chevronSpacing = w / 6f
            for (i in 1..5) {
                val cx = i * chevronSpacing
                val path = Path()
                path.moveTo(cx - 10f, barTop + 5f)
                path.lineTo(cx + 10f, barTop + (barHeight / 2f))
                path.lineTo(cx - 10f, barBottom - 5f)
                canvas.drawPath(path, chevronOutline)
            }
            
            // Draw Cyclist Triangle pointing Right at the far left
            val path = Path()
            val cy = barTop + (barHeight / 2f)
            val cWidth = 35f
            val cHeight = barHeight * 0.9f
            path.moveTo(cWidth, cy) // Point Right
            path.lineTo(5f, cy - (cHeight / 2f)) // Top Left
            path.lineTo(15f, cy) // Inner Left
            path.lineTo(5f, cy + (cHeight / 2f)) // Bottom Left
            path.close()
            canvas.drawPath(path, cyclistPaint)
            canvas.drawPath(path, cyclistOutline)
            
        } else {
            // VERTICAL MODE (e.g. side map slot)
            val barWidth = w * 0.4f
            val barLeft = (w - barWidth) / 2f
            val barRight = barLeft + barWidth
            val blockHeight = h / numBlocks.toFloat()
            
            // Draw Blocks (Bottom to Top)
            for (i in blocks.indices) {
                val grade = blocks[i]
                segmentPaint.color = Color.parseColor(GradeColorScale.getColorHex(grade.toDouble()))
                val bottom = h - (i * blockHeight)
                val top = bottom - blockHeight
                canvas.drawRect(barLeft, top - 1f, barRight, bottom, segmentPaint) // -1f to prevent gaps
            }
            
            // Draw Outline
            canvas.drawRect(barLeft, 0f, barRight, h, outlinePaint)
            
            // Draw Distance Markers (1km, 2km...)
            val blocksPerKm = (1000.0 / strat.subBlockSizeMeters).toInt()
            if (blocksPerKm > 0) {
                for (i in 0 until numBlocks step blocksPerKm) {
                    if (i == 0) continue
                    val kmLabel = "${i / blocksPerKm}k"
                    val py = h - (i * blockHeight)
                    canvas.drawLine(barLeft, py, barRight, py, outlinePaint)
                    canvas.drawText(kmLabel, barRight + (w * 0.25f), py + 10f, textPaint)
                }
            }
            
            // Draw Directional Chevrons ( ^ ^ ^ )
            val chevronSpacing = h / 6f
            for (i in 1..5) {
                val cy = h - (i * chevronSpacing)
                val path = Path()
                path.moveTo(barLeft + 5f, cy + 10f)
                path.lineTo(barLeft + (barWidth / 2f), cy - 10f)
                path.lineTo(barRight - 5f, cy + 10f)
                canvas.drawPath(path, chevronOutline)
            }
            
            // Draw Cyclist Triangle pointing Up at the bottom
            val path = Path()
            val cx = barLeft + (barWidth / 2f)
            val cy = h - 25f
            val cWidth = barWidth * 0.9f
            val cHeight = 35f
            path.moveTo(cx, h - cHeight - 10f) // Top point
            path.lineTo(cx + (cWidth / 2f), h - 10f) // Bottom right
            path.lineTo(cx, h - 20f) // Inner bottom
            path.lineTo(cx - (cWidth / 2f), h - 10f) // Bottom left
            path.close()
            canvas.drawPath(path, cyclistPaint)
            canvas.drawPath(path, cyclistOutline)
        }
    }
}
