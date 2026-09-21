import re

file_path = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\MainActivity.kt'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add tabProContainer
content = content.replace(
    'val tabVamContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }',
    'val tabVamContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }\n        val tabProContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }'
)

# 2. Add to tabContainers
content = content.replace(
    'tabVamContainer\n        )',
    'tabVamContainer,\n            tabProContainer\n        )'
)

# 3. Add to tabTitles
content = content.replace(
    'val tabTitles = listOf("🎨 Estilo", "🏔️ 3D", "📊 2D", "🚴 VAM")',
    'val tabTitles = listOf("🎨 Estilo", "🏔️ 3D", "📊 2D", "🚴 VAM", "🚀 Pro")'
)

# 4. Add Pro Tab Content just before rootLayout.addView
pro_content = '''
        // ==========================================
        // PESTAÑA 5: 🚀 PRO (MÁXIMO NIVEL SDK)
        // ==========================================

        val proLabel = createSectionLabel("WORLD TOUR PRO FEATURES")
        tabProContainer.addView(proLabel)

        // Smart Zoom
        val smartZoomCard = createCardContainer()
        val switchSmartZoom = Switch(this).apply {
            text = getString(R.string.setting_smart_zoom)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.smartZoomEnabled
            setOnCheckedChangeListener { _, isChecked -> prefs.smartZoomEnabled = isChecked }
        }
        smartZoomCard.addView(switchSmartZoom)
        tabProContainer.addView(smartZoomCard)

        // Oasis Tracking
        val oasisCard = createCardContainer()
        val switchOasis = Switch(this).apply {
            text = getString(R.string.setting_oasis_tracking)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.oasisTrackingEnabled
            setOnCheckedChangeListener { _, isChecked -> prefs.oasisTrackingEnabled = isChecked }
        }
        oasisCard.addView(switchOasis)
        tabProContainer.addView(oasisCard)

        // Virtual Pacer
        val pacerCard = createCardContainer()
        val switchPacer = Switch(this).apply {
            text = getString(R.string.setting_virtual_pacer)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.virtualPacerEnabled
            setOnCheckedChangeListener { _, isChecked -> prefs.virtualPacerEnabled = isChecked }
        }
        pacerCard.addView(switchPacer)
        tabProContainer.addView(pacerCard)

        // Switchback Dynamics
        val switchbackCard = createCardContainer()
        val switchSwitchback = Switch(this).apply {
            text = getString(R.string.setting_switchback_rendering)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.switchbackRenderingEnabled
            setOnCheckedChangeListener { _, isChecked -> prefs.switchbackRenderingEnabled = isChecked }
        }
        switchbackCard.addView(switchSwitchback)
        tabProContainer.addView(switchbackCard)

        // Energy Management
        val energyCard = createCardContainer()
        val switchEnergy = Switch(this).apply {
            text = getString(R.string.setting_energy_management)
            textSize = 15f
            setTextColor(Color.WHITE)
            isChecked = prefs.energyManagementEnabled
            setOnCheckedChangeListener { _, isChecked -> prefs.energyManagementEnabled = isChecked }
        }
        energyCard.addView(switchEnergy)
        tabProContainer.addView(energyCard)

        // Final Button Layout
'''
content = content.replace(
    '        val saveExitButton = Button(this).apply {',
    pro_content + '        val saveExitButton = Button(this).apply {'
)

# 5. Add to rootLayout
content = content.replace(
    'rootLayout.addView(tabVamContainer)',
    'rootLayout.addView(tabVamContainer)\n        rootLayout.addView(tabProContainer)'
)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("MainActivity patched successfully")
