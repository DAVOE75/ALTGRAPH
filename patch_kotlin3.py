import os

strings_en = {
    'theme_forcefield_alt': 'Field',
    'label_alerts_radar': '⚠️ Alerts (Walls, Next Climb...)'
}

strings_es = {
    'theme_forcefield_alt': 'Campo',
    'label_alerts_radar': '⚠️ Avisos (Muros, Próx. Puerto...)'
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

def replace_in_file(file_path, replacements):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    for old, new in replacements:
        content = content.replace(old, new)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

main_rep = [
    ('''            val shortName = when (theme) {
                "Estándar" -> "Clásico"
                "Crisoles" -> "Crisol"
                "Campo de Fuerza" -> "Campo"
                else -> theme
            }''',
     '''            val shortName = when (theme) {
                getString(R.string.theme_standard) -> getString(R.string.theme_classic_alt)
                getString(R.string.theme_crucibles) -> getString(R.string.theme_crucible_alt)
                getString(R.string.theme_forcefield) -> getString(R.string.theme_forcefield_alt)
                else -> theme
            }'''),
    ('text = "⚠️ Avisos (Muros, Próx. Puerto...)"', 'text = getString(R.string.label_alerts_radar)'),
]
replace_in_file('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/MainActivity.kt', main_rep)
