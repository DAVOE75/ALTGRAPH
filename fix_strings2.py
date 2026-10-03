import os

strings_en = {
    'style_classic_title': 'Classic 3D (Default)',
    'style_classic_desc': 'Professional profile with 3D bevel, smooth gradient 0-15% and sharp metrics',
    'style_horizon_title': 'Isometric Horizon',
    'style_horizon_desc': 'Cockpit perspective projected towards horizon with rider light beam',
    'style_oasis_title': 'Tactical Oases & Crucibles',
    'style_oasis_desc': 'Highlights recovery flats in ice blue and steep walls in thermal fire',
    'style_force_title': 'Force Field & Fatigue',
    'style_force_desc': 'Biometric inertia relief and dynamic modulation of accumulated fatigue',
    'style_monolith_title': 'Obsidian & Plasma Monolith',
    'style_monolith_desc': 'Faceted dark crystal with luminous plasma core and laser ridge',
    'style_global_title': 'Global Isometric Map',
    'style_global_desc': 'Architectural 3D vision of the entire route in snake form',
    'toast_vista': 'View: %1$s',
    'toast_visor_puertos': 'Climb Viewer: %1$s',
    'label_elite_header': '👑 ALTGRAPH ELITE 👑',
    'label_elite_features': '🎯 Radar ELITE Features',
    'label_ghost': '👻 Ghost — Previous best time\\n(Recorded at end of route)',
    'label_energy_bar': '🔋 Remaining energy bar',
    'label_poi_icons': '📍 POI Icons on ruler',
    'label_histogram': '📊 Gradient Histogram',
    'label_radar_3d': '🏔️ 3D Radar with isometric perspective',
    'label_radar_theme': 'Radar Theme:',
    'theme_standard': 'Standard',
    'theme_oasis': 'Oasis',
    'theme_crucibles': 'Crucibles',
    'theme_mist': 'Mist',
    'theme_forcefield': 'Force Field',
}

strings_es = {
    'style_classic_title': 'Clásica 3D (Por defecto)',
    'style_classic_desc': 'Perfil profesional con bisel 3D, degradado suave del 0 al 15% y cotas nítidas',
    'style_horizon_title': 'Horizonte Isométrico',
    'style_horizon_desc': 'Perspectiva de cabina proyectada hacia el horizonte con haz luminoso del ciclista',
    'style_oasis_title': 'Oasis y Crisoles Tácticos',
    'style_oasis_desc': 'Destaca descansillos de recuperación en azul hielo y muros duros en fuego térmico',
    'style_force_title': 'Campo de Fuerza y Fatiga',
    'style_force_desc': 'Relieve biométrico de inercia y modulación dinámica de dureza acumulada',
    'style_monolith_title': 'Monolito Obsidiana y Plasma',
    'style_monolith_desc': 'Cristal oscuro facetado con núcleo de plasma luminoso y cresta láser',
    'style_global_title': 'Mapa Isométrico Global',
    'style_global_desc': 'Visión arquitectónica 3D de todo el recorrido en forma de serpiente',
    'toast_vista': 'Vista: %1$s',
    'toast_visor_puertos': 'Visor Puertos: %1$s',
    'label_elite_header': '👑 ALTGRAPH ELITE 👑',
    'label_elite_features': '🎯 Radar ELITE Features',
    'label_ghost': '👻 Ghost — Mejor marca anterior\\n(Grabado al terminar la ruta)',
    'label_energy_bar': '🔋 Barra de energía restante',
    'label_poi_icons': '📍 Iconos POI en la regla',
    'label_histogram': '📊 Histograma de pendientes',
    'label_radar_3d': '🏔️ Radar 3D con perspectiva isométrica',
    'label_radar_theme': 'Tema del Radar:',
    'theme_standard': 'Estándar',
    'theme_oasis': 'Oasis',
    'theme_crucibles': 'Crisoles',
    'theme_mist': 'Bruma',
    'theme_forcefield': 'Campo de Fuerza',
}

def inject(path, d):
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    new_elements = []
    for k, v in d.items():
        if f'name="{k}"' not in content:
            val = v.replace('&', '&amp;').replace('<', '&lt;')
            new_elements.append(f'    <string name="{k}">{val}</string>')
    if new_elements:
        idx = content.rfind('</resources>')
        content = content[:idx] + '\\n'.join(new_elements) + '\\n' + content[idx:]
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)

inject('c:/ALTGRAPH/app/src/main/res/values/strings.xml', strings_en)
inject('c:/ALTGRAPH/app/src/main/res/values-es/strings.xml', strings_es)
