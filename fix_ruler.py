import re

filepath = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/RouteBarView.kt'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix 1: Horizontal mode
horizontal_target = """
                    // Línea de marca hacia abajo
                    canvas.drawLine(px, headerHeight, px, headerHeight + 15f, dividerPaint)
                    // Texto (un-skew if 3D)
                    val kmLabel = if (tickDist >= 1000) {
                        val km = tickDist / 1000.0
                        val kmStr = String.format(java.util.Locale.US, "%.2f", km).trimEnd('0').trimEnd('.')
                        "${kmStr}km"
                    } else {
                        "${tickDist.toInt()}m"
                    }
                    val cyTick = headerHeight + 20f + tickTextPaint.textSize
"""

horizontal_replacement = """
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
"""

content = content.replace(horizontal_target.strip(), horizontal_replacement.strip())

# Fix 2: Vertical mode
vertical_target = """
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
"""

vertical_replacement = """
                    // Dynamic scaling for vertical ruler
                    val rulerSpace = radarW - headerWidth
                    val dynamicTickLen = Math.min(15f, rulerSpace * 0.25f)
                    // Text needs to fit horizontally in the remaining space
                    tickTextPaint.textSize = 28f // reset to default
                    var currentTextSize = 28f
                    val kmLabel = if (tickDist >= 1000) {
                        val km = tickDist / 1000.0
                        val kmStr = String.format(java.util.Locale.US, "%.2f", km).trimEnd('0').trimEnd('.')
                        "${kmStr}km"
                    } else {
                        "${tickDist.toInt()}m"
                    }
                    // Scale down if it's too wide
                    val maxTextWidth = rulerSpace - dynamicTickLen - 4f
                    if (tickTextPaint.measureText(kmLabel) > maxTextWidth) {
                        currentTextSize = currentTextSize * (maxTextWidth / tickTextPaint.measureText(kmLabel))
                        tickTextPaint.textSize = currentTextSize.coerceAtLeast(10f)
                    }

                    // Línea de marca hacia la derecha
                    canvas.drawLine(headerWidth, py, headerWidth + dynamicTickLen, py, dividerPaint)
                    // Texto
                    val cyTick = py + (tickTextPaint.textSize / 3f)
"""

content = content.replace(vertical_target.strip(), vertical_replacement.strip())

# Vertical alignment x coordinate fix
vert_x_target = "canvas.drawText(kmLabel, headerWidth + 20f, cyTick, tickTextPaint)"
vert_x_replacement = "canvas.drawText(kmLabel, headerWidth + dynamicTickLen + 2f, cyTick, tickTextPaint)"
content = content.replace(vert_x_target, vert_x_replacement)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
