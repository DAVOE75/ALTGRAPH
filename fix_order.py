import io

with io.open('README.md', 'r', encoding='utf-8') as f:
    text = f.read()

v45 = """## 🔥 VERSIÓN 1.0.45: PERFIL TV 2D (ELITE) 🔥

- **Perfil TV 2D (Novedad ELITE)**: Se ha añadido un nuevo campo de datos que replica los gráficos altimétricos estilo retransmisión ciclista (Giro/Tour). Dibuja la silueta de toda la etapa y rellena dinámicamente con un degradado en verde brillante la zona pedaleada, mostrando un punto iluminado en la posición exacta.
- **Rotación Apaisada**: Incluye un ajuste exclusivo en el menú Elite para girar el gráfico 90º, ideal para colocar el campo de datos en vertical pero disfrutar del gráfico en su máxima anchura visual.

<p align="center"><img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_tv_profile.png" width="400" alt="TV Profile" /></p>

"""

# Remove v45 block from wherever it is
text = text.replace(v45, "")

# Find the V44 block and insert V45 before it
# We also fix the broken </p tag before V44!
v44_start = "## 🚀 VERSIÓN 1.0.44: PACING DINÁMICO Y DIFUMINADO 🚀"

if v44_start not in text:
    # Just in case the rockets are different or missing
    # Let's search for "VERSIÓN 1.0.44"
    import re
    match = re.search(r"## .*VERSIÓN 1\.0\.44.*", text)
    if match:
        v44_start = match.group(0)

# Fix broken </p if it exists
text = text.replace("</p\n\n" + v44_start, "</p>\n\n" + v44_start)

# Now insert v45
text = text.replace(v44_start, v45 + v44_start)

with io.open('README.md', 'w', encoding='utf-8') as f:
    f.write(text)
