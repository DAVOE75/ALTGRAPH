import io
import os

strings = {
    'values': """    <string name="data_type_profile_tv_2d_desc">2D elevation profile chart like cycling broadcasts</string>
    <string name="pref_elite_profile_tv_2d_desc">Shows the full stage profile with completed area colored</string>
    <string name="pref_elite_profile_tv_2d_rotate_title">Rotate TV Profile 90º</string>
    <string name="pref_elite_profile_tv_2d_rotate_desc">Draw the 2D profile horizontally (landscape orientation)</string>
    <string name="data_type_profile_tv_2d_title">TV Profile 2D</string>
    <string name="pref_elite_profile_tv_2d_title">TV Profile 2D</string>
</resources>""",
    'values-es': """    <string name="data_type_profile_tv_2d_desc">Gráfico de perfil 2D estilo retransmisión ciclista</string>
    <string name="pref_elite_profile_tv_2d_desc">Muestra el perfil completo de la etapa rellenando la zona completada</string>
    <string name="pref_elite_profile_tv_2d_rotate_title">Girar Perfil TV 90º</string>
    <string name="pref_elite_profile_tv_2d_rotate_desc">Dibuja el gráfico 2D en horizontal (apaisado)</string>
    <string name="data_type_profile_tv_2d_title">Perfil TV 2D</string>
    <string name="pref_elite_profile_tv_2d_title">Perfil TV 2D</string>
</resources>""",
    'values-fr': """    <string name="data_type_profile_tv_2d_desc">Graphique d\\'élévation 2D style diffusion cycliste</string>
    <string name="pref_elite_profile_tv_2d_desc">Affiche le profil complet de l\\'étape avec la zone terminée en couleur</string>
    <string name="pref_elite_profile_tv_2d_rotate_title">Tourner Profil TV 90º</string>
    <string name="pref_elite_profile_tv_2d_rotate_desc">Dessine le graphique en mode paysage (horizontal)</string>
    <string name="data_type_profile_tv_2d_title">Profil TV 2D</string>
    <string name="pref_elite_profile_tv_2d_title">Profil TV 2D</string>
</resources>""",
    'values-it': """    <string name="data_type_profile_tv_2d_desc">Grafico altimetrico 2D stile trasmissione ciclistica</string>
    <string name="pref_elite_profile_tv_2d_desc">Mostra il profilo completo della tappa con l\\'area completata a colori</string>
    <string name="pref_elite_profile_tv_2d_rotate_title">Ruotare Profilo TV 90º</string>
    <string name="pref_elite_profile_tv_2d_rotate_desc">Disegna il grafico 2D in orizzontale (modalità paesaggio)</string>
    <string name="data_type_profile_tv_2d_title">Profilo TV 2D</string>
    <string name="pref_elite_profile_tv_2d_title">Profilo TV 2D</string>
</resources>""",
    'values-de': """    <string name="data_type_profile_tv_2d_desc">2D-Höhenprofil-Diagramm im Stil von Radsportübertragungen</string>
    <string name="pref_elite_profile_tv_2d_desc">Zeigt das gesamte Etappenprofil, wobei der gefahrene Bereich farbig ist</string>
    <string name="pref_elite_profile_tv_2d_rotate_title">TV-Profil um 90º drehen</string>
    <string name="pref_elite_profile_tv_2d_rotate_desc">Zeichnet das 2D-Profil horizontal (Querformat)</string>
    <string name="data_type_profile_tv_2d_title">TV-Profil 2D</string>
    <string name="pref_elite_profile_tv_2d_title">TV-Profil 2D</string>
</resources>""",
    'values-pt': """    <string name="data_type_profile_tv_2d_desc">Gráfico de perfil 2D estilo transmissão de ciclismo</string>
    <string name="pref_elite_profile_tv_2d_desc">Mostra o perfil completo da etapa com a área concluída em cores</string>
    <string name="pref_elite_profile_tv_2d_rotate_title">Girar Perfil TV 90º</string>
    <string name="pref_elite_profile_tv_2d_rotate_desc">Desenha o gráfico 2D na horizontal (modo paisagem)</string>
    <string name="data_type_profile_tv_2d_title">Perfil TV 2D</string>
    <string name="pref_elite_profile_tv_2d_title">Perfil TV 2D</string>
</resources>"""
}

for folder, addition in strings.items():
    filepath = os.path.join('app', 'src', 'main', 'res', folder, 'strings.xml')
    if os.path.exists(filepath):
        with io.open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        content = content.replace("</resources>", addition)
        with io.open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)

# README.md
with io.open('README.md', 'r', encoding='utf-8') as f:
    text = f.read()

replacement = """</p>

## 🔥 VERSIÓN 1.0.45: PERFIL TV 2D (ELITE) 🔥

- **Perfil TV 2D (Novedad ELITE)**: Se ha añadido un nuevo campo de datos que replica los gráficos altimétricos estilo retransmisión ciclista (Giro/Tour). Dibuja la silueta de toda la etapa y rellena dinámicamente con un degradado en verde brillante la zona pedaleada, mostrando un punto iluminado en la posición exacta.
- **Rotación Apaisada**: Incluye un ajuste exclusivo en el menú Elite para girar el gráfico 90º, ideal para colocar el campo de datos en vertical pero disfrutar del gráfico en su máxima anchura visual.

<p align="center"><img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_tv_profile.png" width="400" alt="TV Profile" /></p>

## """

if "## 🔥 VERSIÓN 1.0.45: PERFIL TV 2D (ELITE) 🔥" not in text:
    text = text.replace("</p>\n\n## ", replacement, 1)
    
text = text.replace("v1.0.43 ELITE", "v1.0.45 ELITE")
text = text.replace("v1.0.44 ELITE", "v1.0.45 ELITE")

with io.open('README.md', 'w', encoding='utf-8') as f:
    f.write(text)
