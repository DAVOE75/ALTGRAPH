package com.example.altgraph

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast

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
            setPadding(24, 24, 24, 32)
        }

        // Header Card
        val headerCard = createCardContainer()
        val logo = ImageView(this).apply {
            setImageResource(R.drawable.ic_altigraph_logo)
            layoutParams = LinearLayout.LayoutParams(140, 140).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = 12
            }
        }

        val title = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 26f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "${getString(R.string.title_settings)} • v0.2"
            textSize = 13f
            setTextColor(Color.parseColor("#A1A1AA"))
            gravity = Gravity.CENTER
            setPadding(0, 4, 0, 8)
        }

        headerCard.addView(logo)
        headerCard.addView(title)
        headerCard.addView(subtitle)

        // Section 1: Block Size Card
        val blockCard = createCardContainer()
        val blockLabel = createSectionLabel(getString(R.string.setting_block_size))
        val blockValueText = createValueText("${prefs.blockSizeMeters.toInt()} m")
        val blockRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val blockButtons = mutableListOf<Button>()
        val blockSizes = listOf(50.0, 100.0, 250.0, 500.0)

        blockSizes.forEach { size ->
            val btn = createSegmentButton("${size.toInt()}m", prefs.blockSizeMeters == size) {
                prefs.blockSizeMeters = size
                blockValueText.text = "${size.toInt()} m"
                updateSegmentActiveStates(blockButtons, blockSizes.indexOf(size))
            }
            blockButtons.add(btn)
            blockRow.addView(btn)
        }

        blockCard.addView(blockLabel)
        blockCard.addView(blockValueText)
        blockCard.addView(blockRow)

        // Section 2: Attack Threshold Card
        val attackCard = createCardContainer()
        val attackLabel = createSectionLabel(getString(R.string.setting_attack_threshold))
        val attackValueText = createValueText("${prefs.thresholdAttackPct.toInt()}%")
        val attackRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val attackButtons = mutableListOf<Button>()
        val thresholds = listOf(8.0, 10.0, 12.0, 15.0)

        thresholds.forEach { thresh ->
            val btn = createSegmentButton("${thresh.toInt()}%", prefs.thresholdAttackPct == thresh) {
                prefs.thresholdAttackPct = thresh
                attackValueText.text = "${thresh.toInt()}%"
                updateSegmentActiveStates(attackButtons, thresholds.indexOf(thresh))
            }
            attackButtons.add(btn)
            attackRow.addView(btn)
        }

        attackCard.addView(attackLabel)
        attackCard.addView(attackValueText)
        attackCard.addView(attackRow)

        // Section 3: Target VAM Card
        val vamCard = createCardContainer()
        val vamLabel = createSectionLabel(getString(R.string.setting_target_vam))
        val vamValueText = createValueText("${prefs.targetVam} m/h")
        val vamRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val vamMinusBtn = createActionButton("- 50 m/h") {
            if (prefs.targetVam > 500) {
                prefs.targetVam -= 50
                vamValueText.text = "${prefs.targetVam} m/h"
            }
        }

        val vamPlusBtn = createActionButton("+ 50 m/h") {
            if (prefs.targetVam < 2000) {
                prefs.targetVam += 50
                vamValueText.text = "${prefs.targetVam} m/h"
            }
        }

        vamRow.addView(vamMinusBtn)
        vamRow.addView(vamPlusBtn)

        vamCard.addView(vamLabel)
        vamCard.addView(vamValueText)
        vamCard.addView(vamRow)

        // Section 4: Asphalt Quality Card
        val asphaltCard = createCardContainer()
        val asphaltLabel = createSectionLabel(getString(R.string.setting_asphalt_factor))
        val currentAsphaltQuality = FatigueGradeCalculator.AsphaltQuality.entries.find { it.factor == prefs.asphaltFactor } ?: FatigueGradeCalculator.AsphaltQuality.GOOD
        val asphaltValueText = createValueText("${currentAsphaltQuality.labelEs} (TA = ${prefs.asphaltFactor})")
        val asphaltRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val asphaltButtons = mutableListOf<Button>()
        val qualities = FatigueGradeCalculator.AsphaltQuality.entries

        qualities.forEach { q ->
            val btn = createSegmentButton(q.labelEs.take(6), prefs.asphaltFactor == q.factor) {
                prefs.asphaltFactor = q.factor
                asphaltValueText.text = "${q.labelEs} (TA = ${q.factor})"
                updateSegmentActiveStates(asphaltButtons, qualities.indexOf(q))
            }
            asphaltButtons.add(btn)
            asphaltRow.addView(btn)
        }

        asphaltCard.addView(asphaltLabel)
        asphaltCard.addView(asphaltValueText)
        asphaltCard.addView(asphaltRow)

        // Section 5: Toggle Attack Alerts Card
        val alertCard = createCardContainer()
        val switchAlerts = Switch(this).apply {
            text = getString(R.string.setting_enable_alerts)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.attackAlertEnabled
            setOnCheckedChangeListener { _, isChecked ->
                prefs.attackAlertEnabled = isChecked
            }
        }
        alertCard.addView(switchAlerts)

        // Save & Exit Button
        val saveExitButton = Button(this).apply {
            text = "💾 " + getString(R.string.btn_save_and_exit)
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.BLACK)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#22C55E"))
                cornerRadius = 24f
            }
            setPadding(32, 24, 32, 24)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
                bottomMargin = 16
            }
            setOnClickListener {
                Toast.makeText(this@MainActivity, getString(R.string.status_saved), Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        rootLayout.addView(headerCard)
        rootLayout.addView(blockCard)
        rootLayout.addView(attackCard)
        rootLayout.addView(vamCard)
        rootLayout.addView(asphaltCard)
        rootLayout.addView(alertCard)
        rootLayout.addView(saveExitButton)

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }

    private fun createCardContainer(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#18181B"))
                setStroke(2, Color.parseColor("#27272A"))
                cornerRadius = 24f
            }
            setPadding(24, 20, 24, 20)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
        }
    }

    private fun createSectionLabel(textStr: String): TextView {
        return TextView(this).apply {
            text = textStr
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#38BDF8"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 4)
        }
    }

    private fun createValueText(textStr: String): TextView {
        return TextView(this).apply {
            text = textStr
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 12)
        }
    }

    private fun createSegmentButton(textStr: String, isActive: Boolean, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = textStr
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(8, 12, 8, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f).apply {
                leftMargin = 4
                rightMargin = 4
            }
            applyButtonStyle(this, isActive)
            setOnClickListener { onClick() }
        }
    }

    private fun createActionButton(textStr: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = textStr
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f).apply {
                leftMargin = 8
                rightMargin = 8
            }
            applyButtonStyle(this, false)
            setOnClickListener { onClick() }
        }
    }

    private fun updateSegmentActiveStates(buttons: List<Button>, activeIndex: Int) {
        buttons.forEachIndexed { idx, btn ->
            applyButtonStyle(btn, idx == activeIndex)
        }
    }

    private fun applyButtonStyle(btn: Button, isActive: Boolean) {
        if (isActive) {
            btn.setTextColor(Color.BLACK)
            btn.background = GradientDrawable().apply {
                setColor(Color.parseColor("#38BDF8"))
                cornerRadius = 16f
            }
        } else {
            btn.setTextColor(Color.WHITE)
            btn.background = GradientDrawable().apply {
                setColor(Color.parseColor("#27272A"))
                cornerRadius = 16f
            }
        }
    }
}