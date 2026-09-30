import re
filepath = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/AppPreferences.kt'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

replacement1 = """    var elitePowerBarEnabled: Boolean
        get() = prefs.getBoolean(KEY_ELITE_POWER_BAR_ENABLED, true) && isEliteUnlocked
        set(value) = prefs.edit().putBoolean(KEY_ELITE_POWER_BAR_ENABLED, value).apply()

    var eliteRadarAlertsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ELITE_RADAR_ALERTS_ENABLED, true) && isEliteUnlocked
        set(value) = prefs.edit().putBoolean(KEY_ELITE_RADAR_ALERTS_ENABLED, value).apply()
"""
content = re.sub(r'    var elitePowerBarEnabled: Boolean.*?\.apply\(\)\n', replacement1, content, flags=re.DOTALL)

replacement2 = """        private const val KEY_ELITE_POWER_BAR_ENABLED = "elite_power_bar_enabled"
        private const val KEY_ELITE_RADAR_ALERTS_ENABLED = "elite_radar_alerts_enabled\""""
content = content.replace('        private const val KEY_ELITE_POWER_BAR_ENABLED = "elite_power_bar_enabled"', replacement2)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
