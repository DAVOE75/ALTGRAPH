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
import java.util.Locale
import kotlin.math.abs

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
            text = "${getString(R.string.title_settings)} • v0.2.1"
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

        // Section 2: Visible Blocks Count Card (1 a 10 tramos, por defecto 5)
        val visibleBlocksCard = createCardContainer()
        val visibleBlocksLabel = createSectionLabel(getString(R.string.setting_visible_blocks_count))
        val visibleBlocksValueText = createValueText("${prefs.visibleBlocksCount} tramos")
        val visibleBlocksRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val visibleMinusBtn = createActionButton("- 1") {
            if (prefs.visibleBlocksCount > 1) {
                prefs.visibleBlocksCount -= 1
                visibleBlocksValueText.text = "${prefs.visibleBlocksCount} tramos"
            }
        }

        val visiblePlusBtn = createActionButton("+ 1") {
            if (prefs.visibleBlocksCount < 10) {
                prefs.visibleBlocksCount += 1
                visibleBlocksValueText.text = "${prefs.visibleBlocksCount} tramos"
            }
        }

        visibleBlocksRow.addView(visibleMinusBtn)
        visibleBlocksRow.addView(visiblePlusBtn)

        visibleBlocksCard.addView(visibleBlocksLabel)
        visibleBlocksCard.addView(visibleBlocksValueText)
        visibleBlocksCard.addView(visibleBlocksRow)

        // Section 3: 3D Lookahead Distance Card
        val lookaheadCard = createCardContainer()
        val lookaheadLabel = createSectionLabel(getString(R.string.setting_lookahead_meters_3d))
        val lookaheadValueText = createValueText("${prefs.lookaheadMeters3d} m")
        val lookaheadRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val lookaheadMinusBtn = createActionButton("- 50 m") {
            if (prefs.lookaheadMeters3d > 200) {
                prefs.lookaheadMeters3d -= 50
                lookaheadValueText.text = "${prefs.lookaheadMeters3d} m"
            }
        }

        val lookaheadPlusBtn = createActionButton("+ 50 m") {
            if (prefs.lookaheadMeters3d < 500) {
                prefs.lookaheadMeters3d += 50
                lookaheadValueText.text = "${prefs.lookaheadMeters3d} m"
            }
        }

        lookaheadRow.addView(lookaheadMinusBtn)
        lookaheadRow.addView(lookaheadPlusBtn)

        lookaheadCard.addView(lookaheadLabel)
        lookaheadCard.addView(lookaheadValueText)
        lookaheadCard.addView(lookaheadRow)

        // Section 4: Attack Threshold Card
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

        // Section 5: Target VAM Card
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

        // Section 6: Asphalt Quality Card (Con 1 solo decimal y Bueno por defecto)
        val asphaltCard = createCardContainer()
        val asphaltLabel = createSectionLabel(getString(R.string.setting_asphalt_factor))
        val currentAsphaltQuality = FatigueGradeCalculator.AsphaltQuality.entries.find { abs(it.factor - prefs.asphaltFactor) < 0.05 } ?: FatigueGradeCalculator.AsphaltQuality.GOOD
        val asphaltValueText = createValueText("${currentAsphaltQuality.labelEs} (TA = %.1f)".format(Locale.US, currentAsphaltQuality.factor))
        val asphaltRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val asphaltButtons = mutableListOf<Button>()
        val qualities = FatigueGradeCalculator.AsphaltQuality.entries

        qualities.forEach { q ->
            val isActive = abs(prefs.asphaltFactor - q.factor) < 0.05
            val btn = createSegmentButton(q.labelEs.take(6), isActive) {
                prefs.asphaltFactor = q.factor
                asphaltValueText.text = "${q.labelEs} (TA = %.1f)".format(Locale.US, q.factor)
                updateSegmentActiveStates(asphaltButtons, qualities.indexOf(q))
            }
            asphaltButtons.add(btn)
            asphaltRow.addView(btn)
        }

        asphaltCard.addView(asphaltLabel)
        asphaltCard.addView(asphaltValueText)
        asphaltCard.addView(asphaltRow)

        // Section 7: 3D Font Size Scale Card
        val font3dCard = createCardContainer()
        val font3dLabel = createSectionLabel(getString(R.string.setting_font_size_3d))
        val fontScales = listOf(1.0f to getString(R.string.font_normal), 1.25f to getString(R.string.font_large), 1.5f to getString(R.string.font_xl))
        val currentFontLabel = fontScales.find { it.first == prefs.fontSize3dScale }?.second ?: getString(R.string.font_normal)
        val font3dValueText = createValueText(currentFontLabel)
        val font3dRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val fontButtons = mutableListOf<Button>()
        fontScales.forEach { (scale, labelStr) ->
            val btn = createSegmentButton(labelStr, prefs.fontSize3dScale == scale) {
                prefs.fontSize3dScale = scale
                font3dValueText.text = labelStr
                updateSegmentActiveStates(fontButtons, fontScales.indexOfFirst { it.first == scale })
            }
            fontButtons.add(btn)
            font3dRow.addView(btn)
        }

        font3dCard.addView(font3dLabel)
        font3dCard.addView(font3dValueText)
        font3dCard.addView(font3dRow)

        // Section 8: Toggle Show % in Profile Blocks Card
        val showPctCard = createCardContainer()
        val switchShowPct = Switch(this).apply {
            text = getString(R.string.setting_show_block_percentages)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.showBlockPercentages
            setOnCheckedChangeListener { _, isChecked ->
                prefs.showBlockPercentages = isChecked
            }
        }
        showPctCard.addView(switchShowPct)

        // Section 9: Toggle Show 3D Cotas Card
        val showCotasCard = createCardContainer()
        val switchShowCotas = Switch(this).apply {
            text = getString(R.string.setting_show_3d_cotas)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.show3dCotas
            setOnCheckedChangeListener { _, isChecked ->
                prefs.show3dCotas = isChecked
            }
        }
        showCotasCard.addView(switchShowCotas)

        // Section 10: Toggle Attack Alerts Card
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
        rootLayout.addView(visibleBlocksCard)
        rootLayout.addView(lookaheadCard)
        rootLayout.addView(attackCard)
        rootLayout.addView(vamCard)
        rootLayout.addView(asphaltCard)
        rootLayout.addView(font3dCard)
        rootLayout.addView(showPctCard)
        rootLayout.addView(showCotasCard)
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