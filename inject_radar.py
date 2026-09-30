import re

with open('c:/ALTGRAPH/docs/index.html', 'r', encoding='utf-8') as f:
    content = f.read()

radar_html = """
    <!-- RADAR ALTIMETRICO SECTION -->
    <section class="flagship-section" id="radar" style="margin-top: -40px;">
        <div class="flagship-card" style="border-color: #eab308;">
            <span class="flagship-badge" style="background-color: #eab308; color: #000;">🚀 NUEVO EN v1.0.3</span>
            <h3>🛰️ Radar Altimétrico (Vista 3D)</h3>
            <p class="lead">El nuevo modo <strong>Radar Altimétrico</strong> se superpone a tu mapa de Karoo permitiendo ver tanto la navegación pura como el relieve de lo que tienes por delante, todo integrado de manera exquisita en la misma pantalla.</p>

            <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_radar_altimetrico.png" alt="Radar Altimetrico" style="max-width: 100%; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); margin: 20px 0 40px 0; border: 1px solid rgba(255,255,255,0.1);" />

            <div class="flagship-features-grid">
                <div class="feature-item">
                    <h4 style="color: #eab308;">📡 Relieve sobre el Mapa</h4>
                    <p>Permite seguir tu ruta GPX mientras observas el perfil 3D del terreno venidero en una cinta superpuesta.</p>
                </div>
                <div class="feature-item">
                    <h4 style="color: #eab308;">🌈 Barra de Zonas Integrada</h4>
                    <p>Integra una barra inferior dinámica (7 colores) basada en tus vatios de potencia o pulsaciones en tiempo real.</p>
                </div>
                <div class="feature-item">
                    <h4 style="color: #eab308;">🎯 Indicador de Pendiente</h4>
                    <p>Muestra flechas de colores (Roja para ascenso, Azul para descenso) para anticipar rápidamente la pendiente máxima.</p>
                </div>
            </div>
        </div>
    </section>
"""

content = content.replace('<section class="features" id="climb-viewer"', radar_html + '\n    <section class="features" id="climb-viewer"')

with open('c:/ALTGRAPH/docs/index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Injected Radar Altimetrico section into index.html')
