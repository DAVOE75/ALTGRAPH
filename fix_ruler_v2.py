import re

filepath = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/RouteBarView.kt'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix 1: Horizontal mode
horizontal_target = """                if (px in -50f..(w + 50f)) {
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
                    val cyTick = headerHeight + 20f + tickTextPaint.textSize"""

horizontal_replacement = """                if (px in -50f..(w + 50f)) {
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
                    val cyTick = headerHeight + dynamicTickLen + 2f + tickTextPaint.textSize * 0.8f"""

content = content.replace(horizontal_target, horizontal_replacement)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
