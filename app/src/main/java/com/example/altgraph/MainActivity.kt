package com.example.altgraph

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
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
            setPadding(20, 20, 20, 28)
        }

        // 1. Header Card (Logo, Título y Versión)
        val headerCard = createCardContainer()
        val logo = ImageView(this).apply {
            setImageResource(R.drawable.ic_altigraph_logo)
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = 8
            }
        }

        val title = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "${getString(R.string.title_settings)} • v0.2.3"
            textSize = 12f
            setTextColor(Color.parseColor("#A1A1AA"))
            gravity = Gravity.CENTER
            setPadding(0, 2, 0, 4)
        }

        headerCard.addView(logo)
        headerCard.addView(title)
        headerCard.addView(subtitle)

        // 2. Bar de Pestañas Categorizadas
        val tabBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
        }

        // Contenedores de las 4 Pestañas
        val tabEstiloContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val tab3dContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val tab2dContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val tabVamContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        val tabContainers = listOf(
            tabEstiloContainer,
            tab3dContainer,
            tab2dContainer,
            tabVamContainer
        )

        val tabTitles = listOf("🎨 Estilo", "🏔️ 3D", "📊 2D", "🚴 VAM")
        val tabButtons = mutableListOf<Button>()

        fun selectTab(activeIdx: Int) {
            tabContainers.forEachIndexed { idx, container ->
                container.visibility = if (idx == activeIdx) View.VISIBLE else View.GONE
            }
            tabButtons.forEachIndexed { idx, btn ->
                if (idx == activeIdx) {
                    btn.setTextColor(Color.BLACK)
                    btn.background = GradientDrawable().apply {
                        setColor(Color.parseColor("#38BDF8"))
                        cornerRadius = 18f
                    }
                } else {
                    btn.setTextColor(Color.WHITE)
                    btn.background = GradientDrawable().apply {
                        setColor(Color.parseColor("#27272A"))
                        cornerRadius = 18f
                    }
                }
            }
        }

        tabTitles.forEachIndexed { idx, titleStr ->
            val btn = Button(this).apply {
                text = titleStr
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setPadding(4, 12, 4, 12)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f).apply {
                    leftMargin = 3
                    rightMargin = 3
                }
                setOnClickListener { selectTab(idx) }
            }
            tabButtons.add(btn)
            tabBar.addView(btn)
        }

        // ==========================================
        // PESTAÑA 1: 🎨 ESTILO Y APARIENCIA
        // ==========================================

        // Opción: Tipografía (Dropdown Spinner)
        val fontCard = createCardContainer()
        val fontLabel = createSectionLabel(getString(R.string.setting_font_family))
        val currentFontOpt = FontHelper.options.find { it.key == prefs.fontFamilyKey } ?: FontHelper.options[0]
        val fontValueText = createValueText(currentFontOpt.labelEs)

        val fontSpinner = Spinner(this).apply {
            val adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                FontHelper.options.map { it.labelEs }
            )
            setAdapter(adapter)

            val selectedIndex = FontHelper.options.indexOfFirst { it.key == prefs.fontFamilyKey }
            if (selectedIndex >= 0) setSelection(selectedIndex)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val opt = FontHelper.options[position]
                    prefs.fontFamilyKey = opt.key
                    fontValueText.text = opt.labelEs
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 4
                bottomMargin = 4
            }
        }

        fontCard.addView(fontLabel)
        fontCard.addView(fontValueText)
        fontCard.addView(fontSpinner)

        // Opción: Girar 90° Modo Apaisado
        val rotate90Card = createCardContainer()
        val switchRotate90 = Switch(this).apply {
            text = getString(R.string.setting_rotate_90_cw)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.rotate90Clockwise
            setOnCheckedChangeListener { _, isChecked ->
                prefs.rotate90Clockwise = isChecked
            }
        }
        rotate90Card.addView(switchRotate90)

        // Opción: Tamaño de Letra 3D
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

        tabEstiloContainer.addView(fontCard)
        tabEstiloContainer.addView(rotate90Card)
        tabEstiloContainer.addView(font3dCard)

        // ==========================================
        // PESTAÑA 2: 🏔️ ALTIMETRÍA 3D
        // ==========================================

        fun formatLookaheadText(meters: Int): String {
            return if (meters < 1000) {
                "$meters m"
            } else {
                "${meters / 1000} km"
            }
        }

        // Opción: Anticipación 3D
        val lookaheadCard = createCardContainer()
        val lookaheadLabel = createSectionLabel(getString(R.string.setting_lookahead_meters_3d))
        val lookaheadValueText = createValueText(formatLookaheadText(prefs.lookaheadMeters3d))
        val lookaheadRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val lookaheadMinusBtn = createActionButton("- Reducir") {
            val curr = prefs.lookaheadMeters3d
            val nextVal = when {
                curr > 1000 -> curr - 1000
                curr == 1000 -> 500
                curr > 200 -> curr - 50
                else -> 200
            }
            prefs.lookaheadMeters3d = nextVal
            lookaheadValueText.text = formatLookaheadText(nextVal)
        }

        val lookaheadPlusBtn = createActionButton("+ Aumentar") {
            val curr = prefs.lookaheadMeters3d
            val nextVal = when {
                curr < 500 -> curr + 50
                curr == 500 -> 1000
                curr < 10000 -> curr + 1000
                else -> 10000
            }
            prefs.lookaheadMeters3d = nextVal
            lookaheadValueText.text = formatLookaheadText(nextVal)
        }

        lookaheadRow.addView(lookaheadMinusBtn)
        lookaheadRow.addView(lookaheadPlusBtn)

        lookaheadCard.addView(lookaheadLabel)
        lookaheadCard.addView(lookaheadValueText)
        lookaheadCard.addView(lookaheadRow)

        // Opción: Mostrar Cotas en 3D
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

        // Opción: Mostrar Rampas Duras
        val showRampsCard = createCardContainer()
        val switchShowRamps = Switch(this).apply {
            text = getString(R.string.setting_show_3d_ramps)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.show3dRamps
            setOnCheckedChangeListener { _, isChecked ->
                prefs.show3dRamps = isChecked
            }
        }
        showRampsCard.addView(switchShowRamps)

        // Opción: Pendiente Mínima de Rampa
        val rampMinCard = createCardContainer()
        val rampMinLabel = createSectionLabel(getString(R.string.setting_ramp_min_slope))
        val rampMinValueText = createValueText("${prefs.rampMinSlopePct.toInt()}%")
        val rampMinRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val rampMinMinusBtn = createActionButton("- 1%") {
            if (prefs.rampMinSlopePct > 5.0) {
                prefs.rampMinSlopePct -= 1.0
                rampMinValueText.text = "${prefs.rampMinSlopePct.toInt()}%"
            }
        }

        val rampMinPlusBtn = createActionButton("+ 1%") {
            if (prefs.rampMinSlopePct < prefs.rampMaxSlopePct - 1.0) {
                prefs.rampMinSlopePct += 1.0
                rampMinValueText.text = "${prefs.rampMinSlopePct.toInt()}%"
            }
        }

        rampMinRow.addView(rampMinMinusBtn)
        rampMinRow.addView(rampMinPlusBtn)

        rampMinCard.addView(rampMinLabel)
        rampMinCard.addView(rampMinValueText)
        rampMinCard.addView(rampMinRow)

        // Opción: Pendiente Máxima de Rampa
        val rampMaxCard = createCardContainer()
        val rampMaxLabel = createSectionLabel(getString(R.string.setting_ramp_max_slope))
        val rampMaxValueText = createValueText("${prefs.rampMaxSlopePct.toInt()}%")
        val rampMaxRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val rampMaxMinusBtn = createActionButton("- 1%") {
            if (prefs.rampMaxSlopePct > prefs.rampMinSlopePct + 1.0) {
                prefs.rampMaxSlopePct -= 1.0
                rampMaxValueText.text = "${prefs.rampMaxSlopePct.toInt()}%"
            }
        }

        val rampMaxPlusBtn = createActionButton("+ 1%") {
            if (prefs.rampMaxSlopePct < 30.0) {
                prefs.rampMaxSlopePct += 1.0
                rampMaxValueText.text = "${prefs.rampMaxSlopePct.toInt()}%"
            }
        }

        rampMaxRow.addView(rampMaxMinusBtn)
        rampMaxRow.addView(rampMaxPlusBtn)

        rampMaxCard.addView(rampMaxLabel)
        rampMaxCard.addView(rampMaxValueText)
        rampMaxCard.addView(rampMaxRow)

        // Opción: Pendiente Máxima en Encabezado
        val showMaxGradCard = createCardContainer()
        val switchShowMaxGrad = Switch(this).apply {
            text = getString(R.string.setting_show_max_gradient)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.showMaxGradient
            setOnCheckedChangeListener { _, isChecked ->
                prefs.showMaxGradient = isChecked
            }
        }
        showMaxGradCard.addView(switchShowMaxGrad)

        tab3dContainer.addView(lookaheadCard)
        tab3dContainer.addView(showCotasCard)
        tab3dContainer.addView(showRampsCard)
        tab3dContainer.addView(rampMinCard)
        tab3dContainer.addView(rampMaxCard)
        tab3dContainer.addView(showMaxGradCard)

        // ==========================================
        // PESTAÑA 3: 📊 ESTRATEGA 2D
        // ==========================================

        // Opción: Tamaño de Bloque (50m, 100m, 250m, 500m, 1km)
        val blockCard = createCardContainer()
        val blockLabel = createSectionLabel(getString(R.string.setting_block_size))
        val currentBlockText = if (prefs.blockSizeMeters >= 1000.0) "${(prefs.blockSizeMeters / 1000).toInt()} km" else "${prefs.blockSizeMeters.toInt()} m"
        val blockValueText = createValueText(currentBlockText)
        val blockRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        val blockButtons = mutableListOf<Button>()
        val blockSizes = listOf(50.0, 100.0, 250.0, 500.0, 1000.0)

        blockSizes.forEach { size ->
            val labelStr = if (size >= 1000.0) "${(size / 1000).toInt()}km" else "${size.toInt()}m"
            val btn = createSegmentButton(labelStr, prefs.blockSizeMeters == size) {
                prefs.blockSizeMeters = size
                blockValueText.text = if (size >= 1000.0) "${(size / 1000).toInt()} km" else "${size.toInt()} m"
                updateSegmentActiveStates(blockButtons, blockSizes.indexOf(size))
            }
            blockButtons.add(btn)
            blockRow.addView(btn)
        }

        blockCard.addView(blockLabel)
        blockCard.addView(blockValueText)
        blockCard.addView(blockRow)

        // Opción: Tramos Visibles
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

        // Opción: Mostrar % en Bloques
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

        // Opción: Alertas de Ataque
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

        // Opción: Umbral de Ataque
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

        tab2dContainer.addView(blockCard)
        tab2dContainer.addView(visibleBlocksCard)
        tab2dContainer.addView(showPctCard)
        tab2dContainer.addView(alertCard)
        tab2dContainer.addView(attackCard)

        // ==========================================
        // PESTAÑA 4: 🚴 RENDIMIENTO Y VAM
        // ==========================================

        // Opción: VAM Objetivo
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

        // Opción: Calidad de Asfalto (TA)
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

        tabVamContainer.addView(vamCard)
        tabVamContainer.addView(asphaltCard)

        // Botón de Guardar y Salir Fijado al Final
        val saveExitButton = Button(this).apply {
            text = "💾 " + getString(R.string.btn_save_and_exit)
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.BLACK)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#22C55E"))
                cornerRadius = 22f
            }
            setPadding(28, 20, 28, 20)
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
        rootLayout.addView(tabBar)
        rootLayout.addView(tabEstiloContainer)
        rootLayout.addView(tab3dContainer)
        rootLayout.addView(tab2dContainer)
        rootLayout.addView(tabVamContainer)
        rootLayout.addView(saveExitButton)

        scrollView.addView(rootLayout)
        setContentView(scrollView)

        // Seleccionar pestaña por defecto (Tab 0: Estilo)
        selectTab(0)
    }

    private fun createCardContainer(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#18181B"))
                setStroke(2, Color.parseColor("#27272A"))
                cornerRadius = 20f
            }
            setPadding(20, 16, 20, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
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
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 10)
        }
    }

    private fun createSegmentButton(textStr: String, isActive: Boolean, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = textStr
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(6, 10, 6, 10)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f).apply {
                leftMargin = 3
                rightMargin = 3
            }
            applyButtonStyle(this, isActive)
            setOnClickListener { onClick() }
        }
    }

    private fun createActionButton(textStr: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = textStr
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(12, 12, 12, 12)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f).apply {
                leftMargin = 6
                rightMargin = 6
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
                cornerRadius = 14f
            }
        } else {
            btn.setTextColor(Color.WHITE)
            btn.background = GradientDrawable().apply {
                setColor(Color.parseColor("#27272A"))
                cornerRadius = 14f
            }
        }
    }
}