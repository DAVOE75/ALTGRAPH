import os

files = ['USER_MANUAL.md', 'README.md', 'README_fr.md', 'README_it.md', 'README_de.md', 'README_en.md']

changelog = """
---
## 🚀 What's new in v0.6.0 (World Tour Pro Features) / Novedades v0.6.0

### 🇪🇸 ESPAÑOL
- **Smart Zoom Táctico (Auto-Escala)**: El zoom 3D se ajusta automáticamente según la pendiente instantánea, cerrándose en muros y abriéndose en llanos panorámicos.
- **Oasis Tracking**: El sistema calcula y proyecta un holograma en el cielo indicando la distancia exacta hasta el próximo "Crisol de Fuego" (rampa > 8%), permitiendo gestionar el esfuerzo y recuperación.
- **Virtual Pacer**: Añadido el cálculo físico por VAM que proyecta una orbe fantasma Cyan que avanza por el perfil de la altimetría.
- **Dinámica de Herraduras 3D (Switchbacks)**: La geometría de la carretera hace un zig-zag lateral tridimensional al llegar a una curva de herradura real.
- **Human Battery (Energy Management)**: HUD táctico que evalúa tu quema por VAM, baja tus reservas al subir fuerte y las recarga en llano.

### 🇬🇧 ENGLISH
- **Tactical Smart Zoom (Auto-Scale)**: The 3D zoom automatically adjusts based on the instant slope, zooming in on walls and opening up on panoramic flats.
- **Oasis Tracking**: The system calculates and projects a hologram in the sky indicating the exact distance to the next "Crucible" (ramp > 8%), allowing you to manage effort and recovery.
- **Virtual Pacer**: Physical VAM calculation added that projects a Cyan ghost orb advancing along the altimetry profile.
- **3D Switchback Dynamics**: The road geometry makes a 3D lateral zigzag when approaching a real hairpin curve.
- **Human Battery (Energy Management)**: Tactical HUD that evaluates your VAM burn, lowers your reserves when pushing hard, and recharges them on flats.

### 🇫🇷 FRANÇAIS
- **Smart Zoom Tactique (Auto-Scale)**: Le zoom 3D s'ajuste automatiquement en fonction de la pente instantanée.
- **Oasis Tracking**: Le système calcule et projette un hologramme dans le ciel indiquant la distance exacte jusqu'au prochain "Creuset" (rampa > 8%).
- **Virtual Pacer**: Ajout du calcul physique par VAM qui projette un orbe fantôme Cyan avançant sur le profil.
- **Dynamique de Lacets 3D (Switchbacks)**: La géométrie de la route effectue un zigzag latéral 3D à l'approche d'un véritable lacet.
- **Human Battery (Energy Management)**: HUD tactique qui évalue votre dépense VAM, réduit vos réserves lors d'efforts intenses et les recharge sur le plat.

### 🇮🇹 ITALIANO
- **Smart Zoom Tattico (Auto-Scale)**: Lo zoom 3D si adatta automaticamente in base alla pendenza istantanea.
- **Oasis Tracking**: Il sistema calcola e proietta un ologramma nel cielo indicando la distanza esatta fino al prossimo "Crogiolo" (rampa > 8%).
- **Virtual Pacer**: Aggiunto il calcolo fisico tramite VAM che proietta una sfera fantasma Cyan che avanza lungo il profilo.
- **Dinamiche Tornanti 3D (Switchbacks)**: La geometria della strada compie uno zig-zag laterale 3D quando ci si avvicina a un vero tornante.
- **Human Battery (Energy Management)**: HUD tattico che valuta il tuo dispendio VAM, riduce le tue riserve durante gli sforzi intensi e le ricarica in pianura.

### 🇩🇪 DEUTSCH
- **Taktischer Smart Zoom (Auto-Scale)**: Der 3D-Zoom passt sich automatisch der aktuellen Steigung an.
- **Oasis Tracking**: Das System berechnet und projiziert ein Hologramm in den Himmel, das die genaue Entfernung zur nächsten "Bewährungsprobe" (Rampe > 8%) anzeigt.
- **Virtual Pacer**: Physische VAM-Berechnung hinzugefügt, die eine Cyan Geisterkugel projiziert, die entlang des Höhenprofils voranschreitet.
- **3D-Kehren-Dynamik (Switchbacks)**: Die Straßengeometrie macht einen lateralen 3D-Zickzack, wenn man sich einer echten Haarnadelkurve nähert.
- **Human Battery (Energy Management)**: Taktisches HUD, das deinen VAM-Verbrauch bewertet, deine Reserven bei harter Anstrengung senkt und sie in der Ebene wieder auflädt.
"""

for file in files:
    if os.path.exists(file):
        with open(file, 'a', encoding='utf-8') as f:
            f.write(changelog)
        print(f"Appended changelog to {file}")
    else:
        print(f"File {file} not found")

