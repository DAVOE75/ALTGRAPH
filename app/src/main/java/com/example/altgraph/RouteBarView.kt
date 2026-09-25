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
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 42f // Aumentado para que los números se vean más grandes
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        setShadowLayer(5f, 0f, 2f, Color.BLACK)
    }
    
    private val chevronOutline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(120, 0, 0, 0)
        style = Paint.Style.STROKE
        strokeWidth = 5f
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
        strokeWidth = 3f
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
            // HORIZONTAL MODE
            // Reducimos el alto de la franja a un 60% para que sea más estrecha (el resto lo pintará Karoo de negro)
            val barHeight = h * 0.60f
            val barTop = (h - barHeight) / 2f
            val barBottom = barTop + barHeight
            
            val blockWidth = w / numBlocks.toFloat()
            val cy = (h / 2f) + (textPaint.textSize / 3f)
            
            var currentGrade = blocks[0].toInt()
            var currentLeft = 0f
            
            // Agrupar bloques
            for (i in 0..numBlocks) {
                val grade = if (i < numBlocks) blocks[i].toInt() else -999
                
                if (grade != currentGrade) {
                    val right = i * blockWidth
                    
                    segmentPaint.color = Color.parseColor(GradeColorScale.getColorHex(currentGrade.toDouble()))
                    canvas.drawRect(currentLeft, barTop, right + 1f, barBottom, segmentPaint) // +1f to prevent gaps
                    
                    val bandWidth = right - currentLeft
                    if (bandWidth > 40f) {
                        canvas.drawText("$currentGrade%", currentLeft + (bandWidth / 2f), cy, textPaint)
                    }
                    
                    currentGrade = grade
                    currentLeft = right
                }
            }
            
            // Draw Directional Chevrons
            val chevronSpacing = w / 6f
            for (i in 1..5) {
                val cx = i * chevronSpacing
                val path = Path()
                path.moveTo(cx - 15f, barTop + 10f)
                path.lineTo(cx + 15f, h / 2f)
                path.lineTo(cx - 15f, barBottom - 10f)
                canvas.drawPath(path, chevronOutline)
            }
            
            // Draw Cyclist Triangle
            val path = Path()
            val cWidth = 45f
            val cHeight = barHeight * 0.9f
            val cyCyc = h / 2f
            path.moveTo(cWidth, cyCyc)
            path.lineTo(5f, cyCyc - (cHeight / 2f))
            path.lineTo(15f, cyCyc)
            path.lineTo(5f, cyCyc + (cHeight / 2f))
            path.close()
            canvas.drawPath(path, cyclistPaint)
            canvas.drawPath(path, cyclistOutline)
            
        } else {
            // VERTICAL MODE
            val barWidth = w * 0.60f
            val barLeft = (w - barWidth) / 2f
            val barRight = barLeft + barWidth
            
            val blockHeight = h / numBlocks.toFloat()
            val cx = w / 2f
            
            var currentGrade = blocks[0].toInt()
            var currentBottom = h
            
            for (i in 0..numBlocks) {
                val grade = if (i < numBlocks) blocks[i].toInt() else -999
                
                if (grade != currentGrade) {
                    val top = h - (i * blockHeight)
                    
                    segmentPaint.color = Color.parseColor(GradeColorScale.getColorHex(currentGrade.toDouble()))
                    canvas.drawRect(barLeft, top - 1f, barRight, currentBottom, segmentPaint)
                    
                    val bandHeight = currentBottom - top
                    if (bandHeight > 40f) {
                        val cy = top + (bandHeight / 2f) + (textPaint.textSize / 3f)
                        canvas.drawText("$currentGrade%", cx, cy, textPaint)
                    }
                    
                    currentGrade = grade
                    currentBottom = top
                }
            }
            
            // Draw Directional Chevrons
            val chevronSpacing = h / 6f
            for (i in 1..5) {
                val cy = h - (i * chevronSpacing)
                val path = Path()
                path.moveTo(barLeft + 10f, cy + 15f)
                path.lineTo(cx, cy - 15f)
                path.lineTo(barRight - 10f, cy + 15f)
                canvas.drawPath(path, chevronOutline)
            }
            
            // Draw Cyclist Triangle
            val path = Path()
            val cyCyc = h - 25f
            val cWidth = barWidth * 0.9f
            val cHeight = 45f
            path.moveTo(cx, h - cHeight - 10f)
            path.lineTo(cx + (cWidth / 2f), h - 10f)
            path.lineTo(cx, h - 20f)
            path.lineTo(cx - (cWidth / 2f), h - 10f)
            path.close()
            canvas.drawPath(path, cyclistPaint)
            canvas.drawPath(path, cyclistOutline)
        }
    }
}
