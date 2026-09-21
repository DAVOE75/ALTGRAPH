import re

html_path = 'docs/index.html'

with open(html_path, 'r', encoding='utf-8') as f:
    content = f.read()

# I want to add World Tour Pro Features to the features grid
feature_html = '''
                <div class="feature-item" style="border: 1px solid var(--accent-blue);">
                    <h4 id="f7-title">🚀 World Tour Pro Features (v0.6.0)</h4>
                    <p id="f7-desc"><strong>Smart Zoom</strong>, <strong>Oasis Tracking</strong> (recuperación), <strong>Virtual Pacer</strong> biométrico (VAM), deformación 3D de <strong>Herraduras</strong> y <strong>Energy Management</strong> HUD (batería humana).</p>
                </div>'''

# Search for the end of flagship-features-grid which is before </div> \n        </div> \n    </section>
if 'World Tour Pro Features' not in content:
    content = content.replace(
        '                <div class="feature-item">\n                    <h4 id="f6-title">📐 Método Topográfico Exacto</h4>\n                    <p id="f6-desc">Descarta falsos llanos y bajadas para entregarte la pendiente media real matemática y el Índice Grado de Fatiga (GF) puro.</p>\n                </div>',
        '                <div class="feature-item">\n                    <h4 id="f6-title">📐 Método Topográfico Exacto</h4>\n                    <p id="f6-desc">Descarta falsos llanos y bajadas para entregarte la pendiente media real matemática y el Índice Grado de Fatiga (GF) puro.</p>\n                </div>' + feature_html
    )
    
    with open(html_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Updated index.html")
else:
    print("Already updated")

