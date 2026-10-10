import io

with io.open('README.md', 'r', encoding='utf-8') as f:
    text = f.read()

replacement = """</p>

## 🔥 VERSIÓN 1.0.45: PERFIL TV 2D (ELITE) 🔥

- **Perfil TV 2D (Novedad ELITE)**: Se ha añadido un nuevo campo de datos que replica los gráficos altimétricos estilo retransmisión ciclista (Giro/Tour). Dibuja la silueta de toda la etapa y rellena dinámicamente con un degradado en verde brillante la zona pedaleada, mostrando un punto iluminado en la posición exacta.
- **Rotación Apaisada**: Incluye un ajuste exclusivo en el menú Elite para girar el gráfico 90º, ideal para colocar el campo de datos en vertical pero disfrutar del gráfico en su máxima anchura visual.

<p align="center"><img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_tv_profile.png" width="400" alt="TV Profile" /></p>

## """

# Replace the malformed </p missing > or the normal </p> before the previous version
text = text.replace("</p\n\n## ", replacement)
text = text.replace("</p>\n\n## ", replacement)

with io.open('README.md', 'w', encoding='utf-8') as f:
    f.write(text)
