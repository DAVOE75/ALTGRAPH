import re

filepath = 'c:/ALTGRAPH/docs/index.html'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

html_target = """        <div class="flagship-card" style="border-color: #eab308;">
            <span class="flagship-badge" style="background-color: #eab308; color: #000;">🌟 NUEVO EN v1.0.5</span>
            <h3>🛰️ Radar Altimétrico (Vista 3D)</h3>
            <p class="lead">El nuevo modo <strong>Radar Altimétrico</strong> se superpone a tu mapa de Karoo permitiendo ver tanto la navegación pura como el relieve de lo que tienes por delante, todo integrado de manera exquisita en la misma pantalla.</p>"""

html_replace = """        <div class="flagship-card" style="border-color: #eab308;">
            <span class="flagship-badge" id="radar-badge" style="background-color: #eab308; color: #000;">🌟 NUEVO EN v1.0.5</span>
            <h3 id="radar-title">🛰️ Radar Altimétrico (Vista 3D)</h3>
            <p class="lead" id="radar-desc">El nuevo modo <strong>Radar Altimétrico</strong> se superpone a tu mapa de Karoo permitiendo ver tanto la navegación pura como el relieve de lo que tienes por delante, todo integrado de manera exquisita en la misma pantalla.</p>"""

content = content.replace(html_target, html_replace)

# Now inject JS assignments
js_target = """            if (document.getElementById('c5-bullets')) document.getElementById('c5-bullets').innerHTML = t.c5Bullets;"""
js_replace = """            if (document.getElementById('c5-bullets')) document.getElementById('c5-bullets').innerHTML = t.c5Bullets;

            if (document.getElementById('radar-badge')) document.getElementById('radar-badge').innerHTML = t.radarBadge;
            if (document.getElementById('radar-title')) document.getElementById('radar-title').innerHTML = t.radarTitle;
            if (document.getElementById('radar-desc')) document.getElementById('radar-desc').innerHTML = t.radarDesc;"""
content = content.replace(js_target, js_replace)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
