import re

def update_docs():
    files = ['USER_MANUAL.md', 'README.md']
    
    changelog = """
---
## 🆕 What's new in v0.4.1 / Novedades v0.4.1

### 🇪🇸 ESPAÑOL
- **Precisión Algorítmica Dinámica**: La Pendiente Media y la Rampa Máxima ahora se calculan analizando el relieve metro a metro en alta resolución (ignorando el suavizado de los bloques visuales) y se acotan estrictamente a la distancia que tengas en pantalla (200m, 5km, 100km...).
- **Cálculo de Pendiente Media Real**: La media del puerto ahora descarta automáticamente las bajadas y llaneos, haciendo el promedio matemático de esfuerzo puramente de las secciones de subida.
- **Grado de Fatiga (GF) y VAM Desligados del Zoom**: El motor científico de APM ya no sufre de "dilución de pendiente" al alejar el zoom. Evalúa la dureza real sobre los puntos brutos de la telemetría, ofreciendo resultados perfectos independientemente de tu configuración de escala.
- **Iconos de Cumbre (Map-Pin)**: Las antiguas balizas circulares se han sustituido por un icono profesional de chincheta topográfica blanca con el núcleo rojo para marcar los puertos oficiales.

### 🇬🇧 ENGLISH
- **Dynamic Algorithmic Precision**: Average Slope and Max Ramp are now calculated point-by-point in high resolution (ignoring visual block smoothing) and are strictly bounded to your current viewport distance (200m, 5km, 100km...).
- **True Average Grade Calculation**: The climb's average grade now automatically discards descents and flats, mathematically averaging the effort only on pure ascending sections.
- **Zoom-Independent Fatigue Grade (GF) & VAM**: The scientific APM engine no longer suffers from "slope dilution" when zooming out. It evaluates true hardness on raw telemetry points, providing perfect results regardless of your scale setting.
- **Map-Pin Summit Icons**: The old circular beacons have been replaced by a professional white topographic map-pin icon with a red core to mark official climbs.

### 🇫🇷 FRANÇAIS
- **Précision Algorithmique Dynamique** : La pente moyenne et la rampe maximale sont désormais calculées point par point en haute résolution (ignorant le lissage visuel) et sont strictement limitées à la distance affichée à l'écran (200m, 5km, 100km...).
- **Calcul de la Pente Moyenne Réelle** : La moyenne de l'ascension écarte automatiquement les descentes et les plats, calculant mathématiquement l'effort uniquement sur les sections montantes pures.
- **Indice de Fatigue (GF) et VAM Indépendants du Zoom** : Le moteur scientifique APM ne souffre plus de "dilution de la pente" lors du dézoom. Il évalue la véritable dureté sur les points de télémétrie bruts, offrant des résultats parfaits quelle que soit votre échelle.
- **Icônes de Sommet (Map-Pin)** : Les anciennes balises circulaires ont été remplacées par une icône professionnelle d'épingle topographique blanche avec un cœur rouge pour marquer les cols officiels.

### 🇮🇹 ITALIANO
- **Precisione Algoritmica Dinamica**: La pendenza media e la rampa massima sono ora calcolate punto per punto in alta risoluzione (ignorando l'arrotondamento visivo) e sono strettamente limitate alla distanza dello schermo (200m, 5km, 100km...).
- **Calcolo della Pendenza Media Reale**: La media della salita ora scarta automaticamente le discese e le parti in piano, calcolando matematicamente lo sforzo puramente delle sezioni di salita.
- **Grado di Fatica (GF) e VAM Indipendenti dallo Zoom**: Il motore scientifico APM non soffre più della "diluizione della pendenza" quando si rimpicciolisce. Valuta la vera durezza sui punti grezzi della telemetria, offrendo risultati perfetti indipendentemente dall'impostazione della scala.
- **Icone Vetta (Map-Pin)**: I vecchi fari circolari sono stati sostituiti da un'icona professionale a forma di spillo topografico bianco con nucleo rosso per segnare le salite ufficiali.

### 🇩🇪 DEUTSCH
- **Dynamische algorithmische Präzision**: Durchschnittliche Steigung und maximale Rampe werden nun Punkt für Punkt in hoher Auflösung berechnet (visuelle Glättung wird ignoriert) und sind streng an die aktuelle Bildschirmdistanz gebunden (200m, 5km, 100km...).
- **Echte Durchschnittssteigung**: Die Durchschnittssteigung des Anstiegs verwirft nun automatisch Abfahrten und flache Stücke und berechnet die mathematische Anstrengung nur auf reinen Steigungsabschnitten.
- **Zoom-unabhängiger Ermüdungsgrad (GF) & VAM**: Die wissenschaftliche APM-Engine leidet nicht mehr unter "Steigungsverdünnung" beim Herauszoomen. Sie bewertet die wahre Härte anhand der rohen Telemetriedaten und liefert unabhängig von der Skalierung perfekte Ergebnisse.
- **Karten-Pin Gipfelsymbole**: Die alten kreisförmigen Leuchtfeuer wurden durch ein professionelles weißes topografisches Karten-Pin-Symbol mit rotem Kern ersetzt, um offizielle Anstiege zu markieren.
"""

    for file in files:
        with open(file, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Replace v0.4.0 with v0.4.1
        content = content.replace('v0.4.0', 'v0.4.1')
        
        # Append changelog if not already there
        if "Novedades v0.4.1" not in content:
            content += "\n" + changelog
            
        with open(file, 'w', encoding='utf-8') as f:
            f.write(content)

update_docs()
print("Docs updated.")
