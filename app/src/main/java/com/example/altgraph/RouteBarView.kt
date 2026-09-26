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

    private val checkeredPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        val size = 20
        val bitmap = android.graphics.Bitmap.createBitmap(size * 2, size * 2, android.graphics.Bitmap.Config.ARGB_8888)
        val cvs = android.graphics.Canvas(bitmap)
        val pWhite = Paint().apply { color = Color.WHITE; style = Paint.Style.FILL }
        val pBlack = Paint().apply { color = Color.BLACK; style = Paint.Style.FILL }
        cvs.drawRect(0f, 0f, size.toFloat(), size.toFloat(), pWhite)
        cvs.drawRect(size.toFloat(), size.toFloat(), size * 2f, size * 2f, pWhite)
        cvs.drawRect(size.toFloat(), 0f, size * 2f, size.toFloat(), pBlack)
        cvs.drawRect(0f, size.toFloat(), size.toFloat(), size * 2f, pBlack)
        shader = android.graphics.BitmapShader(bitmap, android.graphics.Shader.TileMode.REPEAT, android.graphics.Shader.TileMode.REPEAT)
    }

    private val tickTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 28f
        textAlign = Paint.Align.CENTER
        setShadowLayer(3f, 0f, 2f, Color.BLACK)
    }

    // Paint for max-grade-per-block label (top-left of each band, only at 50m scale)
    private val maxGradeBlockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 24f
        textAlign = Paint.Align.LEFT
        setShadowLayer(4f, 0f, 2f, Color.WHITE) // White shadow so it pops on any color band
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

    // ── ELITE feature state (set by RouteBarDataField each frame) ────────────
    var ghostRelativeMeters: Double? = null   // +ahead / -behind in metres
    var showEnergyBar: Boolean = false
    var showPoiRuler: Boolean = false
    var showHistogram: Boolean = false

    // Ghost marker paint (semi-transparent rider arrow)
    private val ghostPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(180, 180, 180, 255)  // ghostly blue-white
        style = Paint.Style.FILL
        setShadowLayer(3f, 0f, 1f, Color.BLACK)
    }
    private val ghostOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(160, 100, 100, 200)
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    // POI icon paint
    private val poiTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 22f
        textAlign = Paint.Align.CENTER
        setShadowLayer(3f, 0f, 2f, Color.BLACK)
    }

    // Energy bar paints
    private val energyBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A1A1A")
        style = Paint.Style.FILL
    }

    // Histogram text paint
    private val histPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 20f
        textAlign = Paint.Align.CENTER
        setShadowLayer(2f, 0f, 1f, Color.BLACK)
    }

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
            
            // Draw checkered start pattern if we are before the actual route starts
            val totalLookahead = strat.subBlockSizeMeters * strat.subBlocks.size
            val startX = w * (0.0 - strat.windowStartMeters).toFloat() / totalLookahead.toFloat()
            if (startX > 0f) {
                canvas.drawRect(0f, 0f, startX, headerHeight, checkeredPaint)
            }
            
            // Draw checkered end pattern if the route finishes within the window
            var endX = w.toFloat()
            if (strat.routeTotalLength > 0.0) {
                endX = w * (strat.routeTotalLength - strat.windowStartMeters).toFloat() / totalLookahead.toFloat()
                if (endX < w) {
                    canvas.drawRect(endX, 0f, w.toFloat(), headerHeight, checkeredPaint)
                }
            }

            // 2. Dibujar overlay oscuro (pasado) a la izquierda de la flecha
            val cWidth = 30f
            var riderX = (w * strat.riderProgress).coerceAtLeast(cWidth / 2f).coerceAtMost(w - (cWidth / 2f))
            
            // Si la ruta está empezando (startX visible) y el ciclista está muy cerca del inicio (a menos de 25m),
            // clavamos la flecha exactamente en la línea de meta (0m) para que visualmente cuadre perfecto.
            if (startX > 0f) {
                val distMeters = (riderX - startX) * totalLookahead / w
                if (distMeters in 0f..25f) {
                    riderX = startX
                }
            }

            if (riderX > cWidth / 2f) {
                // Oscurecer lo que queda atrás (solo en la franja superior donde hay colores)
                // Hacemos que el overlay empiece desde startX para no oscurecer la bandera de cuadros
                canvas.drawRect(Math.max(0f, startX), 0f, riderX, headerHeight, pastOverlayPaint)
            }

            // 3. Textos de porcentaje medio en la franja superior (sin el fondo oscurecido general)
            percentTextPaint.setShadowLayer(4f, 0f, 2f, Color.BLACK) // Sombra fuerte para legibilidad sin fondo oscuro
            FontHelper.applyFontToPaint(maxGradeBlockPaint, fontFamilyKey, Typeface.BOLD)
            val cyPercent = (headerHeight / 2f) + (percentTextPaint.textSize / 3f)
            // getSubBlockSize only returns 50.0 or 100.0 for typical route lookaheads
            val showMaxGrade = strat.subBlockSizeMeters <= 100.0 && strat.subBlocksMax.isNotEmpty()
            for (band in bands) {
                val left = band.startIndex * blockWidth
                val right = (band.endIndex + 1) * blockWidth
                val bandWidth = right - left
                
                // Si la banda está en el área previa al inicio de la ruta, no dibujamos su %
                if (right <= startX) continue
                // Si la banda está en el área de meta, tampoco dibujamos
                if (left >= endX) continue
                
                if (bandWidth > 50f) {
                    val count = band.endIndex - band.startIndex + 1
                    val avgGrade = (Math.round(band.sumGrade / count)).toInt()
                    // Adjust drawing to not overlap with checkered start/end if it crosses them
                    val actualLeft = Math.max(left, startX)
                    val actualRight = Math.min(right, endX)
                    val drawCenter = actualLeft + (actualRight - actualLeft) / 2f
                    canvas.drawText("${avgGrade}%", drawCenter, cyPercent, percentTextPaint)
                    
                    // Max grade label (top-left corner) — computed as extreme across all subBlocks in the band
                    if (showMaxGrade) {
                        val blockGrades = (band.startIndex..band.endIndex)
                            .mapNotNull { strat.subBlocksMax.getOrNull(it) }
                        val maxG = blockGrades.maxOrNull()
                        val minG = blockGrades.minOrNull()
                        // Pick the most extreme value: if avg is negative, look at the most negative peak
                        val extremeG = if (avgGrade < 0 && minG != null && minG < avgGrade - 1f) minG
                                       else if (maxG != null && maxG > avgGrade + 1f) maxG
                                       else null
                        if (extremeG != null) {
                            val arrow = if (extremeG < 0) "▼" else "▲"
                            val maxLabel = "$arrow${Math.abs(Math.round(extremeG))}%"
                            val labelX = actualLeft + 4f
                            val labelY = maxGradeBlockPaint.textSize + 4f
                            canvas.drawText(maxLabel, labelX, labelY, maxGradeBlockPaint)
                        }
                    }
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
                val kmLabel = if (tickDist >= 1000) "${(tickDist / 1000).toInt()}km" else "${tickDist.toInt()}m"
                val cyTick = headerHeight + 20f + tickTextPaint.textSize
                canvas.drawText(kmLabel, px, cyTick, tickTextPaint)
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

            // 5b. Ghost marker (ELITE) — semi-transparent arrow offset by ghostRelativeMeters
            val ghostRel = ghostRelativeMeters
            if (ghostRel != null) {
                val ghostX = (riderX + (ghostRel * w / lookahead).toFloat()).coerceIn(cWidth / 2f, w - cWidth / 2f)
                val ghostPath = Path()
                ghostPath.moveTo(ghostX, headerHeight)
                ghostPath.lineTo(ghostX - (cWidth / 2f), 0f)
                ghostPath.lineTo(ghostX, 10f)
                ghostPath.lineTo(ghostX + (cWidth / 2f), 0f)
                ghostPath.close()
                canvas.drawPath(ghostPath, ghostPaint)
                canvas.drawPath(ghostPath, ghostOutlinePaint)
            }

            // 5c. POI icons on ruler (ELITE)
            if (showPoiRuler && strat.pois.isNotEmpty()) {
                for (poi in strat.pois) {
                    val poiProgress = poi.relativeDistance / lookahead
                    val poiX = (w * poiProgress).toFloat().coerceIn(0f, w)
                    if (poiX < startX || poiX > endX) continue
                    val icon = poi.icon.ifEmpty {
                        when (poi.type) {
                            PoiType.WATER -> "💧"
                            PoiType.TOWN -> "🏘"
                            PoiType.SUMMIT -> "🔺"
                            PoiType.VIEWPOINT -> "📷"
                        }
                    }
                    // Draw a small vertical tick at ruler level and the icon above
                    canvas.drawLine(poiX, headerHeight, poiX, headerHeight + 22f, dividerPaint)
                    canvas.drawText(icon, poiX, headerHeight + 42f, poiTextPaint)
                }
            }
            
            // 6. Alertas dinámicas
            drawAlerts(canvas, strat, isHorizontal = true, w, h)

            // 7. Energy Bar (ELITE) — thin horizontal bar below the alert strip
            if (showEnergyBar) {
                val barTop = h - 12f
                val barHeight = 12f
                val energyLevel = strat.energyBatteryLevel.coerceIn(0.0, 100.0).toFloat() / 100f
                canvas.drawRect(0f, barTop, w, barTop + barHeight, energyBgPaint)
                val barColor = when {
                    energyLevel > 0.6f -> Color.parseColor("#4CAF50")
                    energyLevel > 0.3f -> Color.parseColor("#FFC107")
                    else -> Color.parseColor("#F44336")
                }
                val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = barColor; style = Paint.Style.FILL }
                canvas.drawRect(0f, barTop, w * energyLevel, barTop + barHeight, barPaint)
            }

            // 8. Grade Histogram (ELITE) — mini panel on the right side of the ruler strip
            if (showHistogram && strat.subBlocks.isNotEmpty()) {
                drawGradeHistogram(canvas, strat, headerHeight, radarH, w)
            }
            
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

            // Draw checkered start pattern if before actual route starts
            val totalLookahead = strat.subBlockSizeMeters * strat.subBlocks.size
            val startY = h - (h * (0.0 - strat.windowStartMeters).toFloat() / totalLookahead.toFloat())
            if (startY < h) {
                canvas.drawRect(0f, startY, headerWidth, h, checkeredPaint)
            }
            
            // Draw checkered end pattern if route finishes within window
            var endY = 0f
            if (strat.routeTotalLength > 0.0) {
                endY = h - (h * (strat.routeTotalLength - strat.windowStartMeters).toFloat() / totalLookahead.toFloat())
                if (endY > 0f) {
                    canvas.drawRect(0f, 0f, headerWidth, endY, checkeredPaint)
                }
            }

            // 2. Oscurecer lo que queda atrás (abajo)
            val cHeight = 30f
            var riderY = (h - (h * strat.riderProgress)).coerceAtLeast(cHeight / 2f).coerceAtMost(h - (cHeight / 2f))
            
            if (startY < h) {
                val distMeters = (startY - riderY) * totalLookahead / h
                if (distMeters in 0f..25f) {
                    riderY = startY
                }
            }

            if (riderY < h - (cHeight / 2f)) {
                // Overlay termina en startY para no oscurecer la bandera
                val limitY = Math.min(h.toFloat(), startY)
                canvas.drawRect(0f, riderY, headerWidth, limitY, pastOverlayPaint)
            }

            // 3. Textos de %
            percentTextPaint.setShadowLayer(4f, 0f, 2f, Color.BLACK)
            val cxPercent = headerWidth / 2f
            for (band in bands) {
                val top = h - ((band.endIndex + 1) * blockHeight)
                val bottom = h - (band.startIndex * blockHeight)
                val bandHeight = bottom - top
                
                // Si la banda está debajo de startY (pre-ruta), no dibujar
                if (top >= startY) continue
                // Si la banda está arriba de endY (meta), tampoco dibujar
                if (bottom <= endY) continue
                
                if (bandHeight > 40f) {
                    val count = band.endIndex - band.startIndex + 1
                    val avgGrade = (Math.round(band.sumGrade / count)).toInt()
                    // Adjust drawing to not overlap with checkered start/end
                    val actualBottom = Math.min(bottom, startY)
                    val actualTop = Math.max(top, endY)
                    val cyPercent = actualTop + ((actualBottom - actualTop) / 2f) + (percentTextPaint.textSize / 3f)
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
                val kmLabel = if (tickDist >= 1000) "${(tickDist / 1000).toInt()}km" else "${tickDist.toInt()}m"
                val cyTick = py + (tickTextPaint.textSize / 3f)
                canvas.drawText(kmLabel, headerWidth + 20f, cyTick, tickTextPaint)
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
                val gradeStr = String.format("%.1f", nextClimb.avgGrade)
                alertText = "Puerto a ${formatDist(distToStart)} - ${formatDist(nextClimb.length)} al $gradeStr%"
                alertColorHex = GradeColorScale.getColorHex(nextClimb.avgGrade)
            }
        }

        if (alertText.isEmpty()) {
            // Si no hay alerta, el color de la franja debe coincidir con la pendiente actual del ciclista
            val currentBlockIdx = (strat.riderProgress * strat.subBlocks.size).toInt().coerceIn(0, strat.subBlocks.size - 1)
            val currentGrade = if (strat.subBlocks.isNotEmpty()) strat.subBlocks[currentBlockIdx].toDouble() else 0.0
            alertColorHex = GradeColorScale.getColorHex(currentGrade)
        }

        // Fondo de la franja: mezclamos el color de alerta con un poco de transparencia/blanco para que sea "más claro"
        val baseColor = Color.parseColor(alertColorHex)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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
            textSize = 38f // Reduced text size as requested
            textAlign = Paint.Align.CENTER
            setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
            FontHelper.applyFontToPaint(this, AppPreferences.getInstance(context).fontFamilyKey, android.graphics.Typeface.BOLD)
        }

        if (isHorizontal) {
            // Franja inferior completa
            val radarH = h * 0.60f
            val rect = android.graphics.RectF(0f, radarH, w, h)
            canvas.drawRect(rect, bgPaint)
            
            if (alertText.isNotEmpty()) {
                // Texto centrado vertical y horizontalmente en la franja
                val cy = radarH + (h - radarH) / 2f + (alertPaint.textSize / 3f)
                canvas.drawText(alertText, w / 2f, cy, alertPaint)
            }
        } else {
            // Franja derecha completa (vertical mode)
            val radarW = w * 0.60f
            val rect = android.graphics.RectF(radarW, 0f, w, h)
            canvas.drawRect(rect, bgPaint)
            
            if (alertText.isNotEmpty()) {
                // Texto centrado rotado o vertical
                // Para no complicarlo con rotación, lo centramos normal en la zona superior
                alertPaint.textAlign = Paint.Align.CENTER
                alertPaint.textSize = 35f
                canvas.drawText(alertText, radarW + (w - radarW) / 2f, 40f, alertPaint)
            }
        }
    }

    /**
     * Draws a compact grade histogram on the right side of the ruler strip.
     * Buckets: 0-2%, 2-4%, 4-6%, 6-8%, 8-10%, >10%
     */
    private fun drawGradeHistogram(canvas: Canvas, strat: StrategyData, top: Float, bottom: Float, w: Float) {
        val buckets = intArrayOf(0, 0, 0, 0, 0, 0)
        val bucketColors = arrayOf("#43A047", "#7CB342", "#FDD835", "#FB8C00", "#E53935", "#B71C1C")
        val labels = arrayOf("<2", "2-4", "4-6", "6-8", "8-10", ">10")

        for (g in strat.subBlocks) {
            val gi = g.toInt()
            when {
                gi < 2  -> buckets[0]++
                gi < 4  -> buckets[1]++
                gi < 6  -> buckets[2]++
                gi < 8  -> buckets[3]++
                gi < 10 -> buckets[4]++
                else    -> buckets[5]++
            }
        }

        val total = strat.subBlocks.size.coerceAtLeast(1)
        val panelW = w * 0.30f // right 30% of width
        val panelLeft = w - panelW
        val panelHeight = bottom - top
        val bucketW = panelW / buckets.size

        // Background
        canvas.drawRect(panelLeft, top, w, bottom, energyBgPaint)

        for (i in buckets.indices) {
            val fraction = buckets[i].toFloat() / total
            val barH = fraction * panelHeight * 0.85f
            val barLeft = panelLeft + i * bucketW + 2f
            val barRight = barLeft + bucketW - 4f
            val barBottom = bottom - 16f
            val barTop = barBottom - barH

            val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor(bucketColors[i])
                style = Paint.Style.FILL
            }
            canvas.drawRect(barLeft, barTop.coerceAtMost(barBottom), barRight, barBottom, barPaint)

            // Label under bar
            histPaint.textSize = 16f
            canvas.drawText(labels[i], barLeft + bucketW / 2f - 2f, bottom - 2f, histPaint)
        }

        // Title
        histPaint.textSize = 17f
        histPaint.textAlign = Paint.Align.CENTER
        canvas.drawText("% distr.", panelLeft + panelW / 2f, top + 16f, histPaint)
    }
}
