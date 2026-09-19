<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.4.1 STABLE)

*Leggi in: [Español](README.md) | [English](README_en.md) | [Français](README_fr.md) | [Italiano](README_it.md) | [Deutsch](README_de.md)*

**ALTGRAPH** è un'estensione professionale per l'altimetria e le prestazioni di ultima generazione per i ciclocomputer **Hammerhead Karoo** (Karoo 2 e Karoo 3), sviluppata da **David García Pascual** utilizzando l'SDK ufficiale `karoo-ext`.

Rivoluziona il concetto tradizionale di altimetria ciclistica integrando una **Suite di 5 Modelli di Visualizzazione Altimetrica** con selettore dinamico in tempo reale, risoluzione adattiva multiscala, calcolo topografico, rilevamento matematico dei tornanti, filtraggio POI, layout a schermo intero ruotato a 90°, passo **VAM** e calcolo scientifico del **Grado di Fatica (GF)**.

---

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
