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

    var rotate90Clockwise: Boolean
        get() = prefs.getBoolean(KEY_ROTATE_90_CW, false)
        set(value) = prefs.edit().putBoolean(KEY_ROTATE_90_CW, value).apply()

    var visibleBlocksCount: Int
        get() = prefs.getInt(KEY_VISIBLE_BLOCKS_COUNT, 5)
        set(value) = prefs.edit().putInt(KEY_VISIBLE_BLOCKS_COUNT, value.coerceIn(1, 10)).apply()

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
        private const val KEY_ROTATE_90_CW = "rotate_90_cw"
        private const val KEY_VISIBLE_BLOCKS_COUNT = "visible_blocks_count"

        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(context: Context): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}