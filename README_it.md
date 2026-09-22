<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>
# ALTGRAPH (v0.5.0 STABLE)
<p>Leggi in: <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README.md">🇪🇸 Español</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_en.md">🇬🇧 English</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_fr.md">🇫🇷 Français</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_it.md">🇮🇹 Italiano</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_de.md">🇩🇪 Deutsch</a></p>
**ALTGRAPH** è un'estensione professionale per l'altimetria e le prestazioni di ultima generazione per i ciclocomputer **Hammerhead Karoo** (Karoo 2 e Karoo 3), sviluppata da **David García Pascual** utilizzando l'SDK ufficiale `karoo-ext`.
Rivoluziona il concetto tradizionale di altimetria ciclistica integrando una **Suite di 6 Modelli di Visualizzazione Altimetrica** con selettore dinamico in tempo reale, risoluzione adattiva multiscala, calcolo topografico, rilevamento matematico dei tornanti, filtraggio POI, layout a schermo intero ruotato a 90°, passo **VAM** e calcolo scientifico del **Grado di Fatica (GF)**.
---

## 📸 Capturas en Pantalla Real de Karoo 3

| 🎨 Scheda Stile | 🎛️ Menu Selezione | 📊 Dashboard Completo |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="🎨 Scheda Stile" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_data_selection.png" width="220" alt="🎛️ Menu Selezione" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_dashboard_completo.png" width="220" alt="📊 Dashboard Completo" /> |

| 🏔️ Altimetria 3D (200m) | 🏔️ Altimetria 3D (1km) | 🏔️ Altimetria 3D (10km) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="🏔️ Altimetria 3D (200m)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_1km.png" width="220" alt="🏔️ Altimetria 3D (1km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_10km.png" width="220" alt="🏔️ Altimetria 3D (10km)" /> |

| 🏔️ Altimetria 3D (20km) | 🏔️ Altimetria 3D (200km) | 🏔️ Visualizzatore - 3ª Cat |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_20km.png" width="220" alt="🏔️ Altimetria 3D (20km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_200km.png" width="220" alt="🏔️ Altimetria 3D (200km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer.png" width="220" alt="🏔️ Visualizzatore - 3ª Cat" /> |

| 🏔️ Visualizzatore - HC | 🏔️ Visualizzatore (Isometrico) | 🗺️ GPS Isometrico (Globale) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_especial.png" width="220" alt="🏔️ Visualizzatore - HC" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_iso.png" width="220" alt="🏔️ Visualizzatore (Isometrico)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_gps.png" width="220" alt="🗺️ GPS Isometrico (Globale)" /> |

## 🆚 ALTGRAPH vs Climber Nativo di Karoo
ALTGRAPH non sostituisce il Climber nativo, ma lo integra offrendo una **vista grafica completa** e personalizzabile. Le differenze principali sono:
- **Risoluzione Adattiva e Rendering Continuo**: Il profilo avanza fisicamente in finestre scorrevoli di 50m. Le barre non "saltano" staticamente, ma scorrono verso di te al ritmo della tua pedalata.
- **Modelli di Visualizzazione**: Il Climber nativo ha una vista fissa; ALTGRAPH offre 5 modelli rivoluzionari.
- **Scale Touch Dinamiche**: Cambia istantaneamente lo zoom del percorso (200m, 1km, 10km, 20km, 50km, ecc.) toccando semplicemente la lente d'ingrandimento.
- **Grado di Fatica (GF)**: L'unico strumento su Karoo che calcola la durezza scientifica di una salita basandosi sul coefficiente APM.
---
## ⚙️ Caratteristiche e Funzioni nel Dettaglio
### 📈 Campi Dati Esclusivi (Dashboard Tattica)
- **🧠 Stratega dell'Altimetria**: Mostra un blocco visivo intelligente che riassume i prossimi chilometri a colori.
- **⚡ Grado di Fatica (GF)**: Motore scientifico basato sul coefficiente APM che valuta la reale durezza della salita in diretta.
- **🚀 Ritmo VAM Obiettivo**: Calcola la pendenza esatta sotto le tue ruote e ti dice a quale **velocità (km/h)** devi andare per raggiungere il tuo obiettivo in vetta.
- **📐 Tendenza 3D e Rampa Max**: Ti avvisa se la pendenza sta diventando più dura o più dolce prima che le tue gambe lo sentano.
### 🗺️ Navigazione e Topografia Avanzata
- **Icone Topografiche Ufficiali**: Identificazione di Vette, Passi (Map-Pin), Paesi e Fontane.
- **Rilevamento Matematico delle Curve**: L'algoritmo rileva i tornanti stretti e ti avvisa graficamente.
- **Temi Scuri ad Alto Contrasto**: Interfaccia progettata per una leggibilità istantanea sotto la luce solare diretta.
---
## 📲 Installazione su Karoo 2 e Karoo 3
Basata sull'SDK ufficiale `karoo-ext`, l'installazione **non richiede permessi di root** o modifiche pericolose del sistema. È sicura al 100%.
**Tramite Sideloading (Consigliato per Karoo 3)**
1. Scarica l'ultimo file `.apk` dalla sezione [Releases](https://github.com/DAVOE75/ALTGRAPH/releases).
2. Invialo al tuo Karoo usando l'app ufficiale **Hammerhead Companion**.
3. Una volta installata, l'estensione si aggiornerà automaticamente leggendo il `manifest.json`.
---
## 🔮 Roadmap e Prossimi Sviluppi (Modalità Free-Ride)
Il prossimo grande traguardo nello sviluppo di ALTGRAPH è la **Generazione Altimetrica in Tempo Reale (Modalità Free-Ride)**. 
Attualmente, l'estensione richiede il caricamento di un percorso (GPX) per ottenere i profili. Non appena Hammerhead sbloccherà l'accesso ai dati di altimetria imminente nel suo SDK, **ALTGRAPH genererà il grafico 3D e le metriche tattiche al volo**, senza bisogno di un tracciato caricato. Potrai esplorare liberamente e la montagna si disegnerà davanti a te in tempo reale.
---
## 📄 Licenza e Disclaimer
Questo progetto open source è distribuito sotto licenza **MIT** - Copyright 2026 David García Pascual.
*Disclaimer: Questa estensione non è affiliata, approvata, sponsorizzata o supportata da Hammerhead o SRAM.*
---
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
---
## 👑 ALTGRAPH ELITE 👑 (v0.7.0)
- **Strava Live Segments 3D (Beta):** Simulazione dal vivo (KOM Ghost) su salite molto dure.
- **Wind & Weather Overlay:** Integrazione nativa (Open-Meteo) per rappresentare la direzione del vento dal vivo utilizzando vettori 3D.
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