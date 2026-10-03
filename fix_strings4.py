import os

strings_en = {
    'label_tramos': 'blocks',
    'theme_classic_alt': 'Classic',
    'theme_crucible_alt': 'Crucible'
}

strings_es = {
    'label_tramos': 'tramos',
    'theme_classic_alt': 'Clásico',
    'theme_crucible_alt': 'Crisol'
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
        content = content[:idx] + '\n'.join(new_elements) + '\n' + content[idx:]
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)

inject('c:/ALTGRAPH/app/src/main/res/values/strings.xml', strings_en)
inject('c:/ALTGRAPH/app/src/main/res/values-es/strings.xml', strings_es)
