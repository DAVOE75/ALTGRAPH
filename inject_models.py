import re

with open('c:/ALTGRAPH/docs/index.html', 'r', encoding='utf-8') as f:
    content = f.read()

m3 = """<div class="detailed-card">
                <div class="detailed-card-header">
                    <div class="detailed-card-icon">❄️</div>
                    <div>
                        <h3 id="m3-title">Oasis y Crisoles Tácticos</h3>
                        <span style="color: var(--accent-green); font-size: 0.85rem; font-weight: 700;"
                            id="m3-tag">Tactical Recovery & Fire</span>
                    </div>
                </div>
                <p class="desc" id="m3-desc">Visualización táctica de carrera que resalta zonas de oxigenación y recuperación frente a los muros más severos de la ascensión.</p>
                <ul class="feature-bullets" id="m3-bullets">
                    <li><strong>Oasis de Oxígeno</strong>: Tramos de descanso (≤3%) iluminados en cian glacial con distintivo dinámico ❄️ OASIS [dist]m.</li>
                    <li><strong>Crisoles de Fuego</strong>: Rampas duras (≥12%) coloreadas en lava térmica con insignia de alerta 🔥 MURO [dist]m.</li>
                    <li><strong>Bruma de Hipoxia</strong>: Gradiente atmosférico brumoso en la cumbre a altitudes superiores a 1.400 metros.</li>
                </ul>
            </div>"""

m4 = """<div class="detailed-card">
                <div class="detailed-card-header">
                    <div class="detailed-card-icon">⚡</div>
                    <div>
                        <h3 id="m4-title">Campo de Fuerza y Fatiga</h3>
                        <span style="color: var(--accent-yellow); font-size: 0.85rem; font-weight: 700;"
                            id="m4-tag">Kinetic Gravitational Field</span>
                    </div>
                </div>
                <p class="desc" id="m4-desc">Panel de telemetría dinámica donde la gravedad, la inercia cinética y la cadencia se visualizan en un campo de energía continuo.</p>
                <ul class="feature-bullets" id="m4-bullets">
                    <li><strong>Tensión Gravitatoria</strong>: Líneas de fuerza verticales ancladas milimétricamente al relieve en rampas ≥8%.</li>
                    <li><strong>Onda Cinética de Inercia</strong>: Onda senoidal a lo largo de la base (cian en avance fluido, carmesí rápido cuando la pendiente frena la inercia).</li>
                    <li><strong>Línea Guía VAM Flotante</strong>: Trazo dorado de ritmo objetivo que acompaña la ascensión 12 px sobre el perfil.</li>
                </ul>
            </div>"""

m5 = """<div class="detailed-card">
                <div class="detailed-card-header">
                    <div class="detailed-card-icon">💎</div>
                    <div>
                        <h3 id="m5-title">Monolito de Obsidiana y Plasma</h3>
                        <span style="color: var(--accent-purple); font-size: 0.85rem; font-weight: 700;"
                            id="m5-tag">Monolithic Obsidian & Radiant Plasma</span>
                    </div>
                </div>
                <p class="desc" id="m5-desc">Escultura geométrica de montaña en cristal de obsidiana facetado ahumado con un núcleo de plasma radiante interior y aristas láser neón.</p>
                <ul class="feature-bullets" id="m5-bullets">
                    <li><strong>Cristal de Obsidiana Facetado</strong>: Bloque sólido oscuro de montaña con cortes de diamante a 45° y faceta 3D pulida.</li>
                    <li><strong>Núcleo Radiante de Plasma</strong>: Franja interior incandescente que emite el gradiente térmico de pendiente estrictamente acotada.</li>
                    <li><strong>Haz Láser Neón y Cápsulas Holográficas</strong>: Arista superior con doble línea (resplandor celeste difuso de 6 px + núcleo blanco puro de 2.2 px).</li>
                </ul>
            </div>"""

m6 = """<div class="detailed-card">
                <div class="detailed-card-header">
                    <div class="detailed-card-icon">🗺️</div>
                    <div>
                        <h3 id="m6-title">GPS Isométrico Global</h3>
                        <span style="color: var(--accent-red); font-size: 0.85rem; font-weight: 700;"
                            id="m6-tag">Isometric GPS Map</span>
                    </div>
                </div>
                <p class="desc" id="m6-desc">Navegación espacial interactiva que proyecta la ruta completa como un mapa 3D con colores de altimetría.</p>
                <ul class="feature-bullets" id="m6-bullets">
                    <li><strong>Proyección Topográfica Real</strong>: Escala y ángulos GPS reales del mundo proyectados en una cuadrícula isométrica 3D.</li>
                    <li><strong>Baliza de Navegación 3D</strong>: Indicador dinámico que se mueve y rota a lo largo de la ruta marcando la posición exacta del ciclista.</li>
                    <li><strong>Gradiente Multiescala</strong>: Bloques de color integrados en la estructura 3D que delatan las ascensiones.</li>
                </ul>
            </div>"""

# Regex to replace each card HTML.
content = re.sub(r'<div class="detailed-card">\s*<div class="detailed-card-header">\s*<div class="detailed-card-icon">❄️</div>.*?</ul>\s*</div>', m3, content, flags=re.DOTALL)
content = re.sub(r'<div class="detailed-card">\s*<div class="detailed-card-header">\s*<div class="detailed-card-icon">⚡</div>.*?</ul>\s*</div>', m4, content, flags=re.DOTALL)
content = re.sub(r'<div class="detailed-card">\s*<div class="detailed-card-header">\s*<div class="detailed-card-icon">[^<]+</div>\s*<div>\s*<h3 id="m5-title">.*?</ul>\s*</div>', m5, content, flags=re.DOTALL)
content = re.sub(r'<div class="detailed-card">\s*<div class="detailed-card-header">\s*<div class="detailed-card-icon">[^<]+</div>\s*<div>\s*<h3 id="m6-title">.*?</ul>\s*</div>', m6, content, flags=re.DOTALL)


# Update the ES translations section as well
def replace_trans(key, val):
    global content
    content = re.sub(fr'{key}:\s*".*?",', f'{key}: "{val}",', content)
    content = re.sub(fr'{key}:\s*`.*?`,', f'{key}: `{val}`,', content, flags=re.DOTALL)

replace_trans('m3Title', 'Oasis y Crisoles Tácticos')
replace_trans('m3Desc', 'Visualización táctica de carrera que resalta zonas de oxigenación y recuperación frente a los muros más severos de la ascensión.')
replace_trans('m3Bullets', '<li><strong>Oasis de Oxígeno</strong>: Tramos de descanso (≤3%) iluminados en cian glacial con distintivo dinámico ❄️ OASIS [dist]m.</li><li><strong>Crisoles de Fuego</strong>: Rampas duras (≥12%) coloreadas en lava térmica con insignia de alerta 🔥 MURO [dist]m.</li><li><strong>Bruma de Hipoxia</strong>: Gradiente atmosférico brumoso en la cumbre a altitudes superiores a 1.400 metros.</li>')

replace_trans('m4Title', 'Campo de Fuerza y Fatiga')
replace_trans('m4Desc', 'Panel de telemetría dinámica donde la gravedad, la inercia cinética y la cadencia se visualizan en un campo de energía continuo.')
replace_trans('m4Bullets', '<li><strong>Tensión Gravitatoria</strong>: Líneas de fuerza verticales ancladas milimétricamente al relieve en rampas ≥8%.</li><li><strong>Onda Cinética de Inercia</strong>: Onda senoidal a lo largo de la base (cian en avance fluido, carmesí rápido cuando la pendiente frena la inercia).</li><li><strong>Línea Guía VAM Flotante</strong>: Trazo dorado de ritmo objetivo que acompaña la ascensión 12 px sobre el perfil.</li>')

replace_trans('m5Title', 'Monolito de Obsidiana y Plasma')
replace_trans('m5Desc', 'Escultura geométrica de montaña en cristal de obsidiana facetado ahumado con un núcleo de plasma radiante interior y aristas láser neón.')
replace_trans('m5Bullets', '<li><strong>Cristal de Obsidiana Facetado</strong>: Bloque sólido oscuro de montaña con cortes de diamante a 45° y faceta 3D pulida.</li><li><strong>Núcleo Radiante de Plasma</strong>: Franja interior incandescente que emite el gradiente térmico de pendiente estrictamente acotada.</li><li><strong>Haz Láser Neón y Cápsulas Holográficas</strong>: Arista superior con doble línea (resplandor celeste difuso de 6 px + núcleo blanco puro de 2.2 px).</li>')

replace_trans('m6Title', 'GPS Isométrico Global')
replace_trans('m6Desc', 'Navegación espacial interactiva que proyecta la ruta completa como un mapa 3D con colores de altimetría.')
replace_trans('m6Bullets', '<li><strong>Proyección Topográfica Real</strong>: Escala y ángulos GPS reales del mundo proyectados en una cuadrícula isométrica 3D.</li><li><strong>Baliza de Navegación 3D</strong>: Indicador dinámico que se mueve y rota a lo largo de la ruta marcando la posición exacta del ciclista.</li><li><strong>Gradiente Multiescala</strong>: Bloques de color integrados en la estructura 3D que delatan las ascensiones.</li>')

with open('c:/ALTGRAPH/docs/index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Updated models in index.html')
