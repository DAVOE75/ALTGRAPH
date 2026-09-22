package com.example.altgraph

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var fontFamilyKey: String
        get() = prefs.getString(KEY_FONT_FAMILY, "sans-serif-condensed") ?: "sans-serif-condensed"
        set(value) = prefs.edit().putString(KEY_FONT_FAMILY, value).apply()

    var blockSizeMeters: Double
        get() = prefs.getFloat(KEY_BLOCK_SIZE, 100.0f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_BLOCK_SIZE, value.toFloat()).apply()

    var thresholdAttackPct: Double
        get() = prefs.getFloat(KEY_THRESHOLD_ATTACK, 10.0f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_THRESHOLD_ATTACK, value.toFloat()).apply()

    var targetVam: Int
        get() = prefs.getInt(KEY_TARGET_VAM, 900)
        set(value) = prefs.edit().putInt(KEY_TARGET_VAM, value).apply()

    var attackAlertEnabled: Boolean
        get() = prefs.getBoolean(KEY_ATTACK_ALERT_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ATTACK_ALERT_ENABLED, value).apply()

    var asphaltFactor: Double
        get() = prefs.getFloat(KEY_ASPHALT_FACTOR, 0.5f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_ASPHALT_FACTOR, value.toFloat()).apply()

    var fontSize3dScale: Float
        get() = prefs.getFloat(KEY_FONT_SIZE_3D_SCALE, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_FONT_SIZE_3D_SCALE, value).apply()

    var showBlockPercentages: Boolean
        get() = prefs.getBoolean(KEY_SHOW_BLOCK_PERCENTAGES, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_BLOCK_PERCENTAGES, value).apply()

    var lookaheadMeters3d: Int
        get() = prefs.getInt(KEY_LOOKAHEAD_METERS_3D, 350)
        set(value) = prefs.edit().putInt(KEY_LOOKAHEAD_METERS_3D, value).apply()

    var show3dCotas: Boolean
        get() = prefs.getBoolean(KEY_SHOW_3D_COTAS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_3D_COTAS, value).apply()

    var show3dRamps: Boolean
        get() = prefs.getBoolean(KEY_SHOW_3D_RAMPS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_3D_RAMPS, value).apply()

    var showMaxGradient: Boolean
        get() = prefs.getBoolean(KEY_SHOW_MAX_GRADIENT, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_MAX_GRADIENT, value).apply()

    var showRouteCompass: Boolean
        get() = prefs.getBoolean(KEY_SHOW_ROUTE_COMPASS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_ROUTE_COMPASS, value).apply()

    var rotate90Clockwise: Boolean
        get() = prefs.getBoolean(KEY_ROTATE_90_CW, false)
        set(value) = prefs.edit().putBoolean(KEY_ROTATE_90_CW, value).apply()

    var visibleBlocksCount: Int
        get() = prefs.getInt(KEY_VISIBLE_BLOCKS_COUNT, 5)
        set(value) = prefs.edit().putInt(KEY_VISIBLE_BLOCKS_COUNT, value.coerceIn(1, 10)).apply()

    var rampMinSlopePct: Double
        get() = prefs.getFloat(KEY_RAMP_MIN_SLOPE, 10.0f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_RAMP_MIN_SLOPE, value.toFloat()).apply()

    var rampMaxSlopePct: Double
        get() = prefs.getFloat(KEY_RAMP_MAX_SLOPE, 15.0f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_RAMP_MAX_SLOPE, value.toFloat()).apply()

    var useTopographicCalculation: Boolean
        get() = prefs.getBoolean(KEY_USE_TOPOGRAPHIC_CALCULATION, false)
        set(value) = prefs.edit().putBoolean(KEY_USE_TOPOGRAPHIC_CALCULATION, value).apply()

    var showHairpins: Boolean
        get() = prefs.getBoolean(KEY_SHOW_HAIRPINS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_HAIRPINS, value).apply()

    var showPoiTowns: Boolean
        get() = prefs.getBoolean(KEY_SHOW_POI_TOWNS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_POI_TOWNS, value).apply()

    var showPoiWater: Boolean
        get() = prefs.getBoolean(KEY_SHOW_POI_WATER, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_POI_WATER, value).apply()

    var showPoiViewpoints: Boolean
        get() = prefs.getBoolean(KEY_SHOW_POI_VIEWPOINTS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_POI_VIEWPOINTS, value).apply()

    var showPoiSummits: Boolean
        get() = prefs.getBoolean(KEY_SHOW_POI_SUMMITS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_POI_SUMMITS, value).apply()

    var show3dZoomControls: Boolean
        get() = prefs.getBoolean(KEY_SHOW_3D_ZOOM, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_3D_ZOOM, value).apply()

    var customMapProvider: String
        get() = prefs.getString(KEY_CUSTOM_MAP_PROVIDER, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_MAP_PROVIDER, value).apply()

    var altimetriaStyle: AltimetriaStyle
        get() {
            val key = prefs.getString(KEY_ALTIMETRIA_STYLE, AltimetriaStyle.CLASSIC.key) ?: AltimetriaStyle.CLASSIC.key
            return AltimetriaStyle.fromKey(key)
        }
        set(value) = prefs.edit().putString(KEY_ALTIMETRIA_STYLE, value.key).apply()

    var climbAltimetriaStyle: AltimetriaStyle
        get() {
            val key = prefs.getString(KEY_CLIMB_ALTIMETRIA_STYLE, AltimetriaStyle.CLASSIC.key) ?: AltimetriaStyle.CLASSIC.key
            return AltimetriaStyle.fromKey(key)
        }
        set(value) = prefs.edit().putString(KEY_CLIMB_ALTIMETRIA_STYLE, value.key).apply()

    // --- PRO FEATURES (NIVEL SDK MÁXIMO) ---
    var smartZoomEnabled: Boolean
        get() = prefs.getBoolean(KEY_SMART_ZOOM_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SMART_ZOOM_ENABLED, value).apply()

    var oasisTrackingEnabled: Boolean
        get() = prefs.getBoolean(KEY_OASIS_TRACKING_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_OASIS_TRACKING_ENABLED, value).apply()

    var virtualPacerEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIRTUAL_PACER_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIRTUAL_PACER_ENABLED, value).apply()

    var switchbackRenderingEnabled: Boolean
        get() = prefs.getBoolean(KEY_SWITCHBACK_RENDERING_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SWITCHBACK_RENDERING_ENABLED, value).apply()

    var energyManagementEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENERGY_MANAGEMENT_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ENERGY_MANAGEMENT_ENABLED, value).apply()

    // --- ELITE FEATURES (PAYWALL) ---
    var licenseKey: String
        get() = prefs.getString(KEY_LICENSE_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LICENSE_KEY, value).apply()

    val isEliteUnlocked: Boolean
        get() = licenseKey == "ALTGRAPH-ELITE-2026"
        
    var weatherOverlayEnabled: Boolean
        get() = prefs.getBoolean(KEY_WEATHER_OVERLAY_ENABLED, true) && isEliteUnlocked
        set(value) = prefs.edit().putBoolean(KEY_WEATHER_OVERLAY_ENABLED, value).apply()

    var stravaSegmentsEnabled: Boolean
        get() = prefs.getBoolean(KEY_STRAVA_SEGMENTS_ENABLED, true) && isEliteUnlocked
        set(value) = prefs.edit().putBoolean(KEY_STRAVA_SEGMENTS_ENABLED, value).apply()


    var climbRotate90Clockwise: Boolean
        get() = prefs.getBoolean(KEY_CLIMB_ROTATE_90_CW, false)
        set(value) = prefs.edit().putBoolean(KEY_CLIMB_ROTATE_90_CW, value).apply()
        
    var routeMapHeadingUp: Boolean
        get() = prefs.getBoolean(KEY_ROUTE_MAP_HEADING_UP, false)
        set(value) = prefs.edit().putBoolean(KEY_ROUTE_MAP_HEADING_UP, value).apply()

    companion object {
        private const val PREFS_NAME = "altgraph_settings"
        private const val KEY_FONT_FAMILY = "font_family_key"
        private const val KEY_BLOCK_SIZE = "block_size_meters"
        private const val KEY_THRESHOLD_ATTACK = "threshold_attack_pct"
        private const val KEY_TARGET_VAM = "target_vam"
        private const val KEY_ATTACK_ALERT_ENABLED = "attack_alert_enabled"
        private const val KEY_ASPHALT_FACTOR = "asphalt_factor"
        private const val KEY_FONT_SIZE_3D_SCALE = "font_size_3d_scale"
        private const val KEY_SHOW_BLOCK_PERCENTAGES = "show_block_percentages"
        private const val KEY_LOOKAHEAD_METERS_3D = "lookahead_meters_3d"
        private const val KEY_SHOW_3D_COTAS = "show_3d_cotas"
        private const val KEY_SHOW_3D_RAMPS = "show_3d_ramps"
        private const val KEY_SHOW_MAX_GRADIENT = "show_max_gradient"
        private const val KEY_SHOW_ROUTE_COMPASS = "show_route_compass"
        private const val KEY_ROTATE_90_CW = "rotate_90_clockwise"
        private const val KEY_CLIMB_ROTATE_90_CW = "climb_rotate_90_clockwise"
        private const val KEY_ROUTE_MAP_HEADING_UP = "route_map_heading_up"
        private const val KEY_VISIBLE_BLOCKS_COUNT = "visible_blocks_count"
        private const val KEY_RAMP_MIN_SLOPE = "ramp_min_slope_pct"
        private const val KEY_RAMP_MAX_SLOPE = "ramp_max_slope_pct"
        private const val KEY_USE_TOPOGRAPHIC_CALCULATION = "use_topographic_calculation"
        private const val KEY_SHOW_HAIRPINS = "show_hairpins"
        private const val KEY_SHOW_POI_TOWNS = "show_poi_towns"
        private const val KEY_SHOW_POI_WATER = "show_poi_water"
        private const val KEY_SHOW_POI_VIEWPOINTS = "show_poi_viewpoints"
        private const val KEY_SHOW_POI_SUMMITS = "show_poi_summits"
        private const val KEY_SHOW_3D_ZOOM = "show_3d_zoom_controls"
        private const val KEY_CUSTOM_MAP_PROVIDER = "custom_map_provider_key"
        private const val KEY_ALTIMETRIA_STYLE = "altimetria_style_key"
        private const val KEY_CLIMB_ALTIMETRIA_STYLE = "climb_altimetria_style_key"

        private const val KEY_SMART_ZOOM_ENABLED = "smart_zoom_enabled"
        private const val KEY_OASIS_TRACKING_ENABLED = "oasis_tracking_enabled"
        private const val KEY_VIRTUAL_PACER_ENABLED = "virtual_pacer_enabled"
        private const val KEY_SWITCHBACK_RENDERING_ENABLED = "switchback_rendering_enabled"
        private const val KEY_ENERGY_MANAGEMENT_ENABLED = "energy_management_enabled"

        private const val KEY_LICENSE_KEY = "license_key"
        private const val KEY_WEATHER_OVERLAY_ENABLED = "weather_overlay_enabled"
        private const val KEY_STRAVA_SEGMENTS_ENABLED = "strava_segments_enabled"

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

enum class AltimetriaStyle(val key: String, val icon: String, val title: String, val description: String) {
    CLASSIC(
        key = "classic",
        icon = "🏔️",
        title = "Clásica 3D (Por defecto)",
        description = "Perfil profesional con bisel 3D, degradado suave del 0 al 15% y cotas nítidas"
    ),
    HORIZON_ISOMETRIC(
        key = "horizon_iso",
        icon = "✈️",
        title = "Horizonte Isométrico",
        description = "Perspectiva de cabina proyectada hacia el horizonte con haz luminoso del ciclista"
    ),
    TACTICAL_OASES(
        key = "tactical_oases",
        icon = "❄️",
        title = "Oasis y Crisoles Tácticos",
        description = "Destaca descansillos de recuperación en azul hielo y muros duros en fuego térmico"
    ),
    DYNAMIC_FORCE_FIELD(
        key = "force_field",
        icon = "⚡",
        title = "Campo de Fuerza y Fatiga",
        description = "Relieve biométrico de inercia y modulación dinámica de dureza acumulada"
    ),
    MONOLITHIC_OBSIDIAN(
        key = "monolithic",
        icon = "💎",
        title = "Monolito Obsidiana y Plasma",
        description = "Cristal oscuro facetado con núcleo de plasma luminoso y cresta láser"
    ),
    GLOBAL_ISOMETRIC(
        key = "global_iso",
        icon = "🗺️",
        title = "Mapa Isométrico Global",
        description = "Visión arquitectónica 3D de todo el recorrido en forma de serpiente"
    );

    companion object {
        fun fromKey(key: String): AltimetriaStyle {
            return entries.find { it.key == key } ?: CLASSIC
        }
    }
}
