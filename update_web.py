import re

html_path = 'docs/index.html'
try:
    with open(html_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Replace versions
    content = content.replace('v0.4.1', 'v0.5.0')
    content = content.replace('v0.4.0', 'v0.5.0')

    # Add HTML feature card if not already added
    if 'f8-title' not in content:
        html_feature = '''                  <div class="feature-item">
                      <h4 id="f7-title"></h4>
                      <p id="f7-desc"></p>
                  </div>
                  <div class="feature-item">
                      <h4 id="f8-title"></h4>
                      <p id="f8-desc"></p>
                  </div>'''
        content = re.sub(
            r'<div class="feature-item">\s*<h4 id="f7-title">.*?</div>',
            html_feature,
            content,
            flags=re.DOTALL
        )

        # Add translations
        features_data = {
            'es': ("🚴 Historial en Modo Libre", "Gráfica 3D que fluye dibujando el relieve real que acabas de subir en lugar de proyectar rampas ciegas.", "f7Desc: \\"Cálculo topográfico estricto a escala rodante de GF y % Media aislando solo las pendientes ascendentes. Nuevas cotas de montaña clásicas con pin de mapa.\\","),
            'en': ("🚴 Free Ride Real History", "3D graph flows drawing the real terrain you just climbed instead of projecting blind ramps.", "f7Desc: \\"Strict topographic rolling calculation of GF and Average % isolating pure ascents. New classic map-pin mountain summit beacons.\\","),
            'fr': ("🚴 Historique en Mode Libre", "Le graphique 3D dessine le relief reel que vous venez de gravir au lieu de projeter des rampes aveugles.", "f7Desc: \\"Calcul topographique strict de GF et % Moyen isolant les ascensions pures. Nouvelles icones de sommet de montagne en forme d'epingle de carte classique.\\","),
            'it': ("🚴 Cronologia in Modalità Libera", "Il grafico 3D disegna il rilievo reale appena affrontato invece di proiettare rampe cieche.", "f7Desc: \\"Calcolo topografico rigoroso del GF e % Media isolando le salite pure. Nuove icone classiche a spillo per le vette di montagna.\\","),
            'de': ("🚴 Freeride-Modus Historie", "Das 3D-Diagramm zeichnet das reale Terrain, das Sie gerade erklommen haben.", "f7Desc: \\"Strikte topografische Berechnung von GF und Durchschn.-% durch Isolierung reiner Anstiege. Neue klassische Karten-Pin-Symbole für Berggipfel.\\",")
        }

        for lang, (title, desc, anchor) in features_data.items():
            insert_str = f'\n                f8Title: "{title}",\n                f8Desc: "{desc}",'
            content = content.replace(anchor, anchor + insert_str)

        # Add JS assignment
        js_assignment = '''            document.getElementById('f7-title').innerText = t.f7Title;
            document.getElementById('f7-desc').innerHTML = t.f7Desc;
            document.getElementById('f8-title').innerText = t.f8Title;
            document.getElementById('f8-desc').innerHTML = t.f8Desc;'''
        content = content.replace(
            "document.getElementById('f7-title').innerText = t.f7Title;\n            document.getElementById('f7-desc').innerHTML = t.f7Desc;",
            js_assignment
        )

    with open(html_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Updated HTML successfully")
except Exception as e:
    print(f"Error: {e}")
