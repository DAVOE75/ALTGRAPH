import re

with open('app/src/main/java/com/example/altgraph/RouteBarView.kt', 'r', encoding='utf-8') as f:
    code = f.read()

# We want to replace the whole `if (isHorizontal) { ... } else { ... }` block
# Let's find it.
start_str = "        if (isHorizontal) {"
end_str = "    private fun formatDist(m: Double): String {"
start_idx = code.find(start_str)
end_idx = code.find(end_str)

if start_idx == -1 or end_idx == -1:
    print("Could not find block!")
    exit(1)

new_block = """        val ruleBackgroundPaint = Paint().apply { color = Color.parseColor("#151515"); style = Paint.Style.FILL }

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
                
                if (bandWidth > 50f) {
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
                lookahead <= 500 -> 100.0
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
                
                if (bandHeight > 40f) {
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
                lookahead <= 500 -> 100.0
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

"""
new_code = code[:start_idx] + new_block + code[end_idx:]

with open('app/src/main/java/com/example/altgraph/RouteBarView.kt', 'w', encoding='utf-8') as f:
    f.write(new_code)
