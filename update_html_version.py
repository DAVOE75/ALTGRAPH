import re

html_path = 'docs/index.html'

with open(html_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('v0.4.62 STABLE', 'v0.6.0 PRO')
content = content.replace('v0.4.62 Stable Release', 'v0.6.0 Pro Release')
content = content.replace('v0.4.62', 'v0.6.0')
content = content.replace('<title>AltGraph - Altimetría 3D y Relieve</title>', '<title>AltGraph Pro - World Tour Features</title>')

with open(html_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Versions updated in index.html")
