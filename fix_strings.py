import re

strings = {
    'tab_style': ('🎨 Style', '🎨 Estilo'),
    'tab_3d': ('🏔️ 3D', '🏔️ 3D'),
    'tab_2d': ('📊 2D', '📊 2D'),
    'tab_vam': ('🚴 VAM', '🚴 VAM'),
    'tab_pro': ('🚀 Pro', '🚀 Pro'),
    'tab_elite': ('👑 Elite', '👑 Elite'),
    'label_vista_global': ('ALTIMETRY VIEW AND MODEL (GLOBAL ROUTE)', 'VISTA Y MODELO DE ALTIMETRÍA (RUTA GLOBAL)'),
    'label_vista_puertos': ('ALTIMETRY VIEW AND MODEL (CLIMB VIEWER)', 'VISTA Y MODELO DE ALTIMETRÍA (VISOR DE PUERTOS)'),
    'suffix_global': (' (GLOBAL ROUTE)', ' (RUTA GLOBAL)'),
    'suffix_puertos': (' (CLIMB VIEWER)', ' (VISOR PUERTOS)'),
    'btn_reduce': ('- Reduce', '- Reducir'),
    'btn_increase': ('+ Increase', '+ Aumentar'),
    'label_pro_features': ('WORLD TOUR PRO FEATURES', 'WORLD TOUR PRO FEATURES'),
    'label_gradient_trend_3d': ('3D GRADIENT TREND', 'TENDENCIA PENDIENTE 3D'),
    'label_target_vam_format': ('VAM PACING (TARGET: %1$d m/h)', 'RITMO VAM (OBJ: %1$d m/h)'),
    'label_target_speed': ('TARGET SPEED', 'VELOCIDAD OBJETIVO'),
    'label_pacing_on_pace': ('ON PACE', 'EN RITMO'),
    'label_pacing_overpacing': ('OVERPACING', 'SOBREESFUERZO'),
    'label_pacing_underpacing': ('UNDERPACING', 'POR DEBAJO'),
    'label_avg_slope': ('Avg Gradient', 'Pendiente Media'),
    'label_dist_format': ('%1$s distr.', '%1$s distr.'),
    'btn_reduce_1_pct': ('- 1%', '- 1%'),
    'btn_increase_1_pct': ('+ 1%', '+ 1%'),
    'btn_reduce_1': ('- 1', '- 1'),
    'btn_increase_1': ('+ 1', '+ 1'),
    'btn_reduce_50': ('- 50 m/h', '- 50 m/h'),
    'btn_increase_50': ('+ 50 m/h', '+ 50 m/h'),
}

def inject_strings(file_path, lang_idx):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_elements = []
    for key, vals in strings.items():
        if f'name="{key}"' not in content:
            val = vals[lang_idx].replace('&', '&amp;').replace('<', '&lt;')
            new_elements.append(f'    <string name="{key}">{val}</string>')
    
    if new_elements:
        insert_idx = content.rfind('</resources>')
        content = content[:insert_idx] + '\n'.join(new_elements) + '\n' + content[insert_idx:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)

inject_strings('c:/ALTGRAPH/app/src/main/res/values/strings.xml', 0)
inject_strings('c:/ALTGRAPH/app/src/main/res/values-es/strings.xml', 1)
print('Strings injected')
