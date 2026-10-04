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
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 0f, 2f, Color.BLACK)
    }

    private val maxGradeArrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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

    // ── ELITE feature state (set by RouteBarDataField each frame) ────────────
    var ghostRelativeMeters: Double? = null   // +ahead / -behind in metres
    var showEnergyBar: Boolean = false
    var showPowerBar: Boolean = false
    var showRadarAlerts: Boolean = true
    var userFtp: Int = 250
    var showPoiRuler: Boolean = false
    var showHistogram: Boolean = false
    var showRadar3d: Boolean = true
    var radarTheme: String = "Estándar"

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

    private val histPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 20f
        textAlign = Paint.Align.CENTER
        setShadowLayer(2f, 0f, 1f, Color.BLACK)
    }

    private fun drawThemedBand(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        colorHex: String,
        theme: String,
        isHorizontal: Boolean
    ) {
        val baseColor = Color.parseColor(colorHex)
        val w = right - left
        val h = bottom - top

        when (theme.lowercase(java.util.Locale.getDefault())) {
            "oasis" -> {
                // Smooth gradient + shiny top
                val hsv = FloatArray(3)
                Color.colorToHSV(baseColor, hsv)
                hsv[2] = Math.min(1.0f, hsv[2] * 1.3f)
                val lightColor = Color.HSVToColor(hsv)
                hsv[2] = Math.max(0.0f, hsv[2] * 0.5f) // darken from original (1.3 * 0.5 = 0.65)
                val darkColor = Color.HSVToColor(hsv)
                
                val grad = android.graphics.LinearGradient(
                    0f, top, 0f, bottom,
                    intArrayOf(lightColor, baseColor, darkColor),
                    floatArrayOf(0f, 0.4f, 1f),
                    android.graphics.Shader.TileMode.CLAMP
                )
                segmentPaint.shader = grad
                canvas.drawRect(left, top, right + 1f, bottom, segmentPaint)
                segmentPaint.shader = null
                
                // Shiny highlight
                val shinePaint = Paint().apply {
                    color = Color.argb(40, 255, 255, 255)
                    style = Paint.Style.FILL
                }
                if (isHorizontal) {
                    canvas.drawRect(left, top, right + 1f, top + h * 0.2f, shinePaint)
                }
            }
            "crisoles" -> {
                // Inner dark glow, bright center
                val hsv = FloatArray(3)
                Color.colorToHSV(baseColor, hsv)
                hsv[2] = Math.max(0.0f, hsv[2] * 0.5f)
                val darkColor = Color.HSVToColor(hsv)
                hsv[2] = Math.min(1.0f, hsv[2] * 2.4f)
                val lightColor = Color.HSVToColor(hsv)
                
                val grad = android.graphics.LinearGradient(
                    0f, top, 0f, bottom,
                    intArrayOf(darkColor, lightColor, darkColor),
                    null,
                    android.graphics.Shader.TileMode.CLAMP
                )
                segmentPaint.shader = grad
                canvas.drawRect(left, top, right + 1f, bottom, segmentPaint)
                segmentPaint.shader = null
                
                // Add inner shadow
                val innerShadow = Paint().apply {
                    color = Color.argb(100, 0, 0, 0)
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                }
                canvas.drawRect(left, top, right, bottom, innerShadow)
            }
            "bruma" -> {
                // Glassmorphism: Desaturated color with high transparency and white borders
                val glassColor = Color.argb(
                    180,
                    Color.red(baseColor),
                    Color.green(baseColor),
                    Color.blue(baseColor)
                )
                segmentPaint.color = glassColor
                canvas.drawRect(left, top, right + 1f, bottom, segmentPaint)
                
                val glassGlow = android.graphics.LinearGradient(
                    left, top, right, bottom,
                    intArrayOf(Color.argb(80, 255, 255, 255), Color.TRANSPARENT),
                    null,
                    android.graphics.Shader.TileMode.CLAMP
                )
                val glowPaint = Paint().apply {
                    shader = glassGlow
                }
                canvas.drawRect(left, top, right + 1f, bottom, glowPaint)
                
                // Glass border
                val borderPaint = Paint().apply {
                    color = Color.argb(60, 255, 255, 255)
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                }
                canvas.drawRect(left, top, right, bottom, borderPaint)
            }
            "campo de fuerza" -> {
                // Neon Grid
                val hsv = FloatArray(3)
                Color.colorToHSV(baseColor, hsv)
                hsv[2] = Math.max(0.0f, hsv[2] * 0.8f)
                val darkColor = Color.HSVToColor(hsv)
                hsv[2] = Math.min(1.0f, hsv[2] * 1.8f)
                val neonColor = Color.HSVToColor(hsv)
                
                segmentPaint.color = darkColor
                canvas.drawRect(left, top, right + 1f, bottom, segmentPaint)
                
                val neonBorder = Paint().apply {
                    color = neonColor
                    style = Paint.Style.STROKE
                    strokeWidth = 3f
                    setShadowLayer(8f, 0f, 0f, color)
                }
                canvas.drawRect(left, top, right, bottom, neonBorder)
                
                // Draw some grid lines
                val gridPaint = Paint().apply {
                    color = Color.argb(50, 255, 255, 255)
                    style = Paint.Style.STROKE
                    strokeWidth = 1f
                }
                if (isHorizontal && w > 10f) {
                    canvas.drawLine(left + w / 2, top, left + w / 2, bottom, gridPaint)
                }
            }
            else -> { // Estándar
                segmentPaint.color = baseColor
                canvas.drawRect(left, top, right + 1f, bottom, segmentPaint)
            }
        }
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
        val firstGrade = blocks[0].toDouble()
        var currentColor = if (firstGrade <= -900.0) "#9E9E9E" else GradeColorScale.getColorHex(firstGrade)
        var currentBand = ColorBand(0, 0, currentColor, blocks[0].toDouble())
        bands.add(currentBand)
        
        for (i in 1 until numBlocks) {
            val grade = blocks[i].toDouble()
            val color = if (grade <= -900.0) "#9E9E9E" else GradeColorScale.getColorHex(grade)
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
            val radarH = if (showRadarAlerts) h * 0.60f else h.toFloat()
            val headerHeight = radarH * 0.50f
            
            // 1. Dibujar franjas de color con ligera perspectiva isométrica
            if (showRadar3d) {
                canvas.save()
                val skewFactor = -0.35f // controla la inclinación para efecto 3D
                canvas.skew(skewFactor, 0f)
            }
            val blockWidth = w / numBlocks.toFloat()
            val cWidth = 30f
            val arrowX = (w * 0.25f).coerceAtLeast(cWidth / 2f)
            val progressOffset = (w * strat.riderProgress) - arrowX

            for (band in bands) {
                val left = band.startIndex * blockWidth - progressOffset
                val right = (band.endIndex + 1) * blockWidth - progressOffset
                drawThemedBand(canvas, left, 0f, right, headerHeight, band.colorHex, radarTheme, true)
            }
            if (showRadar3d) {
                canvas.restore()
            }
            
            // Draw checkered start pattern if we are before the actual route starts
            val totalLookahead = strat.subBlockSizeMeters * strat.subBlocks.size
            val startX = w * (0.0 - strat.windowStartMeters).toFloat() / totalLookahead.toFloat() - progressOffset
            if (startX > 0f) {
                canvas.drawRect(0f, 0f, startX, headerHeight, checkeredPaint)
            }
            
            // Draw checkered end pattern if the route finishes within the window
            var endX = w.toFloat() + Math.abs(progressOffset)
            if (strat.routeTotalLength > 0.0) {
                endX = w * (strat.routeTotalLength - strat.windowStartMeters).toFloat() / totalLookahead.toFloat() - progressOffset
                if (endX < w) {
                    canvas.drawRect(endX, 0f, w.toFloat(), headerHeight, checkeredPaint)
                }
            }

            // 2. Dibujar overlay oscuro (pasado) a la izquierda de la flecha estática
            if (arrowX > startX) {
                // Oscurecer lo que queda atrás (solo en la franja superior donde hay colores)
                // Hacemos que el overlay empiece desde startX para no oscurecer la bandera de cuadros
                canvas.drawRect(Math.max(0f, startX), 0f, arrowX, headerHeight, pastOverlayPaint)
            }

            // 3. Textos de porcentaje medio en la franja superior (sin el fondo oscurecido general)
            percentTextPaint.setShadowLayer(4f, 0f, 2f, Color.BLACK) // Sombra fuerte para legibilidad sin fondo oscuro
            FontHelper.applyFontToPaint(maxGradeBlockPaint, fontFamilyKey, Typeface.BOLD)
            
            // DYNAMIC SCALING FOR HEIGHT COMPRESSION
            val currentMaxGradeTextSize = Math.min(24f, headerHeight * 0.45f).coerceAtLeast(10f)
            maxGradeBlockPaint.textSize = currentMaxGradeTextSize
            maxGradeArrowPaint.textSize = currentMaxGradeTextSize
            percentTextPaint.textSize = Math.min(32f, headerHeight * 0.8f).coerceAtLeast(12f)
            
            val cyPercent = (headerHeight / 2f) + (percentTextPaint.textSize / 3f)
            // getSubBlockSize only returns 50.0 or 100.0 for typical route lookaheads
            val showMaxGrade = strat.subBlockSizeMeters <= 100.0 && strat.subBlocksMax.isNotEmpty()
            for (band in bands) {
                val left = band.startIndex * blockWidth - progressOffset
                val right = (band.endIndex + 1) * blockWidth - progressOffset
                val bandWidth = right - left
                
                // Si la banda está fuera de la pantalla no dibujar su %
                if (right <= 0f || left >= w) continue
                
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
                            val maxLabel = "${Math.abs(Math.round(extremeG))}%"
                            
                            val labelX = actualLeft + 24f // Center relative to text
                            val arrowY = maxGradeArrowPaint.textSize + 4f
                            val textY = arrowY + maxGradeBlockPaint.textSize + 2f
                            
                            val baseColor = Color.parseColor(band.colorHex)
                            val hsv = FloatArray(3)
                            Color.colorToHSV(baseColor, hsv)
                            hsv[1] = Math.min(1.0f, hsv[1] * 1.2f) // Increase saturation a bit
                            hsv[2] *= 0.5f // Decrease brightness for a darker tone
                            maxGradeArrowPaint.color = Color.HSVToColor(hsv)
                            
                            canvas.drawText(arrow, labelX, arrowY, maxGradeArrowPaint)
                            canvas.drawText(maxLabel, labelX, textY, maxGradeBlockPaint)
                        }
                    }
                }
            }

            // 4. Franja Intermedia (Regla / Ruler)
            if (showRadar3d) {
                canvas.save()
                canvas.skew(-0.35f, 0f)
                canvas.drawRect(-w, headerHeight, w * 2, radarH, ruleBackgroundPaint)
            } else {
                canvas.drawRect(0f, headerHeight, w, radarH, ruleBackgroundPaint)
            }
            // Línea divisoria blanca
            canvas.drawLine(-w, headerHeight, w * 2, headerHeight, dividerPaint)

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
            val startTick = Math.ceil((strat.windowStartMeters - lookahead) / tickInterval) * tickInterval
            var tickDist = startTick
            tickTextPaint.textAlign = Paint.Align.CENTER
            while (tickDist < strat.windowStartMeters + lookahead * 2) {
                val progress = (tickDist - strat.windowStartMeters) / lookahead
                val px = (w * progress).toFloat() - progressOffset
                if (px in -50f..(w + 50f)) {
                    // Dynamic scaling for horizontal ruler
                    val rulerSpace = radarH - headerHeight
                    val dynamicTickLen = Math.min(15f, rulerSpace * 0.25f)
                    val maxTextHeight = rulerSpace - dynamicTickLen - 2f
                    val currentTextSize = Math.min(28f, maxTextHeight * 0.85f).coerceAtLeast(10f)
                    tickTextPaint.textSize = currentTextSize
                    
                    // Línea de marca hacia abajo
                    canvas.drawLine(px, headerHeight, px, headerHeight + dynamicTickLen, dividerPaint)
                    // Texto (un-skew if 3D)
                    val kmLabel = if (tickDist >= 1000) {
                        val km = tickDist / 1000.0
                        val kmStr = String.format(java.util.Locale.US, "%.2f", km).trimEnd('0').trimEnd('.')
                        "${kmStr}km"
                    } else {
                        "${tickDist.toInt()}m"
                    }
                    // Baseline alignment avoiding overlap with alert bar
                    val cyTick = headerHeight + dynamicTickLen + 2f + tickTextPaint.textSize * 0.8f
                    if (showRadar3d) {
                        canvas.save()
                        canvas.translate(px, cyTick)
                        canvas.skew(0.35f, 0f) // unskew
                        canvas.drawText(kmLabel, 0f, 0f, tickTextPaint)
                        canvas.restore()
                    } else {
                        canvas.drawText(kmLabel, px, cyTick, tickTextPaint)
                    }
                }
                tickDist += tickInterval
            }
            if (showRadar3d) {
                canvas.restore()
            }

            // 5. Indicador de posición (Ciclista)
            val path = Path()
            val cHeight = headerHeight * 0.8f
            path.moveTo(arrowX, headerHeight) // Punta abajo (tocando la divisoria)
            path.lineTo(arrowX - (cWidth / 2f), 0f) // Esquina superior izq
            path.lineTo(arrowX, 10f) // Centro arriba
            path.lineTo(arrowX + (cWidth / 2f), 0f) // Esquina superior der
            path.close()
            canvas.drawPath(path, cyclistPaint)
            canvas.drawPath(path, cyclistOutline)

            // 5b. Ghost marker (ELITE) — semi-transparent arrow offset by ghostRelativeMeters
            val ghostRel = ghostRelativeMeters
            if (ghostRel != null) {
                val ghostProgress = strat.riderProgress + (ghostRel / lookahead).toFloat()
                val ghostX = ((w * ghostProgress).toFloat() - progressOffset).coerceIn(cWidth / 2f, w - cWidth / 2f)
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
                if (showRadar3d) {
                    canvas.save()
                    canvas.skew(-0.35f, 0f)
                }
                for (poi in strat.pois) {
                    val poiProgress = poi.relativeDistance / lookahead
                    val poiX = (w * poiProgress).toFloat() - progressOffset
                    if (poiX < startX || poiX > endX) continue
                    if (poiX in -50f..(w + 50f)) {
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
                        
                        // Draw unskewed icon
                        if (showRadar3d) {
                            canvas.save()
                            canvas.translate(poiX, headerHeight + 42f)
                            canvas.skew(0.35f, 0f)
                            canvas.drawText(icon, 0f, 0f, poiTextPaint)
                            canvas.restore()
                        } else {
                            canvas.drawText(icon, poiX, headerHeight + 42f, poiTextPaint)
                        }
                    }
                }
                if (showRadar3d) {
                    canvas.restore()
                }
            }
            
            // 6. Alertas dinámicas
            if (showRadarAlerts) {
                drawAlerts(canvas, strat, isHorizontal = true, w, h)
            }

            // 7. Energy / Power Bars (ELITE) — thin horizontal bars below the alert strip
            var bottomOffset = 0f
            if (showEnergyBar) {
                val barTop = h - bottomOffset - 12f
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
                bottomOffset += 12f
            }

            if (showPowerBar) {
                val barTop = h - bottomOffset - 12f
                val barHeight = 12f
                canvas.drawRect(0f, barTop, w, barTop + barHeight, energyBgPaint)
                
                val currentPwr = strat.currentPower.toFloat()
                val ftp = userFtp.toFloat().coerceAtLeast(1f)
                val percentFtp = currentPwr / ftp
                
                val barColor = when {
                    percentFtp < 0.55f -> Color.parseColor("#808080") // Z1: Active Recovery (Gray)
                    percentFtp < 0.75f -> Color.parseColor("#4CAF50") // Z2: Endurance (Green)
                    percentFtp < 0.90f -> Color.parseColor("#FFEB3B") // Z3: Tempo (Yellow)
                    percentFtp < 1.05f -> Color.parseColor("#FF9800") // Z4: Threshold (Orange)
                    percentFtp < 1.20f -> Color.parseColor("#F44336") // Z5: VO2 Max (Red)
                    percentFtp < 1.50f -> Color.parseColor("#9C27B0") // Z6: Anaerobic Capacity (Purple)
                    else -> Color.parseColor("#FFEB3B") // Z7: Neuromuscular (Yellow/White - let's use White or bright yellow) 
                }
                if (percentFtp >= 1.50f) {
                    val p2 = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
                    canvas.drawRect(0f, barTop, w, barTop + barHeight, p2)
                } else {
                    val p2 = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = barColor; style = Paint.Style.FILL }
                    // Scale it so that Z1-Z6 covers the width linearly, capping at 1.5x FTP
                    val widthScale = (percentFtp / 1.5f).coerceIn(0f, 1f)
                    canvas.drawRect(0f, barTop, w * widthScale, barTop + barHeight, p2)
                }
                bottomOffset += 12f
            }

            // 8. Grade Histogram (ELITE) — mini panel on the right side of the ruler strip
            if (showHistogram && strat.subBlocks.isNotEmpty()) {
                drawGradeHistogram(canvas, strat, headerHeight, radarH, w)
            }
            
        } else {
            // VERTICAL MODE
            val radarW = if (showRadarAlerts) w * 0.60f else w.toFloat()
            val headerWidth = radarW * 0.50f
            val blockHeight = h / numBlocks.toFloat()
            
            // 1. Dibujar franjas de color con ligera perspectiva isométrica (vertical mode)
            if (showRadar3d) {
                canvas.save()
                val skewFactorV = -0.35f // inclinación vertical para efecto 3D
                canvas.skew(0f, skewFactorV)
            }
            for (band in bands) {
                val top = h - ((band.endIndex + 1) * blockHeight)
                val bottom = h - (band.startIndex * blockHeight)
                drawThemedBand(canvas, 0f, top - 1f, headerWidth, bottom, band.colorHex, radarTheme, false)
            }
            if (showRadar3d) {
                canvas.restore()
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
            // DYNAMIC SCALING FOR WIDTH COMPRESSION IN VERTICAL MODE
            percentTextPaint.textSize = Math.min(32f, headerWidth * 0.8f).coerceAtLeast(12f)
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
            if (showRadar3d) {
                canvas.save()
                canvas.skew(0f, -0.35f)
                canvas.drawRect(headerWidth, -h, radarW, h * 2, ruleBackgroundPaint)
            } else {
                canvas.drawRect(headerWidth, 0f, radarW, h, ruleBackgroundPaint)
            }
            canvas.drawLine(headerWidth, -h, headerWidth, h * 2, dividerPaint)

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
                // Dynamic scaling for vertical ruler
                val rulerSpace = radarW - headerWidth
                val dynamicTickLen = Math.min(15f, rulerSpace * 0.25f)
                
                canvas.drawLine(headerWidth, py, headerWidth + dynamicTickLen, py, dividerPaint)
                val kmLabel = if (tickDist >= 1000) "${(tickDist / 1000).toInt()}km" else "${tickDist.toInt()}m"
                
                // Scale down if it's too wide
                tickTextPaint.textSize = 28f
                var currentTextSize = 28f
                val maxTextWidth = rulerSpace - dynamicTickLen - 4f
                if (tickTextPaint.measureText(kmLabel) > maxTextWidth) {
                    currentTextSize = currentTextSize * (maxTextWidth / tickTextPaint.measureText(kmLabel))
                    tickTextPaint.textSize = currentTextSize.coerceAtLeast(10f)
                }

                val cyTick = py + (tickTextPaint.textSize / 3f)
                if (showRadar3d) {
                    canvas.save()
                    canvas.translate(headerWidth + dynamicTickLen + 2f, cyTick)
                    canvas.skew(0f, 0.35f)
                    canvas.drawText(kmLabel, 0f, 0f, tickTextPaint)
                    canvas.restore()
                } else {
                    canvas.drawText(kmLabel, headerWidth + dynamicTickLen + 2f, cyTick, tickTextPaint)
                }
                tickDist += tickInterval
            }
            if (showRadar3d) {
                canvas.restore()
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
            if (showRadarAlerts) {
                drawAlerts(canvas, strat, isHorizontal = false, w, h)
            }
        }
    }

    private fun formatDist(m: Double): String {
        return when {
            m < 1000 -> "${Math.round(m)}m"
            m < 10000 -> String.format(java.util.Locale.getDefault(), "%.2fkm", m / 1000.0)
            else -> String.format(java.util.Locale.getDefault(), "%.1fkm", m / 1000.0)
        }
    }

    private fun drawAlerts(canvas: Canvas, strat: StrategyData, isHorizontal: Boolean, w: Float, h: Float) {
        // Usar la distancia exacta del ciclista en lugar de la relativa a la ventana
        val trueRiderDist = strat.routeTotalLength - strat.remainingDistance
        
        var alertText = ""
        var alertColorHex = "#212121" // Default dark gray
        
        val activeClimb = strat.activeClimbs.find { trueRiderDist >= it.startDistance && trueRiderDist < it.endDistance }
        if (activeClimb != null) {
            val currentBlockIdx = (strat.riderProgress * strat.subBlocks.size).toInt()
            var steepDist: Double? = null
            var steepGrade: Float? = null
            
            for (i in currentBlockIdx until strat.subBlocks.size) {
                val grade = strat.subBlocks[i]
                if (grade >= 12f) {
                    val distToBlock = strat.windowStartMeters + (i * strat.subBlockSizeMeters) - trueRiderDist
                    if (distToBlock > 0 && distToBlock < activeClimb.endDistance - trueRiderDist) {
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
                val distToTop = activeClimb.endDistance - trueRiderDist
                alertText = "Coronar a ${formatDist(distToTop)}"
                alertColorHex = "#4CAF50" // Green
            }
        } else {
            val nextClimb = strat.activeClimbs.filter { it.startDistance > trueRiderDist }.minByOrNull { it.startDistance }
            if (nextClimb != null) {
                val distToStart = nextClimb.startDistance - trueRiderDist
                val gradeStr = String.format(java.util.Locale.getDefault(), "%.1f", nextClimb.avgGrade)
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
                // Ajustar tamaño de texto según la altura disponible
                alertPaint.textSize = Math.min(38f, (h - radarH) * 0.8f).coerceAtLeast(12f)
                
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
                alertPaint.textSize = Math.min(35f, (w - radarW) * 0.8f).coerceAtLeast(10f)
                canvas.drawText(alertText, radarW + (w - radarW) / 2f, alertPaint.textSize + 10f, alertPaint)
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
        canvas.drawText(context.getString(R.string.label_dist_format, "%"), panelLeft + panelW / 2f, top + 16f, histPaint)
    }
}
