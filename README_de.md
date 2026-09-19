# ALTGRAPH (v0.4.1 STABLE)

*Aufrufen in: [Español](README.md) | [English](README_en.md) | [Français](README_fr.md) | [Italiano](README_it.md) | [Deutsch](README_de.md)*

ALTGRAPH ist eine hochmoderne, professionelle Höhenmessungs- und Leistungserweiterung für Hammerhead Karoo Fahrradcomputer (Karoo 2 und Karoo 3), entwickelt von David García Pascual mit dem offiziellen karoo-ext SDK.

Es revolutioniert das traditionelle Konzept der Fahrrad-Höhenmessung durch die Integration einer Suite von 5 Höhen-Visualisierungsmodellen mit einem dynamischen Live-Selektor, einer kontinuierlichen 15-stufigen monochromen Skala, einem gleitenden 50-Meter-Quantenfenster, adaptiver Multiskalen-Auflösung, Bergpass-Analyse, optionaler topografischer Berechnung (reine horizontale Projektion), mathematischer Erkennung von Haarnadelkurven (Tornanti), Filterung von POI-Kategorien (Dörfer, Wasser, Aussichtspunkte, Gipfel), Touchscreen-Zoomskalenschalter, Google Sans / Condensed-Typografie, Vollbild-90°-gedrehtem Querformat, dualer VAM-Geschwindigkeit mit empfohlener Geschwindigkeit und wissenschaftlicher Berechnung des Ermüdungsgrades (GF).

## 📸 Echte Karoo 3 Screenshots
- 🏔️ 3D-Altimetrie und Reiseroute
- 🎨 Stil-Tab und Selektor
- 🏔️ 3D-Tab
- 📊 2D-Tab
- 🚴 VAM-Tab

## 🌐 Offizielles Webportal & Mehrsprachiger Support (5 Sprachen)
Besuchen Sie das [Offizielle ALTGRAPH Webportal](https://davoe75.github.io/ALTGRAPH/) für die interaktive Dokumentation auf Spanisch, Englisch, Französisch, Italienisch und Deutsch.

## 🚀 Neuigkeiten & Highlights (v0.4.1 Stable)

### 🌟 Suite von 5 Revolutionären Altimetriemodellen (Fliegender Wechsel)
Ermöglicht es dem Radfahrer, über die obere Karte des 🎨 Stil-Tabs zwischen 5 visuellen Möglichkeiten zur Interpretation des Berges zu wählen:
- **🏔️ Klassisches 3D (Standard)**: Professionelles Profil mit dezentem 3D-Fase, sanftem 0-15% monochromatischem Verlauf, gedrehten Höhenmarkierungen und dunklen, kontrastreichen Kapseln mit gestochen scharfer Typografie für die Prozentsätze.
- **🌅 Isometrischer Horizont**: Cockpit-3D-Perspektive, die in die Tiefe in Richtung Horizont projiziert wird, gestrichelte Mittellinie im Stil einer Startbahn und dynamischer Frontlichtstrahl, projiziert durch die Bake des Radfahrers, der sich entsprechend der physischen Neigung der bevorstehenden Rampe neigt.
- **❄️ Taktische Oasen und Tiegel**: Intelligente Erkennung von Erholungsflachstücken (≤3% und ≥30m), die eisblau mit dynamischen ❄️ OASIS-Badges leuchten, Feuer-Tiegel für kritische Rampen (≥12%) mit taktischen 🔥 MAUER-Badges und atmosphärischer Hypoxie-Nebel auf Höhen ≥1400m.
- **⚡ Kraftfeld & Ermüdung**: Relief mit Gravitations-Spannungslinien, die pixelgenau an der Steigung verankert sind, sinusförmige kinetische Welle an der Basis (Cyan beim Vorrücken durch Trägheit, schnelles Karmesinrot, wenn die Steigung die Trittfrequenz senkt) und VAM-Führungslinie, die sanft über dem Profil schwebt.
- **💎 Obsidian & Plasma Monolith**: Berg aus dunklem, facettiertem Obsidian-Kristall (#151D2C bis #04070D), polierte 3D-Kristall-Oberfläche, strahlender innerer Plasma-Kern und oberer weißer Laserstrahl-Kamm mit hellblauem Neonleuchten.

### 🎨 Intensive, Hochsichtbare Gradientenskala
* **≤ -10.0%**: Dunkles Marineblau `#041E42` (Steile Abfahrt).
* **-10.0% bis -5.0%**: Dunkelblau `#004B87` (Mittlere Abfahrt).
* **-5.0% bis -2.0%**: Mittelblau `#0072CE` (Sanfte Abfahrt).
* **-2.0% bis < 0.0%**: Helles Himmelblau `#41B6E6` (Falsche Ebene bergab).
* **0.0% bis 3.0%**: Intensives Waldgrün `#388E3C`.
* **3.0% bis 5.0%**: Kräftiges Gelb `#FBC02D`.
* **5.0% bis 8.0%**: Intensives Orange `#F57C00`.
* **8.0% bis 10.0%**: Dunkelorange / Rost `#E65100`.
* **10.0% bis 13.0%**: Intensives Rot `#D32F2F`.
* **13.0% bis 17.0%**: Dunkles Purpurrot `#B71C1C`.
* **> 17.0%**: Tiefschwarz `#000000`.

### 🔄 Quanten-Gleitfenster von 50m und Physischer Fortschritt
- Die Bake des Radfahrers klettert physisch von 0 bis 50m auf der oberen Kontur der Steigung.
- Nach Abschluss jedes Vielfachen von 50 Metern verschiebt sich das Fenster sauber um 50m nach links, ohne abrupte Skalensprünge.

### 📏 Adaptive Multiskalen-Auflösung
- **≤ 500m**: 50m Unterblöcke innerhalb von 100m Blöcken.
- **500m bis 5km**: 100m Blöcke (500m Hauptblöcke).
- **5km bis 20km**: 500m Blöcke (2km Hauptblöcke).
- **20km bis 50km**: 1km Blöcke (5km Hauptblöcke).
- **> 100km**: 10km Abschnitte (20km Hauptblöcke).

### ⚡ Zero-Allocation-Architektur für Karoo 3
- Speicherzuweisungsfreier Rendering-Canvas: Keine Pausen des Garbage Collectors (GC) im Hintergrund.
- Echtzeit-Wiederverwendung von Bitmap- und Canvas-Puffern.

### 📊 Dreifache Kopfzeile & Telemetrie
- Live-Anzeige von AKTUELLE STEIGUNG, DURCHSCHNITTLICHE STEIGUNG des sichtbaren Abschnitts und MAXIMALE STEIGUNG.
- Vektorielle Erkennung von Haarnadelkurven (Tornanti), Filterung von POI-Meilensteinen (Dörfer, Wasser, Aussichtspunkte, Gipfel), exakte topografische Methode, VAM-Zieltempo und Index für den Ermüdungsgrad (GF).

## 📖 Benutzerhandbuch
Im [Benutzerhandbuch (USER_MANUAL.md)](https://github.com/DAVOE75/ALTGRAPH/blob/main/USER_MANUAL.md) finden Sie die vollständige Konfigurations- und Installationsanleitung.

## 🛠️ Anforderungen & Installation
**Entwicklungsanforderungen**
- Android Studio: Ladybug / 2024.2.1 oder neuer.
- JDK: Java 17 oder neuer.
- Android SDK: compileSdk = 34, minSdk = 26.
- Gerät: Hammerhead Karoo 2 oder Karoo 3 mit aktiviertem ADB.

**📦 Kompilierung**
```bash
# Projekt kompilieren
./gradlew assembleDebug

# Auf Karoo installieren (über USB verbunden)
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 👨‍💻 Entwickler
Mit Leidenschaft entwickelt von David García Pascual.

## 📄 Lizenz
Dieses Projekt wird unter der MIT-Lizenz vertrieben.
