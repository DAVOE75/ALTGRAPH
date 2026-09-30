import re

filepath = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/RouteBarView.kt'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix 2: Vertical mode
vertical_target = """                if (py in -50f..(h + 50f)) {
                    // Línea de marca hacia la derecha
                    canvas.drawLine(headerWidth, py, headerWidth + 15f, py, dividerPaint)
                    // Texto
                    val kmLabel = if (tickDist >= 1000) {
                        val km = tickDist / 1000.0
                        val kmStr = String.format(java.util.Locale.US, "%.2f", km).trimEnd('0').trimEnd('.')
                        "${kmStr}km"
                    } else {
                        "${tickDist.toInt()}m"
                    }
                    val cyTick = py + (tickTextPaint.textSize / 3f)
                    if (showRadar3d) {
                        canvas.save()
                        canvas.translate(headerWidth + 20f, cyTick)
                        canvas.skew(0f, -0.35f) // vertical unskew
                        canvas.drawText(kmLabel, 0f, 0f, tickTextPaint)
                        canvas.restore()
                    } else {
                        canvas.drawText(kmLabel, headerWidth + 20f, cyTick, tickTextPaint)
                    }
                }"""

vertical_replacement = """                if (py in -50f..(h + 50f)) {
                    // Dynamic scaling for vertical ruler
                    val rulerSpace = radarW - headerWidth
                    val dynamicTickLen = Math.min(15f, rulerSpace * 0.25f)
                    
                    // Texto
                    val kmLabel = if (tickDist >= 1000) {
                        val km = tickDist / 1000.0
                        val kmStr = String.format(java.util.Locale.US, "%.2f", km).trimEnd('0').trimEnd('.')
                        "${kmStr}km"
                    } else {
                        "${tickDist.toInt()}m"
                    }
                    
                    // Scale down if it's too wide
                    tickTextPaint.textSize = 28f
                    var currentTextSize = 28f
                    val maxTextWidth = rulerSpace - dynamicTickLen - 4f
                    if (tickTextPaint.measureText(kmLabel) > maxTextWidth) {
                        currentTextSize = currentTextSize * (maxTextWidth / tickTextPaint.measureText(kmLabel))
                        tickTextPaint.textSize = currentTextSize.coerceAtLeast(10f)
                    }

                    // Línea de marca hacia la derecha
                    canvas.drawLine(headerWidth, py, headerWidth + dynamicTickLen, py, dividerPaint)
                    
                    val cyTick = py + (tickTextPaint.textSize / 3f)
                    if (showRadar3d) {
                        canvas.save()
                        canvas.translate(headerWidth + dynamicTickLen + 2f, cyTick)
                        canvas.skew(0f, -0.35f) // vertical unskew
                        canvas.drawText(kmLabel, 0f, 0f, tickTextPaint)
                        canvas.restore()
                    } else {
                        canvas.drawText(kmLabel, headerWidth + dynamicTickLen + 2f, cyTick, tickTextPaint)
                    }
                }"""

content = content.replace(vertical_target, vertical_replacement)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
