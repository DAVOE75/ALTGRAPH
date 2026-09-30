import re

es_text = """## 🌟 VERSIÓN 1.0.5: ESCALA DINÁMICA Y AVISOS OPCIONALES 🌟

- **NUEVA FUNCIÓN ELITE - Avisos Dinámicos Opcionales**: Ahora puedes activar o desactivar los avisos dinámicos de ("Muro", "Próximo Puerto", "Metros para Coronar") desde la pestaña ELITE en la configuración de la app. Al desactivarlos, el radar altimétrico 3D se expandirá automáticamente para ocupar todo el espacio disponible, ¡ofreciendo un área de visión mucho mayor!
- **Ajuste de Escala Dinámica en Campos Múltiples**: Hemos reprogramado la lógica visual de la escala kilométrica inferior. Los textos (0m, 50m, 1km...) ahora calculan el espacio de forma inteligente. Esto arregla un problema visual donde los números se cortaban al usar el radar en pantallas con múltiples campos de datos divididos.
- **Calibración Genuina de GPS**: El progreso del radar de Altimetría ahora está matemáticamente anclado al avance real del GPS de tu Karoo (y no únicamente a la línea de ruta teórica), solucionando problemas de desfase en rutas muy largas.
- **Lógica de renderizado**: Solucionado un solapamiento gráfico que ocurría al ocultar la barra de avisos dinámicos en vistas apaisadas.

"""

en_text = """## 🌟 VERSION 1.0.5: DYNAMIC SCALE & OPTIONAL ALERTS 🌟

- **NEW ELITE FEATURE - Optional Dynamic Alerts**: You can now toggle the popup alerts ("Steep Wall", "Upcoming Climb", "Meters to Summit") on/off from the ELITE tab in the app's settings. If you disable them, the 3D altimetry radar will dynamically expand to fill the entire data field, providing a much larger map area!
- **Dynamic Scaling in Multi-Field Layouts**: We've rewritten the rendering logic for the bottom distance ruler. The text marks (0m, 50m, 1km...) now resize dynamically based on the exact available screen estate. This solves a visual bug where numbers were cut in half when inserting the radar into tighter data page layouts.
- **Genuine GPS Calibration**: The progress of the Altimetry radar is now mathematically locked to the real GPS polling from your Karoo device (and not solely bounded to raw route polyline calculations). This prevents elevation progression mismatch after riding long distances.
- **Rendering logic**: Fixed a graphical overlapping bug that occurred when hiding the dynamic alerts bar on horizontal views.

"""

def insert_text(filepath, search_str, insert_str):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    if search_str in content:
        content = content.replace(search_str, insert_str + search_str)
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)

# Spanish
insert_text('c:/ALTGRAPH/README.md', '## 🐛 VERSIÓN 1.0.4', es_text)
insert_text('c:/ALTGRAPH/USER_MANUAL.md', '## 🐛 VERSIÓN 1.0.4', es_text)

# English
insert_text('c:/ALTGRAPH/README_en.md', '## 🐛 VERSION 1.0.4', en_text)
insert_text('c:/ALTGRAPH/README_fr.md', '## 🐛 VERSION 1.0.4', en_text)
insert_text('c:/ALTGRAPH/README_it.md', '## 🐛 VERSIONE 1.0.4', en_text)
insert_text('c:/ALTGRAPH/README_de.md', '## 🐛 VERSION 1.0.4', en_text)
insert_text('c:/ALTGRAPH/README_pt.md', '## 🐛 VERSÃO 1.0.4', en_text)
