package com.example.altgraph

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = AppPreferences.getInstance(this)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#09090B"))
            isFillViewport = true
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(32, 32, 32, 32)
        }

        // Header
        val logo = ImageView(this).apply {
            setImageResource(R.drawable.ic_altigraph_logo)
            layoutParams = LinearLayout.LayoutParams(160, 160).apply {
                bottomMargin = 16
            }
        }

        val title = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "${getString(R.string.title_settings)} • v0.2"
            textSize = 13f
            setTextColor(Color.parseColor("#A1A1AA"))
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }

        // Section 1: Block Size
        val blockLabel = createSectionLabel(getString(R.string.setting_block_size))
        val blockValueText = createValueText("${prefs.blockSizeMeters.toInt()} m")
        val blockButtonsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val blockSizes = listOf(50.0, 100.0, 250.0, 500.0)
        blockSizes.forEach { size ->
            val btn = Button(this).apply {
                text = "${size.toInt()}m"
                setOnClickListener {
                    prefs.blockSizeMeters = size
                    blockValueText.text = "${size.toInt()} m"
                }
            }
            blockButtonsLayout.addView(btn)
        }

        // Section 2: Attack Alert Threshold
        val attackLabel = createSectionLabel(getString(R.string.setting_attack_threshold))
        val attackValueText = createValueText("${prefs.thresholdAttackPct.toInt()}%")
        val attackButtonsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val thresholds = listOf(8.0, 10.0, 12.0, 15.0)
        thresholds.forEach { thresh ->
            val btn = Button(this).apply {
                text = "${thresh.toInt()}%"
                setOnClickListener {
                    prefs.thresholdAttackPct = thresh
                    attackValueText.text = "${thresh.toInt()}%"
                }
            }
            attackButtonsLayout.addView(btn)
        }

        // Section 3: Target VAM
        val vamLabel = createSectionLabel(getString(R.string.setting_target_vam))
        val vamValueText = createValueText("${prefs.targetVam} m/h")
        val vamControlsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val vamMinusBtn = Button(this).apply {
            text = "- 50"
            setOnClickListener {
                if (prefs.targetVam > 500) {
                    prefs.targetVam -= 50
                    vamValueText.text = "${prefs.targetVam} m/h"
                }
            }
        }

        val vamPlusBtn = Button(this).apply {
            text = "+ 50"
            setOnClickListener {
                if (prefs.targetVam < 2000) {
                    prefs.targetVam += 50
                    vamValueText.text = "${prefs.targetVam} m/h"
                }
            }
        }

        vamControlsLayout.addView(vamMinusBtn)
        vamControlsLayout.addView(vamPlusBtn)

        // Section 4: Toggle Attack Alerts
        val switchAlerts = Switch(this).apply {
            text = getString(R.string.setting_enable_alerts)
            setTextColor(Color.WHITE)
            isChecked = prefs.attackAlertEnabled
            setOnCheckedChangeListener { _, isChecked ->
                prefs.attackAlertEnabled = isChecked
            }
            setPadding(16, 24, 16, 24)
        }

        // Status Footer
        val statusText = TextView(this).apply {
            text = "✓ " + getString(R.string.status_saved)
            textSize = 12f
            setTextColor(Color.parseColor("#22C55E"))
            gravity = Gravity.CENTER
            setPadding(0, 24, 0, 24)
        }

        rootLayout.addView(logo)
        rootLayout.addView(title)
        rootLayout.addView(subtitle)

        rootLayout.addView(blockLabel)
        rootLayout.addView(blockValueText)
        rootLayout.addView(blockButtonsLayout)

        rootLayout.addView(attackLabel)
        rootLayout.addView(attackValueText)
        rootLayout.addView(attackButtonsLayout)

        rootLayout.addView(vamLabel)
        rootLayout.addView(vamValueText)
        rootLayout.addView(vamControlsLayout)

        rootLayout.addView(switchAlerts)
        rootLayout.addView(statusText)

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }

    private fun createSectionLabel(textStr: String): TextView {
        return TextView(this).apply {
            text = textStr
            textSize = 14f
            setTextColor(Color.parseColor("#38BDF8"))
            setPadding(0, 16, 0, 4)
        }
    }

    private fun createValueText(textStr: String): TextView {
        return TextView(this).apply {
            text = textStr
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(0, 0, 0, 8)
        }
    }
}