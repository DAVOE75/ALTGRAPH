import os

version = "0.6.0-PRO"

def update_docs():
    languages = ['es', 'en', 'fr', 'it', 'de']
    for lang in languages:
        path = f'c:/ALTGRAPH/docs/manual_{lang}.md'
        content = f'''# AltGraph Pro v{version} Manual ({lang})
## Novedades / What's New
- 🚀 **World Tour Pro Features**:
  - Smart Zoom Táctico (Auto-Scale)
  - Oasis Tracking (Zonas de Recuperación)
  - Virtual Pacer Biomecánico
  - Dinámica de Herraduras 3D
  - Energy Management System (Human Battery)
'''
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)

def update_web():
    path = 'c:/ALTGRAPH/docs/index.html'
    content = f'''<!DOCTYPE html>
<html>
<head><title>AltGraph Pro {version}</title></head>
<body>
<h1>AltGraph Pro - World Tour Features</h1>
<ul>
  <li>Smart Zoom Táctico</li>
  <li>Oasis Tracking</li>
  <li>Virtual Pacer Biomecánico</li>
  <li>Dinámica de Herraduras 3D</li>
  <li>Energy Management System</li>
</ul>
</body>
</html>'''
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

update_docs()
update_web()
print("Docs and Web updated for v0.6.0-PRO")
