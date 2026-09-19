# ALTGRAPH (v0.4.1 STABLE)

*Leggi in: [Español](README.md) | [English](README_en.md) | [Français](README_fr.md) | [Italiano](README_it.md) | [Deutsch](README_de.md)*

ALTGRAPH è un'estensione professionale di altimetria e prestazioni di ultima generazione per ciclocomputer Hammerhead Karoo (Karoo 2 e Karoo 3) sviluppata da David García Pascual utilizzando l'SDK ufficiale karoo-ext.

Rivoluziona il concetto tradizionale di altimetria ciclistica incorporando una Suite di 5 Modelli di Visualizzazione Altimetrica con selettore dinamico dal vivo, scala monocromatica continua a 15 segmenti, finestra di scorrimento quantistico di 50 metri, risoluzione adattiva multiscala, analisi dei passi di montagna, calcolo topografico opzionale (proiezione orizzontale pura), rilevamento matematico dei tornanti, filtraggio per categorie di POI (Paesi, Fontane, Punti panoramici, Cime), interruttore della scala di zoom touch, tipografia Google Sans / Condensed, modalità paesaggio a schermo intero ruotata di 90°, andatura VAM doppia con velocità consigliata e calcolo scientifico del Grado di Fatica (GF).

## 📸 Schermate Reali del Karoo 3
- 🏔️ Altimetria 3D e Itinerario
- 🎨 Scheda Stile e Selettore
- 🏔️ Scheda 3D
- 📊 Scheda 2D
- 🚴 Scheda VAM

## 🌐 Portale Web Ufficiale e Supporto Multilingue (5 Lingue)
Visita il [Portale Web Ufficiale ALTGRAPH](https://davoe75.github.io/ALTGRAPH/) per la documentazione interattiva in spagnolo, inglese, francese, italiano e tedesco.

## 🚀 Novità e Funzionalità Principali (v0.4.1 Stable)

### 🌟 Suite di 5 Modelli di Altimetria Rivoluzionari (Cambio in tempo reale)
Permette al ciclista di scegliere tra 5 modi visivi per interpretare la montagna dalla scheda superiore 🎨 Stile:
- **🏔️ 3D Classico (Predefinito)**: Profilo professionale con sottile smusso 3D, gradiente monocromatico morbido dallo 0 al 15%, marcatori di altitudine ruotati e capsule scure ad alto contrasto con tipografia nitida per le percentuali.
- **🌅 Orizzonte Isometrico**: Prospettiva 3D da cabina di pilotaggio proiettata in profondità verso l'orizzonte, corsia centrale tratteggiata stile pista di decollo e fascio di luce frontale dinamico proiettato dal faro del ciclista che si inclina in base alla pendenza fisica della rampa imminente.
- **❄️ Oasi e Crogioli Tattici**: Rilevamento intelligente dei tratti di recupero (≤3% e ≥30m) illuminati in blu ghiaccio con badge dinamici ❄️ OASI, crogioli di fuoco per le rampe critiche (≥12%) con badge tattici 🔥 MURO, e nebbia atmosferica da ipossia ad altitudini ≥1400m.
- **⚡ Campo di Forza e Fatica**: Rilievo con linee di tensione gravitazionale ancorate con precisione al pixel alla pendenza, onda cinetica sinusoidale alla base (ciano durante l'avanzamento per inerzia, cremisi rapido quando la pendenza fa calare la cadenza) e linea guida VAM che fluttua dolcemente sul profilo.
- **💎 Monolito di Ossidiana e Plasma**: Montagna scolpita in cristallo di ossidiana scuro sfaccettato (da #151D2C a #04070D), faccetta superiore 3D in cristallo lucido, nucleo interno di plasma radiante e cresta superiore in raggio laser bianco con bagliore neon azzurro.

### 🎨 Scala di Gradienti Intensa ad Alta Visibilità
* **≤ -10.0%**: Blu marino scuro `#041E42` (Discesa ripida).
* **-10.0% a -5.0%**: Blu scuro `#004B87` (Discesa media).
* **-5.0% a -2.0%**: Blu medio `#0072CE` (Discesa dolce).
* **-2.0% a < 0.0%**: Azzurro cielo chiaro `#41B6E6` (Falso piano in discesa).
* **0.0% a 3.0%**: Verde foresta intenso `#388E3C`.
* **3.0% a 5.0%**: Giallo forte `#FBC02D`.
* **5.0% a 8.0%**: Arancione intenso `#F57C00`.
* **8.0% a 10.0%**: Arancione scuro / Ruggine `#E65100`.
* **10.0% a 13.0%**: Rosso intenso `#D32F2F`.
* **13.0% a 17.0%**: Granata rosso scuro `#B71C1C`.
* **> 17.0%**: Nero corvino `#000000`.

### 🔄 Finestra Scorrevole Quantistica di 50m e Avanzamento Fisico
- Il faro del ciclista si arrampica fisicamente lungo il contorno superiore della pendenza da 0 a 50m.
- Al completamento di ogni multiplo di 50 metri, la finestra si sposta in modo pulito di 50m a sinistra senza sbalzi di scala improvvisi.

### 📏 Risoluzione Adattiva Multiscala
- **≤ 500m**: Sottoblocchi di 50m all'interno di blocchi di 100m.
- **500m a 5km**: Blocchi di 100m (blocchi maggiori di 500m).
- **5km a 20km**: Blocchi di 500m (blocchi maggiori di 2km).
- **20km a 50km**: Blocchi di 1km (blocchi maggiori di 5km).
- **> 100km**: Tratti di 10km (blocchi maggiori di 20km).

### ⚡ Architettura Zero-Allocation per Karoo 3
- Canvas di rendering senza allocazione di memoria: zero pause del Garbage Collector (GC) in background.
- Riutilizzo in tempo reale dei buffer Bitmap e Canvas.

### 📊 Tripla Intestazione e Telemetria
- Lettura in tempo reale di PENDENZA ATTUALE, PENDENZA MEDIA del tratto visibile e PENDENZA MASSIMA.
- Rilevamento vettoriale dei tornanti, filtraggio dei punti di riferimento POI (Paesi, Fontane, Punti panoramici, Cime), metodo topografico esatto, andatura VAM obiettivo e Indice del Grado di Fatica (GF).

## 📖 Manuale Utente
Consulta il [Manuale Utente (USER_MANUAL.md)](https://github.com/DAVOE75/ALTGRAPH/blob/main/USER_MANUAL.md) per la guida completa alla configurazione e installazione.

## 🛠️ Requisiti e Installazione
**Requisiti di Sviluppo**
- Android Studio: Ladybug / 2024.2.1 o superiore.
- JDK: Java 17 o superiore.
- SDK Android: compileSdk = 34, minSdk = 26.
- Dispositivo: Hammerhead Karoo 2 o Karoo 3 con ADB abilitato.

**📦 Compilazione**
```bash
# Compilare il progetto
./gradlew assembleDebug

# Installare sul Karoo collegato via USB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 👨‍💻 Sviluppatore
Sviluppato con passione da David García Pascual.

## 📄 Licenza
Questo progetto è distribuito sotto la licenza MIT.
