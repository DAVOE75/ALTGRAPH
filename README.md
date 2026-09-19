<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.4.1 STABLE)

**ALTGRAPH** es una extensión de rendimiento y altimetría profesional de última generación para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3) desarrollada por **David García Pascual** utilizando el SDK oficial `karoo-ext`.

Revoluciona el concepto tradicional de altimetría ciclista incorporando una **Suite de 5 Modelos de Visualización Altimétrica** con selector dinámico en vivo, escala monocromática continua de 15 tramos, avance cuántico en ventana rodante de 50 metros, resolución adaptativa multiescala, análisis de puertos de montaña, cálculo topográfico opcional (proyección horizontal pura), detección matemática de curvas de herradura (tornanti), filtrado por categorías de Hitos (Pueblos, Fuentes, Miradores, Cimas), conmutador de escala de zoom al tocar la pantalla, tipografías Google Sans / Condensed, modo apaisado rotado a 90° a pantalla completa, ritmo **VAM** dual con velocidad recomendada y cálculo científico del **Grado de Fatiga (GF)**.

---

## 📸 Capturas en Pantalla Real de Karoo 3

| 🏔️ Altimetría 3D e Itinerario | 🎨 Pestaña Estilo y Selector | 🏔️ Pestaña 3D |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="Altimetría 3D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="Panel Estilo" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_3d.png" width="220" alt="Panel 3D" /> |

| 📊 Pestaña 2D | 🚴 Pestaña VAM |
| :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_2d.png" width="220" alt="Panel 2D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_vam.png" width="220" alt="Panel VAM" /> |

---

## 🌐 Portal Web Oficial y Soporte Multilingüe (5 Idiomas)
Visita el **[Portal Web Oficial de ALTGRAPH (davoe75.github.io/ALTGRAPH)](https://davoe75.github.io/ALTGRAPH/)** para la documentación interactiva en español, inglés, francés, italiano y alemán.

---

## 🚀 Novedades y Funciones Destacadas (v0.4.1 Stable)

### 🌟 Suite de 5 Modelos de Altimetría Revolucionarios (Conmutables al vuelo)
Permite al ciclista elegir entre 5 formas visuales de interpretar la montaña desde la tarjeta superior de la pestaña **🎨 Estilo** de la extensión:
1. **🏔️ Clásica 3D (Por defecto)**: Perfil profesional con bisel 3D sutil y estilizado, degradado monocromático suave del 0 al 15%, cotas de altitud rotadas y cápsulas oscuras de alto contraste con tipografía nítida para porcentajes.
2. **🌅 Horizonte Isométrico (`Horizonte Iso`)**: Perspectiva de cabina (*cockpit 3D*) proyectada en profundidad hacia el horizonte, carril central discontinuo tipo pista de despegue y **haz de luz frontal dinámico** proyectado por la baliza del ciclista que se inclina según la pendiente física de la rampa inminente.
3. **❄️ Oasis y Crisoles Tácticos (`Oasis Tácticos`)**: Detección inteligente de descansillos de recuperación ($\le 3\%$ y $\ge 30\text{m}$) iluminados en azul hielo con insignias dinámicas `❄️ OASIS [distancia]m`, crisoles de fuego para rampas críticas ($\ge 12\%$) con insignias tácticas `🔥 MURO [distancia]m`, y bruma atmosférica de hipoxia a altitudes $\ge 1400\text{m}$.
4. **⚡ Campo de Fuerza y Fatiga (`Campo Fuerza`)**: Relieve con líneas gravitatorias de tensión ancladas con precisión de píxel a la pendiente, onda cinética senoidal en la base (cian en avance con inercia, carmesí rápido cuando la pendiente frena la cadencia) y línea guía de ritmo ascensional (VAM) flotando suavemente sobre el perfil.
5. **💎 Monolito de Obsidiana y Plasma (`Monolito`)**: Montaña esculpida en cristal de obsidiana facetado oscuro (`#151D2C` a `#04070D`), faceta superior 3D en cristal pulido, núcleo interior de plasma radiante y cresta superior en haz láser blanco con resplandor neón celeste.

### 🎨 Escala de Gradientes Intensa de Alta Visibilidad
* **$< 0.0\%$**: Blanco puro `#FFFFFF` (Descensos y llaneos).
* **$0.0\%$ a $3.0\%$**: Verde puro flúor `#00FF00`.
* **$3.0\%$ a $5.0\%$**: Amarillo puro flúor `#FFFF00`.
* **$5.0\%$ a $8.0\%$**: Naranja puro eléctrico `#FF6600`.
* **$8.0\%$ a $10.0\%$**: Marrón puro intenso `#964B00`.
* **$10.0\%$ a $13.0\%$**: Rojo puro sangre `#FF0000`.
* **$13.0\%$ a $17.0\%$**: Granate rojo oscuro `#8B0000`.
* **$> 17.0\%$**: Negro azabache `#000000`.

### 🔄 Ventana Rodante Cuántica de 50m y Avance Físico
* El faro del ciclista asciende físicamente por el contorno superior de la pendiente de 0 a 50m.
* Al completar cada múltiplo de 50 metros, la ventana se desplaza limpiamente 50m a la izquierda sin saltos abruptos de escala.

### 📏 Resolución Adaptativa Multiescala
* $\le 500\text{m}$: Sub-bloques de **50m** dentro de bloques de 100m.
* $500\text{m}$ a $5\text{km}$: Bloques de **100m** (bloques mayores de 500m).
* $5\text{km}$ a $20\text{km}$: Bloques de **500m** (bloques mayores de 2km).
* $20\text{km}$ a $50\text{km}$: Bloques de **1km** (bloques mayores de 5km).
* $> 100\text{km}$: Tramos de **10km** (bloques mayores de 20km).

### ⚡ Arquitectura Zero-Allocation para Karoo 3
* Canvas de renderizado sin asignación de memoria: cero pausas del recolector de basura (GC) en segundo plano.
* Reutilización de búfer `Bitmap` y `Canvas` en tiempo real.

### 📊 Encabezado Triple y Telemetría
* Lectura en vivo de **PENDIENTE ACTUAL**, **PENDIENTE MEDIA** del tramo visible y **PENDIENTE MÁXIMA**.
* Detección vectorial de curvas de herradura (tornanti), filtro de hitos POIs (Pueblos, Fuentes, Miradores, Cimas), método topográfico exacto, ritmo VAM objetivo e Índice Grado de Fatiga (GF).

---

## 📖 Manual de Usuario
Consulta el [**Manual de Usuario (`USER_MANUAL.md`)**](USER_MANUAL.md) para ver la guía completa de configuración e instalación.

---

## 🛠️ Requisitos e Instalación

### Requisitos de desarrollo
* **Android Studio**: Ladybug / 2024.2.1 o superior.
* **JDK**: Java 17 o superior.
* **Android SDK**: `compileSdk = 34`, `minSdk = 26`.
* **Dispositivo**: Hammerhead Karoo 2 o Karoo 3 con ADB activado.

---

## 📦 Compilación

```bash
# Compilar proyecto
./gradlew assembleDebug

# Instalar en Karoo conectado por USB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 👨‍💻 Desarrollador
Desarrollado con pasión por **David García Pascual**.

---

## 📄 Licencia
Este proyecto se distribuye bajo la licencia **MIT**.


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
