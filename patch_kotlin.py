import re

def replace_in_file(file_path, replacements):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

# MainActivity.kt
main_rep = [
    ('val tabTitles = listOf("🎨 Estilo", "🏔️ 3D", "📊 2D", "🚴 VAM", "🚀 Pro", "👑 Elite")',
     'val tabTitles = listOf(getString(R.string.tab_style), getString(R.string.tab_3d), getString(R.string.tab_2d), getString(R.string.tab_vam), getString(R.string.tab_pro), getString(R.string.tab_elite))'),
    ('createSectionLabel("VISTA Y MODELO DE ALTIMETRÍA (RUTA GLOBAL)")', 'createSectionLabel(getString(R.string.label_vista_global))'),
    ('createSectionLabel("VISTA Y MODELO DE ALTIMETRÍA (VISOR DE PUERTOS)")', 'createSectionLabel(getString(R.string.label_vista_puertos))'),
    ('text = getString(R.string.setting_rotate_90_cw) + " (RUTA GLOBAL)"', 'text = getString(R.string.setting_rotate_90_cw) + getString(R.string.suffix_global)'),
    ('text = getString(R.string.setting_rotate_90_cw) + " (VISOR PUERTOS)"', 'text = getString(R.string.setting_rotate_90_cw) + getString(R.string.suffix_puertos)'),
    ('createActionButton("- Reducir")', 'createActionButton(getString(R.string.btn_reduce))'),
    ('createActionButton("+ Aumentar")', 'createActionButton(getString(R.string.btn_increase))'),
    ('createActionButton("- 1%")', 'createActionButton(getString(R.string.btn_reduce_1_pct))'),
    ('createActionButton("+ 1%")', 'createActionButton(getString(R.string.btn_increase_1_pct))'),
    ('createActionButton("- 1")', 'createActionButton(getString(R.string.btn_reduce_1))'),
    ('createActionButton("+ 1")', 'createActionButton(getString(R.string.btn_increase_1))'),
    ('createActionButton("- 50 m/h")', 'createActionButton(getString(R.string.btn_reduce_50))'),
    ('createActionButton("+ 50 m/h")', 'createActionButton(getString(R.string.btn_increase_50))'),
    ('createSectionLabel("WORLD TOUR PRO FEATURES")', 'createSectionLabel(getString(R.string.label_pro_features))'),
    ('text = "${getString(R.string.title_settings)} • v0.7.0 ELITE"', 
     'val versionName = packageManager.getPackageInfo(packageName, 0).versionName\n            text = "${getString(R.string.title_settings)} • v$versionName ELITE"')
]
replace_in_file('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/MainActivity.kt', main_rep)

# ClimbPacingView.kt
climb_rep = [
    ('canvas.drawText("RITMO VAM (OBJ: ${targetVam} m/h)", padX, h * 0.16f, titlePaint)',
     'val titleText = context.getString(R.string.label_target_vam_format, targetVam)\n        canvas.drawText(titleText, padX, h * 0.16f, titlePaint)'),
    ('canvas.drawText("VELOCIDAD OBJETIVO", padX, h * 0.62f, speedLabelPaint)',
     'canvas.drawText(context.getString(R.string.label_target_speed), padX, h * 0.62f, speedLabelPaint)'),
    ('"EN RITMO"', 'context.getString(R.string.label_pacing_on_pace)'),
    ('"SOBREESFUERZO"', 'context.getString(R.string.label_pacing_overpacing)'),
    ('"POR DEBAJO"', 'context.getString(R.string.label_pacing_underpacing)')
]
replace_in_file('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/ClimbPacingView.kt', climb_rep)

# GradientTrendView.kt
grad_rep = [
    ('canvas.drawText("TENDENCIA PENDIENTE 3D", padX, h * 0.16f, titlePaint)',
     'canvas.drawText(context.getString(R.string.label_gradient_trend_3d), padX, h * 0.16f, titlePaint)')
]
replace_in_file('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/GradientTrendView.kt', grad_rep)

# Altimetria3DView.kt
alt3d_rep = [
    ('canvas.drawText("Pendiente Media", legendX, legendY - 10f, titlePaint)',
     'canvas.drawText(context.getString(R.string.label_avg_slope), legendX, legendY - 10f, titlePaint)')
]
replace_in_file('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/Altimetria3DView.kt', alt3d_rep)

# RouteBarView.kt
route_rep = [
    ('canvas.drawText("% distr.", panelLeft + panelW / 2f, top + 16f, histPaint)',
     'canvas.drawText(context.getString(R.string.label_dist_format, "%"), panelLeft + panelW / 2f, top + 16f, histPaint)')
]
replace_in_file('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/RouteBarView.kt', route_rep)

print('Kotlin files patched')
