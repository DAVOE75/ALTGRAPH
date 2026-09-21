import os

langs = {
    'fr': {
        'title': '<!-- Pro Features -->',
        'setting_smart_zoom': '<string name="setting_smart_zoom">Activer Smart Zoom Tactique (Auto-Scale)</string>',
        'setting_oasis_tracking': '<string name="setting_oasis_tracking">Activer Oasis Tracking (Zones de Récupération)</string>',
        'setting_virtual_pacer': '<string name="setting_virtual_pacer">Activer Virtual Pacer Biomécanique (Fantôme)</string>',
        'setting_switchback_rendering': '<string name="setting_switchback_rendering">Activer Dynamique de Lacets 3D</string>',
        'setting_energy_management': '<string name="setting_energy_management">Activer Energy Management System (Batterie Humaine)</string>'
    },
    'it': {
        'title': '<!-- Pro Features -->',
        'setting_smart_zoom': '<string name="setting_smart_zoom">Abilita Smart Zoom Tattico (Auto-Scale)</string>',
        'setting_oasis_tracking': '<string name="setting_oasis_tracking">Abilita Oasis Tracking (Zone di Recupero)</string>',
        'setting_virtual_pacer': '<string name="setting_virtual_pacer">Abilita Virtual Pacer Biomeccanico (Fantasma)</string>',
        'setting_switchback_rendering': '<string name="setting_switchback_rendering">Abilita Dinamiche Tornanti 3D</string>',
        'setting_energy_management': '<string name="setting_energy_management">Abilita Energy Management System (Batteria Umana)</string>'
    },
    'de': {
        'title': '<!-- Pro Features -->',
        'setting_smart_zoom': '<string name="setting_smart_zoom">Taktischen Smart Zoom aktivieren (Auto-Scale)</string>',
        'setting_oasis_tracking': '<string name="setting_oasis_tracking">Oasis Tracking aktivieren (Erholungszonen)</string>',
        'setting_virtual_pacer': '<string name="setting_virtual_pacer">Biomechanischen Virtual Pacer aktivieren (Geist)</string>',
        'setting_switchback_rendering': '<string name="setting_switchback_rendering">3D-Kehren-Dynamik aktivieren</string>',
        'setting_energy_management': '<string name="setting_energy_management">Energy Management System aktivieren (Menschliche Batterie)</string>'
    }
}

for lang, strings in langs.items():
    file_path = f'c:/ALTGRAPH/app/src/main/res/values-{lang}/strings.xml'
    if os.path.exists(file_path):
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        if 'setting_smart_zoom' not in content:
            injection = f"\n    {strings['title']}\n    {strings['setting_smart_zoom']}\n    {strings['setting_oasis_tracking']}\n    {strings['setting_virtual_pacer']}\n    {strings['setting_switchback_rendering']}\n    {strings['setting_energy_management']}\n\n</resources>"
            content = content.replace('</resources>', injection)
            
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Updated {lang}")

