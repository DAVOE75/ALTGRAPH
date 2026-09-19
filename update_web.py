import re

html_path = 'docs/index.html'
with open(html_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace versions
content = content.replace('v0.4.0', 'v0.4.1')

# Add HTML feature card
html_feature = '''                  <div class="feature-item">
                      <h4 id="f6-title">📐 Método Topográfico Exacto</h4>
                      <p id="f6-desc">Opción para calcular la pendiente % sobre la proyección horizontal del mapa, eliminando el sesgo de la hipotenusa en rampas extremas.</p>
                  </div>
                  <div class="feature-item">
                      <h4 id="f7-title"></h4>
                      <p id="f7-desc"></p>
                  </div>'''
content = re.sub(
    r'<div class="feature-item">\s*<h4 id="f6-title">.*?</div>',
    html_feature,
    content,
    flags=re.DOTALL
)

# Add translations
features_data = {
    'es': ('🎯 Precisión Algorítmica y Cotas', 'Cálculo topográfico estricto a escala rodante de GF y % Media aislando solo las pendientes ascendentes. Nuevas cotas de montaña clásicas con pin de mapa.', 'f6Desc: "Opción para calcular la pendiente % sobre la proyección horizontal del mapa, eliminando el sesgo de la hipotenusa en rampas extremas.",'),
    'en': ('🎯 Algorithmic Precision & Summits', 'Strict topographic rolling calculation of GF and Average % isolating pure ascents. New classic map-pin mountain summit beacons.', 'f6Desc: "Option to calculate slope % over horizontal map projection, eliminating hypotenuse bias on extreme ramps.",'),
    'fr': ('🎯 Précision Algorithmique & Sommets', "Calcul topographique strict de GF et % Moyen isolant les ascensions pures. Nouvelles icônes de sommet de montagne en forme d'épingle de carte classique.", 'f6Desc: "Option pour calculer le % de pente sur la projection horizontale de la carte, éliminant le biais de l\\'hypoténuse sur les rampes extrêmes.",'),
    'it': ('🎯 Precisione Algoritmica e Vette', "Calcolo topografico rigoroso del GF e % Media isolando le salite pure. Nuove icone classiche a spillo per le vette di montagna.", 'f6Desc: "Opzione per calcolare la pendenza % sulla proiezione orizzontale della mappa, eliminando l\\'errore dell\\'ipotenusa su rampe estreme.",'),
    'de': ('🎯 Algorithmische Präzision & Gipfel', 'Strikte topografische Berechnung von GF und Durchschn.-% durch Isolierung reiner Anstiege. Neue klassische Karten-Pin-Symbole für Berggipfel.', 'f6Desc: "Option zur Berechnung der Steigung in % auf der horizontalen Kartenprojektion, um Hypotenusenfehler bei extremen Rampen zu eliminieren.",')
}

for lang, (title, desc, anchor) in features_data.items():
    insert_str = f'\n                f7Title: "{title}",\n                f7Desc: "{desc}",'
    content = content.replace(anchor, anchor + insert_str)

# Add JS assignment
js_assignment = '''            document.getElementById('f6-title').innerText = t.f6Title;
            document.getElementById('f6-desc').innerHTML = t.f6Desc;
            document.getElementById('f7-title').innerText = t.f7Title;
            document.getElementById('f7-desc').innerHTML = t.f7Desc;'''
content = content.replace(
    "document.getElementById('f6-title').innerText = t.f6Title;\n            document.getElementById('f6-desc').innerHTML = t.f6Desc;",
    js_assignment
)

with open(html_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated HTML successfully")
