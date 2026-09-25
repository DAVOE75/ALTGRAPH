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
    var isMapRightSide: Boolean = true // Assumes it is placed on the right of the map

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
        textSize = 24f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 0f, 2f, Color.BLACK)
    }
    
    private val chevronPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FBC02D") // Karoo yellow-ish
        style = Paint.Style.FILL
        setShadowLayer(4f, 0f, 2f, Color.BLACK)
    }
    
    private val chevronOutline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val strat = strategyData ?: return
        
        val w = width.toFloat()
        val h = height.toFloat()
        
        // Ribbon takes 45% of width. If placed on the right of the map, 
        // we might align it to the left edge of this data field slot to be closer to the map.
        val barWidth = w * 0.45f
        val barLeft = if (isMapRightSide) w * 0.1f else w * 0.45f
        val barRight = barLeft + barWidth
        
        val blocks = strat.subBlocks
        if (blocks.isEmpty()) return
        
        val numBlocks = blocks.size
        val blockHeight = h / numBlocks.toFloat()
        
        for (i in blocks.indices) {
            val grade = blocks[i]
            val colorHex = GradeColorScale.getColorHex(grade.toDouble())
            segmentPaint.color = Color.parseColor(colorHex)
            
            val bottom = h - (i * blockHeight)
            val top = bottom - blockHeight
            
            canvas.drawRect(barLeft, top, barRight, bottom, segmentPaint)
        }
        
        // Draw the outline around the entire bar
        canvas.drawRect(barLeft, 0f, barRight, h, outlinePaint)
        
        // Every 1km (or 20 blocks if each is 50m), draw a small line and label
        // Actually, we use strat.subBlockSizeMeters
        val blockSize = strat.subBlockSizeMeters
        val blocksPerKm = (1000.0 / blockSize).toInt()
        
        if (blocksPerKm > 0) {
            for (i in 0 until numBlocks step blocksPerKm) {
                if (i == 0) continue
                val kmLabel = "${i / blocksPerKm}km"
                val py = h - (i * blockHeight)
                
                // Draw tick
                canvas.drawLine(barLeft, py, barRight, py, outlinePaint)
                
                // Draw text outside the bar
                val tx = if (isMapRightSide) barRight + (w * 0.25f) else barLeft - (w * 0.25f)
                canvas.drawText(kmLabel, tx, py + 8f, textPaint)
            }
        }
        
        // Draw chevron (Cyclist) at the bottom pointing up
        val path = Path()
        val cx = barLeft + (barWidth / 2f)
        val cy = h - 20f
        val cWidth = barWidth * 0.9f
        val cHeight = 25f
        
        path.moveTo(cx, cy - cHeight) // Top point
        path.lineTo(cx + cWidth, cy + cHeight) // Bottom right
        path.lineTo(cx, cy + (cHeight * 0.3f)) // Inner bottom
        path.lineTo(cx - cWidth, cy + cHeight) // Bottom left
        path.close()
        
        canvas.drawPath(path, chevronPaint)
        canvas.drawPath(path, chevronOutline)
    }
}
