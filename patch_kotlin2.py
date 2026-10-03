import os

def replace_in_file(file_path, replacements):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    for old, new in replacements:
        content = content.replace(old, new)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

app_pref = [
    ('enum class AltimetriaStyle(val key: String, val icon: String, val title: String, val description: String) {',
     'enum class AltimetriaStyle(val key: String, val icon: String, val titleRes: Int, val descRes: Int) {'),
    ('title = "Clásica 3D (Por defecto)",', 'titleRes = R.string.style_classic_title,'),
    ('description = "Perfil profesional con bisel 3D, degradado suave del 0 al 15% y cotas nítidas"', 'descRes = R.string.style_classic_desc'),
    ('title = "Horizonte Isométrico",', 'titleRes = R.string.style_horizon_title,'),
    ('description = "Perspectiva de cabina proyectada hacia el horizonte con haz luminoso del ciclista"', 'descRes = R.string.style_horizon_desc'),
    ('title = "Oasis y Crisoles Tácticos",', 'titleRes = R.string.style_oasis_title,'),
    ('description = "Destaca descansillos de recuperación en azul hielo y muros duros en fuego térmico"', 'descRes = R.string.style_oasis_desc'),
    ('title = "Campo de Fuerza y Fatiga",', 'titleRes = R.string.style_force_title,'),
    ('description = "Relieve biométrico de inercia y modulación dinámica de dureza acumulada"', 'descRes = R.string.style_force_desc'),
    ('title = "Monolito Obsidiana y Plasma",', 'titleRes = R.string.style_monolith_title,'),
    ('description = "Cristal oscuro facetado con núcleo de plasma luminoso y cresta láser"', 'descRes = R.string.style_monolith_desc'),
    ('title = "Mapa Isométrico Global",', 'titleRes = R.string.style_global_title,'),
    ('description = "Visión arquitectónica 3D de todo el recorrido en forma de serpiente"', 'descRes = R.string.style_global_desc')
]
replace_in_file('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/AppPreferences.kt', app_pref)

main_rep = [
    ('val styleValueText = createValueText("${initialStyle.icon} ${initialStyle.title}")',
     'val styleValueText = createValueText("${initialStyle.icon} ${getString(initialStyle.titleRes)}")'),
    ('text = initialStyle.description',
     'text = getString(initialStyle.descRes)'),
    ('styleOptions.map { "${it.icon} ${it.title}" }',
     'styleOptions.map { "${it.icon} ${getString(it.titleRes)}" }'),
    ('styleValueText.text = "${selected.icon} ${selected.title}"',
     'styleValueText.text = "${selected.icon} ${getString(selected.titleRes)}"'),
    ('styleDescText.text = selected.description',
     'styleDescText.text = getString(selected.descRes)'),
    ('Toast.makeText(this@MainActivity, "Vista: ${selected.title}", Toast.LENGTH_SHORT).show()',
     'Toast.makeText(this@MainActivity, getString(R.string.toast_vista, getString(selected.titleRes)), Toast.LENGTH_SHORT).show()'),
    ('val climbStyleValueText = createValueText("${climbInitialStyle.icon} ${climbInitialStyle.title}")',
     'val climbStyleValueText = createValueText("${climbInitialStyle.icon} ${getString(climbInitialStyle.titleRes)}")'),
    ('text = climbInitialStyle.description',
     'text = getString(climbInitialStyle.descRes)'),
    ('climbStyleValueText.text = "${selected.icon} ${selected.title}"',
     'climbStyleValueText.text = "${selected.icon} ${getString(selected.titleRes)}"'),
    ('climbStyleDescText.text = selected.description',
     'climbStyleDescText.text = getString(selected.descRes)'),
    ('Toast.makeText(this@MainActivity, "Visor Puertos: ${selected.title}", Toast.LENGTH_SHORT).show()',
     'Toast.makeText(this@MainActivity, getString(R.string.toast_visor_puertos, getString(selected.titleRes)), Toast.LENGTH_SHORT).show()'),
    ('val eliteLabel = createSectionLabel("👑 ALTGRAPH ELITE 👑")',
     'val eliteLabel = createSectionLabel(getString(R.string.label_elite_header))'),
    ('val eliteSubLabel = createSectionLabel("🎯 Radar ELITE Features")',
     'val eliteSubLabel = createSectionLabel(getString(R.string.label_elite_features))'),
    ('text = "👻 Ghost — Mejor marca anterior\\n(Grabado al terminar la ruta)"',
     'text = getString(R.string.label_ghost)'),
    ('text = "🔋 Barra de energía restante"',
     'text = getString(R.string.label_energy_bar)'),
    ('text = "📍 Iconos POI en la regla"',
     'text = getString(R.string.label_poi_icons)'),
    ('text = "📊 Histograma de pendientes"',
     'text = getString(R.string.label_histogram)'),
    ('text = "🏔️ Radar 3D con perspectiva isométrica"',
     'text = getString(R.string.label_radar_3d)'),
    ('text = "Tema del Radar:"',
     'text = getString(R.string.label_radar_theme)'),
    ('val themes = arrayOf("Estándar", "Oasis", "Crisoles", "Bruma", "Campo de Fuerza")',
     'val themes = arrayOf(getString(R.string.theme_standard), getString(R.string.theme_oasis), getString(R.string.theme_crucibles), getString(R.string.theme_mist), getString(R.string.theme_forcefield))'),
    ('createValueText("${prefs.visibleBlocksCount} tramos")',
     'createValueText("${prefs.visibleBlocksCount} ${getString(R.string.label_tramos)}")'),
    ('visibleBlocksValueText.text = "${prefs.visibleBlocksCount} tramos"',
     'visibleBlocksValueText.text = "${prefs.visibleBlocksCount} ${getString(R.string.label_tramos)}"'),
    ('return if (meters < 1000) {\n                "$meters m"\n            } else {\n                "${meters / 1000} km"\n            }',
     'return if (meters < 1000) {\n                "$meters m"\n            } else {\n                "${meters / 1000} km"\n            }'),
]
replace_in_file('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/MainActivity.kt', main_rep)
