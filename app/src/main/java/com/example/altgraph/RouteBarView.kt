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
        color = Color.argb(40, 0, 0, 0)
        style = Paint.Style.FILL
    }
    
    private val pastOverlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 0, 0, 0) // Oscurece el tramo ya recorrido
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
        textSize = 28f
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

        val ruleBackgroundPaint = Paint().apply { color = Color.parseColor("#151515"); style = Paint.Style.FILL }

        if (isHorizontal) {
            // HORIZONTAL MODE
            val radarH = h * 0.60f
            val headerHeight = radarH * 0.50f
            
            // 1. Dibujar franjas de color SOLAMENTE en la franja superior (Top Band)
            val blockWidth = w / numBlocks.toFloat()
            for (band in bands) {
                val left = band.startIndex * blockWidth
                val right = (band.endIndex + 1) * blockWidth
                segmentPaint.color = Color.parseColor(band.colorHex)
                canvas.drawRect(left, 0f, right + 1f, headerHeight, segmentPaint) // +1f para evitar huecos
            }

            // 2. Dibujar overlay oscuro (pasado) a la izquierda de la flecha
            val cWidth = 30f
            val riderX = (w * strat.riderProgress).coerceAtLeast(cWidth / 2f).coerceAtMost(w - (cWidth / 2f))
            if (riderX > cWidth / 2f) {
                // Oscurecer lo que queda atrás (solo en la franja superior donde hay colores)
                canvas.drawRect(0f, 0f, riderX, headerHeight, pastOverlayPaint)
            }

            // 3. Textos de porcentaje medio en la franja superior (sin el fondo oscurecido general)
            percentTextPaint.setShadowLayer(4f, 0f, 2f, Color.BLACK) // Sombra fuerte para legibilidad sin fondo oscuro
            val cyPercent = (headerHeight / 2f) + (percentTextPaint.textSize / 3f)
            for (band in bands) {
                val left = band.startIndex * blockWidth
                val right = (band.endIndex + 1) * blockWidth
                val bandWidth = right - left
                
                if (bandWidth > 50f && band.colorHex != "#111111") {
                    val count = band.endIndex - band.startIndex + 1
                    val avgGrade = (Math.round(band.sumGrade / count)).toInt()
                    canvas.drawText("${avgGrade}%", left + (bandWidth / 2f), cyPercent, percentTextPaint)
                }
            }

            // 4. Franja Intermedia (Regla / Ruler)
            canvas.drawRect(0f, headerHeight, w, radarH, ruleBackgroundPaint)
            // Línea divisoria blanca
            canvas.drawLine(0f, headerHeight, w, headerHeight, dividerPaint)

            // Ticks de distancia (Escala dinámica)
            val lookahead = strat.subBlockSizeMeters * strat.subBlocks.size
            val tickInterval = when {
                lookahead <= 350 -> 50.0
                lookahead <= 700 -> 100.0
                lookahead <= 1000 -> 250.0
                lookahead <= 2000 -> 500.0
                lookahead <= 5000 -> 1000.0
                else -> 2000.0
            }
            val startTick = Math.ceil(strat.windowStartMeters / tickInterval) * tickInterval
            var tickDist = startTick
            tickTextPaint.textAlign = Paint.Align.CENTER
            while (tickDist < strat.windowStartMeters + lookahead) {
                val progress = (tickDist - strat.windowStartMeters) / lookahead
                val px = (w * progress).toFloat()
                // Línea de marca hacia abajo
                canvas.drawLine(px, headerHeight, px, headerHeight + 15f, dividerPaint)
                // Texto
                if (tickDist >= 0) {
                    val kmLabel = if (tickDist >= 1000) "${(tickDist / 1000).toInt()}km" else "${tickDist.toInt()}m"
                    val cyTick = headerHeight + 20f + tickTextPaint.textSize
                    canvas.drawText(kmLabel, px, cyTick, tickTextPaint)
                }
                tickDist += tickInterval
            }

            // 5. Indicador de posición (Ciclista)
            val path = Path()
            val cHeight = headerHeight * 0.8f
            path.moveTo(riderX, headerHeight) // Punta abajo (tocando la divisoria)
            path.lineTo(riderX - (cWidth / 2f), 0f) // Esquina superior izq
            path.lineTo(riderX, 10f) // Centro arriba
            path.lineTo(riderX + (cWidth / 2f), 0f) // Esquina superior der
            path.close()
            canvas.drawPath(path, cyclistPaint)
            canvas.drawPath(path, cyclistOutline)
            
            // 6. Alertas dinámicas
            drawAlerts(canvas, strat, isHorizontal = true, w, h)
            
        } else {
            // VERTICAL MODE
            val radarW = w * 0.60f
            val headerWidth = radarW * 0.50f
            val blockHeight = h / numBlocks.toFloat()
            
            // 1. Dibujar franjas de color SOLAMENTE en la franja izquierda
            for (band in bands) {
                val top = h - ((band.endIndex + 1) * blockHeight)
                val bottom = h - (band.startIndex * blockHeight)
                segmentPaint.color = Color.parseColor(band.colorHex)
                canvas.drawRect(0f, top - 1f, headerWidth, bottom, segmentPaint)
            }

            // 2. Oscurecer lo que queda atrás (abajo)
            val cHeight = 30f
            val riderY = (h - (h * strat.riderProgress)).coerceAtLeast(cHeight / 2f).coerceAtMost(h - (cHeight / 2f))
            if (riderY < h - (cHeight / 2f)) {
                canvas.drawRect(0f, riderY, headerWidth, h, pastOverlayPaint)
            }

            // 3. Textos de %
            percentTextPaint.setShadowLayer(4f, 0f, 2f, Color.BLACK)
            val cxPercent = headerWidth / 2f
            for (band in bands) {
                val top = h - ((band.endIndex + 1) * blockHeight)
                val bottom = h - (band.startIndex * blockHeight)
                val bandHeight = bottom - top
                
                if (bandHeight > 40f && band.colorHex != "#111111") {
                    val count = band.endIndex - band.startIndex + 1
                    val avgGrade = (Math.round(band.sumGrade / count)).toInt()
                    val cyPercent = top + (bandHeight / 2f) + (percentTextPaint.textSize / 3f)
                    canvas.drawText("${avgGrade}%", cxPercent, cyPercent, percentTextPaint)
                }
            }

            // 4. Franja Intermedia (Regla)
            canvas.drawRect(headerWidth, 0f, radarW, h, ruleBackgroundPaint)
            canvas.drawLine(headerWidth, 0f, headerWidth, h, dividerPaint)

            // Ticks de distancia
            val lookahead = strat.subBlockSizeMeters * strat.subBlocks.size
            val tickInterval = when {
                lookahead <= 350 -> 50.0
                lookahead <= 700 -> 100.0
                lookahead <= 1000 -> 250.0
                lookahead <= 2000 -> 500.0
                lookahead <= 5000 -> 1000.0
                else -> 2000.0
            }
            val startTick = Math.ceil(strat.windowStartMeters / tickInterval) * tickInterval
            var tickDist = startTick
            tickTextPaint.textAlign = Paint.Align.LEFT
            while (tickDist < strat.windowStartMeters + lookahead) {
                val progress = (tickDist - strat.windowStartMeters) / lookahead
                val py = h - (h * progress).toFloat()
                canvas.drawLine(headerWidth, py, headerWidth + 15f, py, dividerPaint)
                if (tickDist >= 0) {
                    val kmLabel = if (tickDist >= 1000) "${(tickDist / 1000).toInt()}km" else "${tickDist.toInt()}m"
                    val cyTick = py + (tickTextPaint.textSize / 3f)
                    canvas.drawText(kmLabel, headerWidth + 20f, cyTick, tickTextPaint)
                }
                tickDist += tickInterval
            }

            // 5. Indicador de posición (Ciclista)
            val path = Path()
            val cWidth = headerWidth * 0.8f
            path.moveTo(headerWidth, riderY) // Punta izq (tocando la divisoria)
            path.lineTo(headerWidth - cWidth, riderY + (cHeight / 2f)) // Esquina abajo
            path.lineTo(headerWidth - cWidth + 10f, riderY) // Centro izq (muesca)
            path.lineTo(headerWidth - cWidth, riderY - (cHeight / 2f)) // Esquina arriba
            path.close()
            canvas.drawPath(path, cyclistPaint)
            canvas.drawPath(path, cyclistOutline)
            
            // 6. Alertas dinámicas
            drawAlerts(canvas, strat, isHorizontal = false, w, h)
        }
    }

    private fun formatDist(m: Double): String {
        return if (m < 1000) "${m.roundToInt()}m" else String.format("%.1fkm", m / 1000.0)
    }

    private fun drawAlerts(canvas: Canvas, strat: StrategyData, isHorizontal: Boolean, w: Float, h: Float) {
        val riderDist = strat.windowStartMeters + strat.riderProgress * (strat.subBlockSizeMeters * strat.subBlocks.size)
        
        var alertText = ""
        var alertColorHex = "#212121" // Default dark gray
        
        val activeClimb = strat.activeClimbs.find { riderDist >= it.startDistance && riderDist < it.endDistance }
        if (activeClimb != null) {
            val currentBlockIdx = (strat.riderProgress * strat.subBlocks.size).toInt()
            var steepDist: Double? = null
            var steepGrade: Float? = null
            
            for (i in currentBlockIdx until strat.subBlocks.size) {
                val grade = strat.subBlocks[i]
                if (grade >= 12f) {
                    val distToBlock = strat.windowStartMeters + (i * strat.subBlockSizeMeters) - riderDist
                    if (distToBlock > 0 && distToBlock < activeClimb.endDistance - riderDist) {
                        steepDist = distToBlock
                        steepGrade = grade
                        break
                    }
                }
            }
            
            if (steepDist != null && steepGrade != null && steepDist < 5000) {
                alertText = "Muro ${steepGrade.toInt()}% a ${formatDist(steepDist)}"
                alertColorHex = GradeColorScale.getColorHex(steepGrade.toDouble())
            } else {
                val distToTop = activeClimb.endDistance - riderDist
                alertText = "Coronar a ${formatDist(distToTop)}"
                alertColorHex = "#4CAF50" // Green
            }
        } else {
            val nextClimb = strat.activeClimbs.filter { it.startDistance > riderDist }.minByOrNull { it.startDistance }
            if (nextClimb != null) {
                val distToStart = nextClimb.startDistance - riderDist
                alertText = "Puerto a ${formatDist(distToStart)}"
                alertColorHex = "#FFC107" // Amber
            }
        }

        if (alertText.isNotEmpty()) {
            // Fondo de la franja: mezclamos el color de alerta con un poco de transparencia/blanco para que sea "más claro" (pero legible)
            val baseColor = Color.parseColor(alertColorHex)
            val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                // Hacemos el color algo translúcido o más suave
                val r = Color.red(baseColor)
                val g = Color.green(baseColor)
                val b = Color.blue(baseColor)
                val mix = 0.4f
                color = Color.rgb(
                    (r + (255 - r) * mix).toInt(),
                    (g + (255 - g) * mix).toInt(),
                    (b + (255 - b) * mix).toInt()
                )
                style = Paint.Style.FILL
            }
            
            val alertPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK // Texto negro sobre fondo claro
                textSize = 45f
                textAlign = Paint.Align.CENTER
                setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
                FontHelper.applyFontToPaint(this, AppPreferences.getInstance(context).fontFamilyKey, android.graphics.Typeface.BOLD)
            }

            if (isHorizontal) {
                // Franja inferior completa
                val radarH = h * 0.60f
                val rect = android.graphics.RectF(0f, radarH, w, h)
                canvas.drawRect(rect, bgPaint)
                
                // Texto centrado vertical y horizontalmente en la franja
                val cy = radarH + (h - radarH) / 2f + (alertPaint.textSize / 3f)
                canvas.drawText(alertText, w / 2f, cy, alertPaint)
            } else {
                // Franja derecha completa (vertical mode)
                val radarW = w * 0.60f
                val rect = android.graphics.RectF(radarW, 0f, w, h)
                canvas.drawRect(rect, bgPaint)
                
                // Texto centrado rotado o vertical
                // Para no complicarlo con rotación, lo centramos normal en la zona superior
                alertPaint.textAlign = Paint.Align.CENTER
                alertPaint.textSize = 35f
                canvas.drawText(alertText, radarW + (w - radarW) / 2f, 40f, alertPaint)
            }
        }
    }
}
