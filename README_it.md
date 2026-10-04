<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>
# ALTGRAPH (v0.5.0 STABLE)
<p>Leggi in: <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README.md">🇪🇸 Español</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_en.md">🇬🇧 English</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_fr.md">🇫🇷 Français</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_it.md">🇮🇹 Italiano</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_de.md">🇩🇪 Deutsch</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_pt.md">🇵🇹 Português</a></p>
**ALTGRAPH** è un'estensione professionale per l'altimetria e le prestazioni di ultima generazione per i ciclocomputer **Hammerhead Karoo** (Karoo 2 e Karoo 3), sviluppata da **David García Pascual** utilizzando l'SDK ufficiale `karoo-ext`.
Rivoluziona il concetto tradizionale di altimetria ciclistica integrando una **Suite di 6 Modelli di Visualizzazione Altimetrica** con selettore dinamico in tempo reale, risoluzione adattiva multiscala, calcolo topografico, rilevamento matematico dei tornanti, filtraggio POI, layout a schermo intero ruotato a 90°, passo **VAM** e calcolo scientifico del **Grado di Fatica (GF)**.
---



## 🐛 VERSIONE 1.0.2: CORREZIONI CRITICHE 🐛

- **Correzione Bug su Percorsi Circolari/Andata e Ritorno**: Risolto un problema in cui il GPS si agganciava erroneamente alla fine del percorso fin dall'inizio, facendo sì che il grafico procedesse a ritroso ("bug snap-to-end").
- **Miglioramento dell'Interfaccia (Radar)**: L'indicatore di pendenza massima ora mostra una freccia colorata (Rossa per salita, Blu Scuro per discesa) sopra la percentuale, migliorandone visibilità e stile.

## 🚀 NOVITÀ NELLA VERSIONE 1.0.0: RADAR ALTIMETRICO E ESPERIENZA PRO 🚀

Siamo arrivati alla versione **1.0.0**! Questo aggiornamento porta una riprogettazione completa dell'esperienza, rendendo il controllo e la visualizzazione più precisi e professionali che mai.

### 🌟 Radar Altimetrico (Nuova Modalità di Visualizzazione 3D)
La nuova modalità **Radar Altimetrico** si sovrappone alla mappa del Karoo, consentendoti di vedere sia la navigazione pura che il rilievo del terreno in arrivo, tutto splendidamente integrato nella stessa schermata.
<p align="center">
  <img src="art/screenshot_radar_altimetrico.png" width="400" />
</p>

### 🔧 Nuove Funzionalità e Miglioramenti:
- **Barra delle Zone di Allenamento**: Integrazione completa della barra colorata inferiore (7 zone) basata sui dati del misuratore di potenza o della frequenza cardiaca nella modalità Radar Altimetrico.
- **Controllo Preciso dello Zoom (+ / -)**: Sostituzione della vecchia lente d'ingrandimento con i pulsanti dedicati **[+]** e **[-]**. Ora puoi ingrandire fino a **blocchi da 50 metri**, bypassando l'algoritmo "Smart Zoom" se desideri il controllo manuale assoluto.
- **Scorrimento Orizzontale (< / >)**: Nuovi pulsanti interattivi nella vista del profilo che ti consentono di trascinare liberamente il grafico a sinistra e a destra senza alterare la scala. Anticipa la salita!
- **Modalità Paesaggio (Landscape) Nativa**: Pieno supporto per Karoo montato orizzontalmente. Ora i pulsanti touch e il profilo si riorientano perfettamente in base al tuo campo visivo, posizionando i controlli in modo ergonomico.
- **Icona del Ciclista in Tempo Reale**: La pallina (posizione attuale del ciclista) ora si sposta fluidamente attraverso il profilo grafico e i colori di elevazione, fornendoti un feedback immediato su dove ti trovi lungo il percorso.
- **Bussola Dinamica**: Nascondimento intelligente della bussola in visualizzazioni diverse dalla mappa isometrica globale, ripulendo l'interfaccia.

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

| 🏔️ Visualizzatore - HC | 🏔️ Visualizzatore (Isometrico) <div align="center">

| 🗺️ GPS Isometrico (Globale) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_especial.png" width="220" alt="🏔️ Visualizzatore - HC" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_iso.png" width="220" alt="🏔️ Visualizzatore (Isometrico)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_gps.png" width="220" alt="🗺️ GPS Isometrico (Globale)" /> |

</div>

## 🆚 ALTGRAPH vs Climber Nativo di Karoo
ALTGRAPH non sostituisce il Climber nativo, ma lo integra offrendo una **vista grafica completa** e personalizzabile. Le differenze principali sono:
- **Risoluzione Adattiva e Rendering Continuo**: Il profilo avanza fisicamente in finestre scorrevoli di 50m. Le barre non "saltano" staticamente, ma scorrono verso di te al ritmo della tua pedalata.
- **Modelli di Visualizzazione**: Il Climber nativo ha una vista fissa; ALTGRAPH offre 5 modelli rivoluzionari.
- **Scale Touch Dinamiche**: Cambia istantaneamente lo zoom del percorso (200m, 1km, 10km, 20km, 50km, ecc.) toccando semplicemente la lente d'ingrandimento.
- **Grado di Fatica (GF)**: L'unico strumento su Karoo che calcola la durezza scientifica di una salita basandosi sul coefficiente APM.

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

## 📲 Installazione su Karoo 2 e Karoo 3
Basata sull'SDK ufficiale `karoo-ext`, l'installazione **non richiede permessi di root** o modifiche pericolose del sistema. È sicura al 100%.
**Tramite Sideloading (Consigliato per Karoo 3)**
1. Scarica l'ultimo file `.apk` dalla sezione [Releases](https://github.com/DAVOE75/ALTGRAPH/releases).
2. Invialo al tuo Karoo usando l'app ufficiale **Hammerhead Companion**.
3. Una volta installata, l'estensione si aggiornerà automaticamente leggendo il `manifest.json`.

#### 🔮 Roadmap e Prossimi Sviluppi (Modalità Free-Ride)

Il prossimo grande traguardo nello sviluppo di ALTGRAPH è la **Generazione Altimetrica in Tempo Reale (Modalità Free-Ride)**. 
Attualmente, l'estensione richiede il caricamento di un percorso (GPX) per ottenere i profili. Non appena Hammerhead sbloccherà l'accesso ai dati di altimetria imminente nel suo SDK, **ALTGRAPH genererà il grafico 3D e le metriche tattiche al volo**, senza bisogno di un tracciato caricato. Potrai esplorare liberamente e la montagna si disegnerà davanti a te in tempo reale.

#### 📄 Licenza e Disclaimer

Questo progetto open source è distribuito sotto licenza **MIT** - Copyright 2026 David García Pascual.
*Disclaimer: Questa estensione non è affiliata, approvata, sponsorizzata o supportata da Hammerhead o SRAM.*
