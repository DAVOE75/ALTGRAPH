import re
filepath = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/MainActivity.kt'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

target = """        // Power Zone Bar"""
replacement = """        // Radar Alerts (Avisos Dinámicos)
        val radarAlertsCard = createCardContainer()
        val switchRadarAlerts = Switch(this).apply {
            text = "⚠️ Avisos (Muros, Próx. Puerto...)"
            textSize = 14f
            setTextColor(Color.WHITE)
            isChecked = prefs.eliteRadarAlertsEnabled
            isEnabled = prefs.isEliteUnlocked
            setOnCheckedChangeListener { _, v -> prefs.eliteRadarAlertsEnabled = v }
        }
        radarAlertsCard.addView(switchRadarAlerts)
        tabEliteContainer.addView(radarAlertsCard)

        // Power Zone Bar"""

content = content.replace(target, replacement)

# Update state in checkLicense
check_license_target = """            switchPowerBar.isEnabled = unlocked"""
check_license_replacement = """            switchPowerBar.isEnabled = unlocked
            switchRadarAlerts.isEnabled = unlocked"""
content = content.replace(check_license_target, check_license_replacement)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
