import re
filepath = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/RouteBarView.kt'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace("    var showPowerBar: Boolean = false", "    var showPowerBar: Boolean = false\n    var showRadarAlerts: Boolean = true")

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
