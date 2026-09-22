<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>
# ALTGRAPH (v0.5.0 STABLE)
<p>Lire en : <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README.md">🇪🇸 Español</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_en.md">🇬🇧 English</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_fr.md">🇫🇷 Français</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_it.md">🇮🇹 Italiano</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_de.md">🇩🇪 Deutsch</a></p>
**ALTGRAPH** est une extension professionnelle d'altimétrie et de performance de nouvelle génération pour les compteurs GPS **Hammerhead Karoo** (Karoo 2 et Karoo 3), développée par **David García Pascual** à l'aide du SDK officiel `karoo-ext`.
Elle révolutionne le concept traditionnel de l'altimétrie cycliste en intégrant une **Suite de 6 Modèles de Visualisation Altimétrique** avec sélecteur dynamique en direct, une échelle monochromatique continue de 15 niveaux, une avancée quantique en fenêtre glissante de 50 mètres, une résolution adaptative multi-échelle, l'analyse des cols de montagne, le calcul topographique (projection horizontale pure), la détection mathématique des virages en épingle (tornanti), le filtrage par catégories de POI, le commutateur d'échelle tactile, le mode paysage pivoté à 90°, l'allure **VAM** et le calcul scientifique du **Degré de Fatigue (GF)**.
---

## 📸 Capturas en Pantalla Real de Karoo 3

| 🎨 Onglet Style | 🎛️ Menu Sélection | 📊 Tableau de Bord |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="🎨 Onglet Style" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_data_selection.png" width="220" alt="🎛️ Menu Sélection" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_dashboard_completo.png" width="220" alt="📊 Tableau de Bord" /> |

| 🏔️ Altimétrie 3D (200m) | 🏔️ Altimétrie 3D (1km) | 🏔️ Altimétrie 3D (10km) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="🏔️ Altimétrie 3D (200m)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_1km.png" width="220" alt="🏔️ Altimétrie 3D (1km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_10km.png" width="220" alt="🏔️ Altimétrie 3D (10km)" /> |

| 🏔️ Altimétrie 3D (20km) | 🏔️ Altimétrie 3D (200km) | 🏔️ Visionneuse - 3ème Cat |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_20km.png" width="220" alt="🏔️ Altimétrie 3D (20km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_200km.png" width="220" alt="🏔️ Altimétrie 3D (200km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer.png" width="220" alt="🏔️ Visionneuse - 3ème Cat" /> |

| 🏔️ Visionneuse - HC | 🏔️ Visionneuse (Isométrique) | 🗺️ GPS Isométrique (Global) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_especial.png" width="220" alt="🏔️ Visionneuse - HC" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_iso.png" width="220" alt="🏔️ Visionneuse (Isométrique)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_gps.png" width="220" alt="🗺️ GPS Isométrique (Global)" /> |

## 🆚 ALTGRAPH vs Climber Natif du Karoo
ALTGRAPH ne remplace pas le Climber natif, mais le complète en offrant une **vue graphique complète** et personnalisable. Ses principales différences sont :
- **Résolution Adaptative et Rendu Continu** : Le profil avance physiquement par fenêtres glissantes de 50m. Les barres ne "sautent" pas de manière statique, elles glissent vers vous au rythme de votre pédalage.
- **Modèles de Visualisation** : Le Climber natif a une vue fixe ; ALTGRAPH vous offre 5 modèles révolutionnaires.
- **Échelles Tactiles Dynamiques** : Changez instantanément le zoom de l'itinéraire (200m, 1km, 10km, 20km, 50km, etc.) en touchant simplement la loupe.
- **Degré de Fatigue (GF)** : Le seul outil sur Karoo qui calcule la dureté scientifique d'un col en se basant sur le coefficient APM.

## ⚙️ Caractéristiques et Fonctions en Détail
### 📈 Champs de Données Exclusifs (Tableau de Bord Tactique)
- **🧠 Stratège d'Altimétrie** : Affiche un bloc visuel intelligent résumant les prochains kilomètres.
- **⚡ Degré de Fatigue (GF)** : Moteur scientifique basé sur le coefficient APM évaluant la dureté réelle du col en direct.
- **🚀 Allure VAM Cible** : Calcule la pente exacte sous vos roues et vous dicte à quelle **vitesse (km/h)** vous devez rouler pour atteindre votre objectif au sommet.
- **📐 Tendance 3D et Rampe Max** : Vous avertit si la pente se durcit ou s'adoucit avant même que vos jambes ne le sentent.
### 🗺️ Navigation et Topographie Avancée
- **Icônes Topographiques Officielles** : Identification des Sommets, Cols (Map-Pin), Villages et Fontaines.
- **Détection Mathématique des Virages** : L'algorithme détecte les virages serrés et vous avertit graphiquement.
- **Calcul Topographique Pur** : Calculez les distances basées sur la projection horizontale pour une rigueur absolue.
- **Thèmes Sombres à Haut Contraste** : Interface conçue pour une lisibilité instantanée en plein soleil.

## 📲 Installation sur Karoo 2 et Karoo 3
Basée sur le SDK officiel `karoo-ext`, l'installation **ne nécessite pas de root** ni de modifications dangereuses du système.
**Via Sideloading (Recommandé pour Karoo 3)**
1. Téléchargez le dernier fichier `.apk` depuis la section [Releases](https://github.com/DAVOE75/ALTGRAPH/releases).
2. Envoyez-le à votre Karoo via l'application **Hammerhead Companion**.
3. Une fois installée, l'extension se mettra à jour automatiquement via le `manifest.json`.

#### 🔮 Roadmap et Prochaines Avancées (Mode Free-Ride)

La prochaine grande étape du développement d'ALTGRAPH est la **Génération Altimétrique en Temps Réel (Mode Free-Ride)**. 
Actuellement, l'extension nécessite le chargement d'un itinéraire (GPX) pour obtenir les profils. Dès qu'Hammerhead publiera l'accès aux données d'élévation imminentes dans son SDK, **ALTGRAPH générera le graphique 3D et toutes ses métriques tactiques à la volée**, sans avoir besoin d'un itinéraire chargé. Vous pourrez explorer librement et la montagne se dessinera devant vous en temps réel.

#### 📄 Licence et Clause de Non-responsabilité

Ce projet open-source est distribué sous la licence **MIT** - Copyright 2026 David García Pascual.
*Clause de non-responsabilité : Cette extension n'est pas affiliée, approuvée, sponsorisée ou soutenue par Hammerhead ou SRAM.*

## 🆕 What's new in v0.5.0 / Novedades v0.5.0
### 🇪🇸 ESPAÑOL
- **Modo Libre con Historial Real (Free Ride History)**: En el modo libre (sin ruta cargada), la gráfica 3D ya no proyecta una pendiente infinita. Ahora acumula y dibuja con precisión métrica el terreno real que acabas de superar, fluyendo de forma espectacular bajo las ruedas de tu avatar.
- **Escala Visual Dinámica 3D**: La cinta 3D ahora escala perfectamente su altura visual. Una rampa plana (0%) se verá perfectamente horizontal, y un 15% ocupará toda la pantalla, manteniendo siempre la proporción realista independientemente de la longitud de la gráfica.
- **Corrección de Escala en Terreno Llano**: Solucionado el salto falso al 5% en terrenos completamente planos. El 0% ahora es verdaderamente 0%.
### 🇬🇧 ENGLISH
- **Real History Free Ride Mode**: In free ride mode (no route loaded), the 3D graph no longer projects an infinite slope. It now precisely accumulates and draws the real terrain you just conquered, flowing spectacularly under your avatar's wheels.
- **Dynamic 3D Visual Scale**: The 3D ribbon now perfectly scales its visual height. A flat ramp (0%) will look perfectly horizontal, and a 15% slope will fill the screen, always maintaining a realistic proportion regardless of the graph's length.
- **Flat Terrain Scale Fix**: Fixed the false 5% jump on completely flat terrain. 0% is now truly 0%.
### 🇫🇷 FRANÇAIS
- **Mode Libre avec Historique Réel (Free Ride History)** : En mode libre (sans itinéraire chargé), le graphique 3D ne projette plus une pente infinie. Il accumule et dessine désormais avec une précision métrique le terrain réel que vous venez de franchir, s'écoulant de manière spectaculaire sous les roues de votre avatar.
- **Échelle Visuelle 3D Dynamique** : Le ruban 3D adapte désormais parfaitement sa hauteur visuelle. Une rampe plate (0%) paraîtra parfaitement horizontale, et une pente de 15% remplira l'écran, maintenant toujours une proportion réaliste quelle que soit la longueur du graphique.
- **Correction d'Échelle sur Terrain Plat** : Correction du faux saut à 5% sur les terrains complètement plats. 0% est désormais vraiment 0%.
### 🇮🇹 ITALIANO
- **Modalità Libera con Cronologia Reale (Free Ride History)**: In modalità libera (nessun percorso caricato), il grafico 3D non proietta più una pendenza infinita. Ora accumula e disegna con precisione metrica il terreno reale che hai appena superato, scorrendo in modo spettacolare sotto le ruote del tuo avatar.
- **Scala Visiva 3D Dinamica**: Il nastro 3D ora scala perfettamente la sua altezza visiva. Una rampa piatta (0%) sembrerà perfettamente orizzontale e una pendenza del 15% riempirà lo schermo, mantenendo sempre una proporzione realistica indipendentemente dalla lunghezza del grafico.
- **Correzione della Scala su Terreno Pianeggiante**: Risolto il falso salto al 5% su terreni completamente pianeggianti. Lo 0% è ora veramente lo 0%.
### 🇩🇪 DEUTSCH
- **Freeride-Modus mit echtem Verlauf (Free Ride History)**: Im Freeride-Modus (keine Route geladen) projiziert das 3D-Diagramm keine unendliche Steigung mehr. Es sammelt und zeichnet nun mit metrischer Präzision das reale Terrain, das Sie gerade bezwungen haben, und fließt spektakulär unter den Rädern Ihres Avatars.
- **Dynamische visuelle 3D-Skala**: Das 3D-Band skaliert nun seine visuelle Höhe perfekt. Eine flache Rampe (0%) sieht perfekt horizontal aus, und eine 15%ige Steigung füllt den Bildschirm aus, wobei unabhängig von der Länge des Diagramms immer ein realistisches Maßverhältnis beibehalten wird.
- **Korrektur der Skalierung in flachem Gelände**: Der falsche 5%-Sprung in völlig flachem Gelände wurde behoben. 0% ist jetzt wirklich 0%.

## 👑 ALTGRAPH ELITE 👑 (v0.7.0)
- **Strava Live Segments 3D (Beta):** Simulation en direct (KOM Ghost) sur les ascensions très difficiles.
- **Wind & Weather Overlay:** Intégration native (Open-Meteo) pour représenter la direction du vent en direct à l'aide de vecteurs 3D.
## 🚀 What's new in v0.7.0 (World Tour Pro Features) / Novedades v0.7.0
### 🇪🇸 ESPAÑOL
- **Smart Zoom Táctico (Auto-Escala)**: El zoom 3D se ajusta automáticamente según la pendiente instantánea, cerrándose en muros y abriéndose en llanos panorámicos.
- **Oasis Tracking**: El sistema calcula y proyecta un holograma en el cielo indicando la distancia exacta hasta el próximo "Crisol de Fuego" (rampa > 8%), permitiendo gestionar el esfuerzo y recuperación.
- **Virtual Pacer**: Añadido el cálculo físico por VAM que proyecta una orbe fantasma `Cyan` que avanza por el perfil de la altimetría.
- **Dinámica de Herraduras 3D (Switchbacks)**: La geometría de la carretera hace un zig-zag lateral tridimensional al llegar a una curva de herradura real.
- **Human Battery (Energy Management)**: HUD táctico que evalúa tu quema por VAM, baja tus reservas al subir fuerte y las recarga en llano.
### 🇬🇧 ENGLISH
- **Tactical Smart Zoom (Auto-Scale)**: The 3D zoom automatically adjusts based on the instant slope, zooming in on walls and opening up on panoramic flats.
- **Oasis Tracking**: The system calculates and projects a hologram in the sky indicating the exact distance to the next "Crucible" (ramp > 8%), allowing you to manage effort and recovery.
- **Virtual Pacer**: Physical VAM calculation added that projects a `Cyan` ghost orb advancing along the altimetry profile.
- **3D Switchback Dynamics**: The road geometry makes a 3D lateral zigzag when approaching a real hairpin curve.
- **Human Battery (Energy Management)**: Tactical HUD that evaluates your VAM burn, lowers your reserves when pushing hard, and recharges them on flats.
### 🇫🇷 FRANÇAIS
- **Smart Zoom Tactique (Auto-Scale)**: Le zoom 3D s'ajuste automatiquement en fonction de la pente instantanée.
- **Oasis Tracking**: Le système calcule et projette un hologramme dans le ciel indiquant la distance exacte jusqu'au prochain "Creuset" (rampe > 8%).
- **Virtual Pacer**: Ajout du calcul physique par VAM qui projette un orbe fantôme `Cyan` avançant sur le profil.
- **Dynamique de Lacets 3D (Switchbacks)**: La géométrie de la route effectue un zigzag latéral 3D à l'approche d'un véritable lacet.
- **Human Battery (Energy Management)**: HUD tactique qui évalue votre dépense VAM, réduit vos réserves lors d'efforts intenses et les recharge sur le plat.
### 🇮🇹 ITALIANO
- **Smart Zoom Tattico (Auto-Scale)**: Lo zoom 3D si adatta automaticamente in base alla pendenza istantanea.
- **Oasis Tracking**: Il sistema calcola e proietta un ologramma nel cielo indicando la distanza esatta fino al prossimo "Crogiolo" (rampa > 8%).
- **Virtual Pacer**: Aggiunto il calcolo fisico tramite VAM che proietta una sfera fantasma `Cyan` che avanza lungo il profilo.
- **Dinamiche Tornanti 3D (Switchbacks)**: La geometria della strada compie uno zig-zag laterale 3D quando ci si avvicina a un vero tornante.
- **Human Battery (Energy Management)**: HUD tattico che valuta il tuo dispendio VAM, riduce le tue riserve durante gli sforzi intensi e le ricarica in pianura.
### 🇩🇪 DEUTSCH
- **Taktischer Smart Zoom (Auto-Scale)**: Der 3D-Zoom passt sich automatisch der aktuellen Steigung an.
- **Oasis Tracking**: Das System berechnet und projiziert ein Hologramm in den Himmel, das die genaue Entfernung zur nächsten "Bewährungsprobe" (Rampe > 8%) anzeigt.
- **Virtual Pacer**: Physische VAM-Berechnung hinzugefügt, die eine `Cyan` Geisterkugel projiziert, die entlang des Höhenprofils voranschreitet.
- **3D-Kehren-Dynamik (Switchbacks)**: Die Straßengeometrie macht einen lateralen 3D-Zickzack, wenn man sich einer echten Haarnadelkurve nähert.
- **Human Battery (Energy Management)**: Taktisches HUD, das deinen VAM-Verbrauch bewertet, deine Reserven bei harter Anstrengung senkt und sie in der Ebene wieder auflädt.