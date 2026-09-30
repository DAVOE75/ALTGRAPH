import re
filepath = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/RouteBarDataField.kt'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

target = """                    routeBarView.showPowerBar = prefs.elitePowerBarEnabled"""
replacement = """                    routeBarView.showPowerBar = prefs.elitePowerBarEnabled
                    routeBarView.showRadarAlerts = prefs.eliteRadarAlertsEnabled"""

content = content.replace(target, replacement)
with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
