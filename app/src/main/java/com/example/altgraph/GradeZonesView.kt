package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.View
import kotlin.math.max

class GradeZonesView(context: Context) : View(context) {

    private var pctDist = DoubleArray(8) { 0.0 }
    private var pctTime = DoubleArray(8) { 0.0 }
    private var rawTimeSec = LongArray(8) { 0L }
    
    private val paintBar = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    
    private val paintText = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        val prefs = AppPreferences.getInstance(context)
        FontHelper.applyFontToPaint(this, prefs.fontFamilyKey, Typeface.BOLD)
    }

    private val rectF = RectF()

    fun updateData(newPctDist: DoubleArray, newPctTime: DoubleArray, newRawTimeSec: LongArray) {
        pctDist = newPctDist.clone()
        pctTime = newPctTime.clone()
        rawTimeSec = newRawTimeSec.clone()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val w = width.toFloat()
        val h = height.toFloat()
        
        if (w == 0f || h == 0f) return

        val numZones = GradeZone.values().size
        val gap = 6f
        val totalGaps = gap * (numZones - 1)
        val barHeight = (h - totalGaps) / numZones
        
        // Aumentar un poco el tamaño de texto
        paintText.textSize = minOf(34f, barHeight * 0.9f)
        
        var currentY = 0f

        // Draw from highest severity (WALL) to lowest (DESCENT) to have hardest at the top
        val reversedZones = GradeZone.values().reversedArray()

        for (i in reversedZones.indices) {
            val zone = reversedZones[i]
            val dist = pctDist[zone.id]
            val time = pctTime[zone.id]
            val secs = rawTimeSec[zone.id]
            
            // Background bar (dark grey)
            paintBar.color = Color.parseColor("#333333")
            rectF.set(0f, currentY, w, currentY + barHeight)
            canvas.drawRoundRect(rectF, 4f, 4f, paintBar)

            // Set the zone color
            paintBar.color = Color.parseColor(zone.colorHex)

            // ALWAYS draw a minimum 12px indicator on the left so colors are visible at 0%
            var barW = 12f
            
            // Fill bar further based on DISTANCE percentage
            if (dist > 0.0) {
                barW = max((w * (dist / 100.0)).toFloat(), 12f)
            }
            
            rectF.set(0f, currentY, barW, currentY + barHeight)
            canvas.drawRoundRect(rectF, 4f, 4f, paintBar)

            // Time formatting (mm:ss or h:mm:ss)
            val timeStr = if (secs > 3600) {
                String.format("%d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60)
            } else {
                String.format("%02d:%02d", secs / 60, secs % 60)
            }

            // Draw Text Label
            val localizedLabel = context.getString(zone.labelResId)
            val textStr = "$localizedLabel: ${"%.1f".format(dist)}% D | ${"%.1f".format(time)}% T - ($timeStr)"
            
            // Center text vertically
            val textY = currentY + (barHeight / 2) - ((paintText.descent() + paintText.ascent()) / 2)
            canvas.drawText(textStr, 20f, textY, paintText)

            currentY += barHeight + gap
        }
    }
}
