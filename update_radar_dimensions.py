import re
filepath = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/RouteBarView.kt'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace radarH calculation in drawHorizontal
horizontal_h_target = """            val radarH = h * 0.60f
            val headerHeight = radarH * 0.50f"""
horizontal_h_replacement = """            val radarH = if (showRadarAlerts) h * 0.60f else h.toFloat()
            val headerHeight = radarH * 0.50f"""
content = content.replace(horizontal_h_target, horizontal_h_replacement)

# Replace radarW calculation in drawVertical
vertical_w_target = """            val radarW = w * 0.60f
            val headerWidth = radarW * 0.50f"""
vertical_w_replacement = """            val radarW = if (showRadarAlerts) w * 0.60f else w.toFloat()
            val headerWidth = radarW * 0.50f"""
content = content.replace(vertical_w_target, vertical_w_replacement)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
