<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>
# ALTGRAPH (v0.5.0 STABLE)
<p>Lesen auf: <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README.md">🇪🇸 Español</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_en.md">🇬🇧 English</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_fr.md">🇫🇷 Français</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_it.md">🇮🇹 Italiano</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_de.md">🇩🇪 Deutsch</a></p>
**ALTGRAPH** ist eine professionelle Höhen- und Leistungserweiterung der nächsten Generation für **Hammerhead Karoo** Fahrradcomputer (Karoo 2 und Karoo 3), entwickelt von **David García Pascual** mit dem offiziellen `karoo-ext` SDK.
Es revolutioniert das traditionelle Konzept der Radsport-Höhenmessung durch die Integration einer **Suite von 6 Höhenvisualisierungsmodellen** mit dynamischer Live-Auswahl, einer 15-stufigen kontinuierlichen monochromatischen Skala, einem gleitenden 50-Meter-Fenster, adaptiver Multiskalen-Auflösung, topografischer Berechnung, mathematischer Erkennung von Haarnadelkurven (Tornanti), einem dynamischen Zoom, **VAM**-Tempo und der wissenschaftlichen Berechnung des **Ermüdungsgrades (GF)**.
---



## 🐛 VERSION 1.0.2: KRITISCHE KORREKTUREN 🐛

- **Bugfix für Rundkurse/Hin- und Rückwege**: Ein Problem wurde behoben, bei dem das GPS am Start fälschlicherweise an das Ende der Route sprang, wodurch der Graph rückwärts lief ("snap-to-end bug").
- **UI-Verbesserung (Radar)**: Die Anzeige der maximalen Steigung zeigt nun einen farbigen Pfeil (Rot für Anstieg, Dunkelblau für Abstieg) über dem Prozentwert, was Sichtbarkeit und Design erheblich verbessert.

## 🚀 NEUIGKEITEN IN VERSION 1.0.0: ALTIMETRIE-RADAR & PRO-ERLEBNIS 🚀

Wir haben die Version **1.0.0** erreicht! Dieses Update bringt ein komplettes Redesign der Erfahrung, wodurch Steuerung und Visualisierung präziser und professioneller denn je werden.

### 🌟 Altimetrie-Radar (Neuer 3D-Ansichtsmodus)
Der neue Modus **Altimetrie-Radar** legt sich über Ihre Karoo-Karte, sodass Sie sowohl die reine Navigation als auch das kommende Geländerelief sehen können, alles wunderbar auf demselben Bildschirm integriert.
<p align="center">
  <img src="art/screenshot_radar_altimetrico.png" width="400" />
</p>

### 🔧 Neue Funktionen und Verbesserungen:
- **Trainingszonen-Leiste**: Vollständige Integration der unteren farbigen Leiste (7 Zonen) basierend auf Ihren Powermeter- oder Herzfrequenzdaten im Modus Altimetrie-Radar.
- **Feine Zoom-Steuerung (+ / -)**: Die alte Lupe wurde durch dedizierte **[+]** und **[-]** Tasten ersetzt. Sie können nun auf bis zu **50-Meter-Blöcke** hineinzoomen, wobei der "Smart Zoom"-Algorithmus überschrieben wird, wenn Sie die absolute manuelle Kontrolle wünschen.
- **Horizontales Scrollen (< / >)**: Neue interaktive Schaltflächen in der Profilansicht, mit denen Sie das Diagramm frei nach links und rechts ziehen können, ohne den Maßstab zu verändern. Nehmen Sie den Anstieg vorweg!
- **Nativer Querformatmodus (Landscape)**: Volle Unterstützung für horizontal montierte Karoos. Jetzt richten sich Touch-Buttons und das Profil perfekt nach Ihrem Sichtfeld aus und platzieren die Touch-Steuerung ergonomisch an den Bildschirmrändern.
- **Echtzeit-Radfahrer-Symbol**: Die Kugel (aktuelle Position des Radfahrers) bewegt sich nun flüssig über das grafische Profil und die Höhenfarben, was Ihnen ein sofortiges Feedback darüber gibt, wo Sie sich auf der Route befinden.
- **Dynamischer Kompass**: Intelligentes Ausblenden des Kompasses in Ansichten außerhalb der globalen Isometriekarte, um die Benutzeroberfläche übersichtlicher zu gestalten.

## 📸 Capturas en Pantalla Real de Karoo 3

| 🎨 Stil-Tab | 🎛️ Auswahlmenü | 📊 Vollständiges Dashboard |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="🎨 Stil-Tab" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_data_selection.png" width="220" alt="🎛️ Auswahlmenü" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_dashboard_completo.png" width="220" alt="📊 Vollständiges Dashboard" /> |

| 🏔️ 3D-Höhenprofil (200m) | 🏔️ 3D-Höhenprofil (1km) | 🏔️ 3D-Höhenprofil (10km) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="🏔️ 3D-Höhenprofil (200m)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_1km.png" width="220" alt="🏔️ 3D-Höhenprofil (1km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_10km.png" width="220" alt="🏔️ 3D-Höhenprofil (10km)" /> |

| 🏔️ 3D-Höhenprofil (20km) | 🏔️ 3D-Höhenprofil (200km) | 🏔️ Anstiegsbetrachter - 3. Kat |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_20km.png" width="220" alt="🏔️ 3D-Höhenprofil (20km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_200km.png" width="220" alt="🏔️ 3D-Höhenprofil (200km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer.png" width="220" alt="🏔️ Anstiegsbetrachter - 3. Kat" /> |

| 🏔️ Anstiegsbetrachter - HC | 🏔️ Anstiegsbetrachter (Isometrisch) <div align="center">

| 🗺️ GPS Isometrisch (Global) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_especial.png" width="220" alt="🏔️ Anstiegsbetrachter - HC" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_iso.png" width="220" alt="🏔️ Anstiegsbetrachter (Isometrisch)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_gps.png" width="220" alt="🗺️ GPS Isometrisch (Global)" /> |

</div>

## 🆚 ALTGRAPH vs Nativer Karoo Climber
ALTGRAPH ersetzt den nativen Climber nicht, sondern ergänzt ihn durch eine **umfassende grafische Ansicht**. Die Hauptunterschiede sind:
- **Adaptive Auflösung & kontinuierliches Rendering**: Das Profil bewegt sich physisch in 50-m-Fenstern vorwärts. Die Balken "springen" nicht statisch, sondern fließen im Rhythmus Ihrer Trittfrequenz.
- **Visualisierungsmodelle**: Der native Climber hat eine feste Ansicht; ALTGRAPH bietet 5 revolutionäre Modelle.
- **Dynamischer Touch-Zoom**: Ändern Sie den Strecken-Zoom (200m, 1km, 10km, 20km, 50km usw.) sofort durch Antippen der Lupe.
- **Ermüdungsgrad (GF)**: Das einzige Tool auf dem Karoo, das die wissenschaftliche Härte eines Anstiegs basierend auf dem APM-Koeffizienten berechnet.

## ⚙️ Merkmale und Funktionen im Detail
### 📈 Exklusive Datenfelder (Taktisches Dashboard)
- **🧠 Altimetrie-Stratege**: Zeigt einen intelligenten visuellen Block, der die kommenden Kilometer zusammenfasst.
- **⚡ Ermüdungsgrad (GF)**: Wissenschaftliche Engine basierend auf dem APM-Koeffizienten, die die wahre Härte des Anstiegs live bewertet.
- **🚀 Ziel-VAM-Tempo**: Berechnet die genaue Steigung unter Ihren Rädern und diktiert Ihnen die genaue **Geschwindigkeit (km/h)**, mit der Sie fahren müssen, um Ihr Ziel auf dem Gipfel zu erreichen.
- **📐 3D-Trend & Max-Rampe**: Warnt Sie, wenn die Steigung härter oder weicher wird, bevor Ihre Beine es spüren.
### 🗺️ Erweiterte Navigation und Topografie
- **Offizielle Topografische Symbole**: Identifizierung von Gipfeln, Gebirgspässen (Map-Pin), Städten und Brunnen.
- **Mathematische Kurvenerkennung**: Der Algorithmus erkennt enge Kurven und warnt Sie grafisch.

## 📲 Installation auf Karoo 2 und Karoo 3
Basierend auf dem offiziellen `karoo-ext` SDK erfordert die Installation **kein Root** oder gefährliche Betriebssystemänderungen.
**Über Sideloading (Empfohlen für Karoo 3)**
1. Laden Sie die neueste `.apk`-Datei aus dem Bereich [Releases](https://github.com/DAVOE75/ALTGRAPH/releases) herunter.
2. Senden Sie sie über die **Hammerhead Companion**-App an Ihren Karoo.
3. Nach der Installation sucht die Erweiterung automatisch nach Updates über die `manifest.json`.

#### 🔮 Roadmap und Zukünftige Entwicklungen (Free-Ride Modus)

Der nächste große Meilenstein in der Entwicklung von ALTGRAPH ist die **Echtzeit-Höhengenerierung (Free-Ride Modus)**. 
Derzeit benötigt die Erweiterung eine geladene Route (GPX), um die Profile abzurufen. Sobald Hammerhead den Zugriff auf unmittelbar bevorstehende Höhendaten im SDK freigibt, wird **ALTGRAPH die 3D-Grafik und alle taktischen Metriken on-the-fly generieren**, ohne dass ein Track geladen werden muss. Sie können frei erkunden und der Berg zeichnet sich in Echtzeit vor Ihnen ab.

#### 📄 Lizenz und Haftungsausschluss

Dieses Open-Source-Projekt wird unter der **MIT**-Lizenz vertrieben - Copyright 2026 David García Pascual.
*Haftungsausschluss: Diese Erweiterung ist nicht mit Hammerhead oder SRAM verbunden, wird von diesen nicht unterstützt oder gesponsert.*
