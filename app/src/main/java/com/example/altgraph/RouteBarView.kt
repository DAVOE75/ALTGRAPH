package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.view.View
import kotlin.math.roundToInt

class RouteBarView(context: Context) : View(context) {

    var strategyData: StrategyData? = null

    private val segmentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    
    private val headerOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(160, 0, 0, 0)
        style = Paint.Style.FILL
    }

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
        alpha = 200
    }
    
    private val percentTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 42f
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 0f, 2f, Color.BLACK)
    }

    private val tickTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
        setShadowLayer(3f, 0f, 2f, Color.BLACK)
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

    data class ColorBand(
        val startIndex: Int,
        var endIndex: Int,
        val colorHex: String,
        var sumGrade: Double
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Aplicar fuente del usuario a los textos
        val fontFamilyKey = AppPreferences.getInstance(context).fontFamilyKey
        FontHelper.applyFontToPaint(percentTextPaint, fontFamilyKey, Typeface.BOLD)
        FontHelper.applyFontToPaint(tickTextPaint, fontFamilyKey, Typeface.NORMAL)
        
        val strat = strategyData ?: return
        
        val w = width.toFloat()
        val h = height.toFloat()
        val blocks = strat.subBlocks
        if (blocks.isEmpty()) return
        
        val numBlocks = blocks.size
        val isHorizontal = w > h

        // Agrupar por colores para calcular la pendiente media de cada franja visual
        val bands = mutableListOf<ColorBand>()
        var currentColor = GradeColorScale.getColorHex(blocks[0].toDouble())
        var currentBand = ColorBand(0, 0, currentColor, blocks[0].toDouble())
        bands.add(currentBand)
        
        for (i in 1 until numBlocks) {
            val grade = blocks[i].toDouble()
            val color = GradeColorScale.getColorHex(grade)
            if (color == currentColor) {
                currentBand.endIndex = i
                currentBand.sumGrade += grade
            } else {
                currentColor = color
                currentBand = ColorBand(i, i, currentColor, grade)
                bands.add(currentBand)
            }
        }

        if (isHorizontal) {
            // HORIZONTAL MODE
            val blockWidth = w / numBlocks.toFloat()
            val headerHeight = h * 0.45f
            
            // 1. Dibujar franjas de color (Fondo completo)
            for (band in bands) {
                val left = band.startIndex * blockWidth
                val right = (band.endIndex + 1) * blockWidth
                segmentPaint.color = Color.parseColor(band.colorHex)
                canvas.drawRect(left, 0f, right + 1f, h, segmentPaint) // +1f para evitar huecos
            }

            // 2. Dibujar overlay oscuro superior
            canvas.drawRect(0f, 0f, w, headerHeight, headerOverlayPaint)

            // 3. Dibujar línea divisoria
            canvas.drawLine(0f, headerHeight, w, headerHeight, dividerPaint)

            // 4. Dibujar marcadores kilométricos y textos de %
            val blocksPerKm = (1000.0 / strat.subBlockSizeMeters).toInt()
            
            // Textos de porcentaje medio en el header oscuro
            val cyPercent = (headerHeight / 2f) + (percentTextPaint.textSize / 3f)
            for (band in bands) {
                val left = band.startIndex * blockWidth
                val right = (band.endIndex + 1) * blockWidth
                val bandWidth = right - left
                
                if (bandWidth > 50f) {
                    val count = band.endIndex - band.startIndex + 1
                    val avgGrade = (band.sumGrade / count).roundToInt()
                    canvas.drawText("$avgGrade%", left + (bandWidth / 2f), cyPercent, percentTextPaint)
                }
            }

            // Ticks de distancia colgando de la divisoria
            if (blocksPerKm > 0) {
                tickTextPaint.textAlign = Paint.Align.CENTER
                for (i in blocksPerKm until numBlocks step blocksPerKm) {
                    val px = i * blockWidth
                    // Línea de marca hacia abajo
                    canvas.drawLine(px, headerHeight, px, headerHeight + 15f, dividerPaint)
                    // Texto debajo de la marca (en la zona de color, pero con sombra)
                    val kmLabel = "${i / blocksPerKm}k"
                    val cyTick = headerHeight + 20f + tickTextPaint.textSize
                    canvas.drawText(kmLabel, px, cyTick, tickTextPaint)
                }
            }

            // 5. Indicador de posición (Ciclista) a la izquierda del todo
            val path = Path()
            val cWidth = 35f
            val cHeight = h * 0.4f
            val cyCyc = headerHeight + (h - headerHeight) / 2f
            path.moveTo(cWidth, cyCyc) // Punta
            path.lineTo(5f, cyCyc - (cHeight / 2f)) // Arriba
            path.lineTo(15f, cyCyc) // Centro interno
            path.lineTo(5f, cyCyc + (cHeight / 2f)) // Abajo
            path.close()
            canvas.drawPath(path, cyclistPaint)
            canvas.drawPath(path, cyclistOutline)
            
        } else {
            // VERTICAL MODE
            val blockHeight = h / numBlocks.toFloat()
            val headerWidth = w * 0.45f
            
            // 1. Dibujar franjas de color
            for (band in bands) {
                val top = h - ((band.endIndex + 1) * blockHeight)
                val bottom = h - (band.startIndex * blockHeight)
                segmentPaint.color = Color.parseColor(band.colorHex)
                canvas.drawRect(0f, top - 1f, w, bottom, segmentPaint)
            }

            // 2. Dibujar overlay oscuro izquierdo
            canvas.drawRect(0f, 0f, headerWidth, h, headerOverlayPaint)

            // 3. Línea divisoria
            canvas.drawLine(headerWidth, 0f, headerWidth, h, dividerPaint)

            // 4. Textos de % en el panel oscuro
            val cxPercent = headerWidth / 2f
            for (band in bands) {
                val top = h - ((band.endIndex + 1) * blockHeight)
                val bottom = h - (band.startIndex * blockHeight)
                val bandHeight = bottom - top
                
                if (bandHeight > 40f) {
                    val count = band.endIndex - band.startIndex + 1
                    val avgGrade = (band.sumGrade / count).roundToInt()
                    val cyPercent = top + (bandHeight / 2f) + (percentTextPaint.textSize / 3f)
                    canvas.drawText("$avgGrade%", cxPercent, cyPercent, percentTextPaint)
                }
            }

            // Ticks de distancia
            val blocksPerKm = (1000.0 / strat.subBlockSizeMeters).toInt()
            if (blocksPerKm > 0) {
                tickTextPaint.textAlign = Paint.Align.LEFT
                for (i in blocksPerKm until numBlocks step blocksPerKm) {
                    val py = h - (i * blockHeight)
                    canvas.drawLine(headerWidth, py, headerWidth + 15f, py, dividerPaint)
                    val kmLabel = "${i / blocksPerKm}k"
                    val cyTick = py + (tickTextPaint.textSize / 3f)
                    canvas.drawText(kmLabel, headerWidth + 20f, cyTick, tickTextPaint)
                }
            }

            // 5. Ciclista en la base
            val path = Path()
            val cxCyc = headerWidth + (w - headerWidth) / 2f
            val cyCycBase = h - 5f
            val cWidth = (w - headerWidth) * 0.7f
            val cHeight = 35f
            path.moveTo(cxCyc, cyCycBase - cHeight) // Punta
            path.lineTo(cxCyc + (cWidth / 2f), cyCycBase) // Derecha
            path.lineTo(cxCyc, cyCycBase - 10f) // Centro interno
            path.lineTo(cxCyc - (cWidth / 2f), cyCycBase) // Izquierda
            path.close()
            canvas.drawPath(path, cyclistPaint)
            canvas.drawPath(path, cyclistOutline)
        }
    }
}
