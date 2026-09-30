import re
filepath = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/RouteBarView.kt'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Add property
prop_replacement = """    var showPowerBar: Boolean = true
    var showRadarAlerts: Boolean = true"""
content = content.replace("    var showPowerBar: Boolean = true", prop_replacement)

# Conditionally draw alerts
# In RouteBarView.kt: drawAlerts(canvas, strat, isHorizontal = true, w, h)
drawalerts_target = """            // 6. Alertas dinámicas
            drawAlerts(canvas, strat, isHorizontal = true, w, h)
        } else {"""
drawalerts_replacement = """            // 6. Alertas dinámicas
            if (showRadarAlerts) {
                drawAlerts(canvas, strat, isHorizontal = true, w, h)
            }
        } else {"""
content = content.replace(drawalerts_target, drawalerts_replacement)

drawalerts_target2 = """            // 6. Alertas dinámicas
            drawAlerts(canvas, strat, isHorizontal = false, w, h)
        }
    }"""
drawalerts_replacement2 = """            // 6. Alertas dinámicas
            if (showRadarAlerts) {
                drawAlerts(canvas, strat, isHorizontal = false, w, h)
            }
        }
    }"""
content = content.replace(drawalerts_target2, drawalerts_replacement2)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
