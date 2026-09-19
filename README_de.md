<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.4.1 STABLE)

*Lesen auf: [Español](README.md) | [English](README_en.md) | [Français](README_fr.md) | [Italiano](README_it.md) | [Deutsch](README_de.md)*

**ALTGRAPH** ist eine professionelle Höhen- und Leistungserweiterung der nächsten Generation für **Hammerhead Karoo** Fahrradcomputer (Karoo 2 und Karoo 3), entwickelt von **David García Pascual** mit dem offiziellen `karoo-ext` SDK.

Es revolutioniert das traditionelle Konzept der Radsport-Höhenmessung durch die Integration einer **Suite von 5 Höhenvisualisierungsmodellen** mit dynamischer Live-Auswahl, einer 15-stufigen kontinuierlichen monochromatischen Skala, einem gleitenden 50-Meter-Fenster, adaptiver Multiskalen-Auflösung, topografischer Berechnung, mathematischer Erkennung von Haarnadelkurven (Tornanti), einem dynamischen Zoom, **VAM**-Tempo und der wissenschaftlichen Berechnung des **Ermüdungsgrades (GF)**.

---

## 🆚 ALTGRAPH vs Nativer Karoo Climber

ALTGRAPH ersetzt den nativen Climber nicht, sondern ergänzt ihn durch eine **umfassende grafische Ansicht**. Die Hauptunterschiede sind:
- **Adaptive Auflösung & kontinuierliches Rendering**: Das Profil bewegt sich physisch in 50-m-Fenstern vorwärts. Die Balken "springen" nicht statisch, sondern fließen im Rhythmus Ihrer Trittfrequenz.
- **Visualisierungsmodelle**: Der native Climber hat eine feste Ansicht; ALTGRAPH bietet 5 revolutionäre Modelle.
- **Dynamischer Touch-Zoom**: Ändern Sie den Strecken-Zoom (200m, 1km, 10km, 20km, 50km usw.) sofort durch Antippen der Lupe.
- **Ermüdungsgrad (GF)**: Das einzige Tool auf dem Karoo, das die wissenschaftliche Härte eines Anstiegs basierend auf dem APM-Koeffizienten berechnet.

---

## ⚙️ Merkmale und Funktionen im Detail

### 📈 Exklusive Datenfelder (Taktisches Dashboard)
- **🧠 Altimetrie-Stratege**: Zeigt einen intelligenten visuellen Block, der die kommenden Kilometer zusammenfasst.
- **⚡ Ermüdungsgrad (GF)**: Wissenschaftliche Engine basierend auf dem APM-Koeffizienten, die die wahre Härte des Anstiegs live bewertet.
- **🚀 Ziel-VAM-Tempo**: Berechnet die genaue Steigung unter Ihren Rädern und diktiert Ihnen die genaue **Geschwindigkeit (km/h)**, mit der Sie fahren müssen, um Ihr Ziel auf dem Gipfel zu erreichen.
- **📐 3D-Trend & Max-Rampe**: Warnt Sie, wenn die Steigung härter oder weicher wird, bevor Ihre Beine es spüren.

### 🗺️ Erweiterte Navigation und Topografie
- **Offizielle Topografische Symbole**: Identifizierung von Gipfeln, Gebirgspässen (Map-Pin), Städten und Brunnen.
- **Mathematische Kurvenerkennung**: Der Algorithmus erkennt enge Kurven und warnt Sie grafisch.

---

## 📲 Installation auf Karoo 2 und Karoo 3

Basierend auf dem offiziellen `karoo-ext` SDK erfordert die Installation **kein Root** oder gefährliche Betriebssystemänderungen.

**Über Sideloading (Empfohlen für Karoo 3)**
1. Laden Sie die neueste `.apk`-Datei aus dem Bereich [Releases](https://github.com/DAVOE75/ALTGRAPH/releases) herunter.
2. Senden Sie sie über die **Hammerhead Companion**-App an Ihren Karoo.
3. Nach der Installation sucht die Erweiterung automatisch nach Updates über die `manifest.json`.

---

## 🔮 Roadmap und Zukünftige Entwicklungen (Free-Ride Modus)

Der nächste große Meilenstein in der Entwicklung von ALTGRAPH ist die **Echtzeit-Höhengenerierung (Free-Ride Modus)**. 

Derzeit benötigt die Erweiterung eine geladene Route (GPX), um die Profile abzurufen. Sobald Hammerhead den Zugriff auf unmittelbar bevorstehende Höhendaten im SDK freigibt, wird **ALTGRAPH die 3D-Grafik und alle taktischen Metriken on-the-fly generieren**, ohne dass ein Track geladen werden muss. Sie können frei erkunden und der Berg zeichnet sich in Echtzeit vor Ihnen ab.

---

## 📄 Lizenz und Haftungsausschluss
Dieses Open-Source-Projekt wird unter der **MIT**-Lizenz vertrieben - Copyright 2026 David García Pascual.
*Haftungsausschluss: Diese Erweiterung ist nicht mit Hammerhead oder SRAM verbunden, wird von diesen nicht unterstützt oder gesponsert.*
