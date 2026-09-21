import re

prefs_file = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\AppPreferences.kt'

with open(prefs_file, 'r', encoding='utf-8') as f:
    content = f.read()

elite_code = '''    // --- ELITE FEATURES (PAYWALL) ---
    var licenseKey: String
        get() = prefs.getString(KEY_LICENSE_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LICENSE_KEY, value).apply()

    var isEliteUnlocked: Boolean
        get() = licenseKey == "ALTGRAPH-ELITE-2026"
        
    var weatherOverlayEnabled: Boolean
        get() = prefs.getBoolean(KEY_WEATHER_OVERLAY_ENABLED, true) && isEliteUnlocked
        set(value) = prefs.edit().putBoolean(KEY_WEATHER_OVERLAY_ENABLED, value).apply()

    var stravaSegmentsEnabled: Boolean
        get() = prefs.getBoolean(KEY_STRAVA_SEGMENTS_ENABLED, true) && isEliteUnlocked
        set(value) = prefs.edit().putBoolean(KEY_STRAVA_SEGMENTS_ENABLED, value).apply()
'''

keys_code = '''        private const val KEY_LICENSE_KEY = "license_key"
        private const val KEY_WEATHER_OVERLAY_ENABLED = "weather_overlay_enabled"
        private const val KEY_STRAVA_SEGMENTS_ENABLED = "strava_segments_enabled"
'''

if 'KEY_LICENSE_KEY' not in content:
    content = content.replace(
        '    // --- PRO FEATURES (NIVEL SDK MÁXIMO) ---',
        elite_code + '\n    // --- PRO FEATURES (NIVEL SDK MÁXIMO) ---'
    )
    
    content = content.replace(
        '        private const val KEY_SMART_ZOOM_ENABLED = "smart_zoom_enabled"',
        keys_code + '\n        private const val KEY_SMART_ZOOM_ENABLED = "smart_zoom_enabled"'
    )
    
    with open(prefs_file, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Elite prefs added")
else:
    print("Elite prefs already added")

