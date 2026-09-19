<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.4.1 STABLE)

*Leer en: [Español](README.md) | [English](README_en.md) | [Français](README_fr.md) | [Italiano](README_it.md) | [Deutsch](README_de.md)*

**ALTGRAPH** es una extensión de rendimiento y altimetría profesional de última generación para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3) desarrollada por **David García Pascual** utilizando el SDK oficial `karoo-ext`.

Revoluciona el concepto tradicional de altimetría ciclista incorporando una **Suite de 5 Modelos de Visualización Altimétrica** con selector dinámico en vivo, escala monocromática continua de 15 tramos, avance cuántico en ventana rodante de 50 metros, resolución adaptativa multiescala, análisis de puertos de montaña, cálculo topográfico opcional (proyección horizontal pura), detección matemática de curvas de herradura (tornanti), filtrado por categorías de Hitos (Pueblos, Fuentes, Miradores, Cimas), conmutador de escala de zoom al tocar la pantalla, tipografías Google Sans / Condensed, modo apaisado rotado a 90° a pantalla completa, ritmo **VAM** dual con velocidad recomendada y cálculo científico del **Grado de Fatiga (GF)**.

---

## 📸 Capturas en Pantalla Real de Karoo 3

| 🎨 Pestaña Estilo | 🎛️ Menú de Selección | 📊 Dashboard Completo |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="Panel Estilo" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_data_selection.png" width="220" alt="Menú Selección" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_dashboard_completo.png" width="220" alt="Dashboard Completo" /> |

| 🏔️ Altimetría 3D (Escala 200m) | 🏔️ Altimetría 3D (Escala 1km) | 🏔️ Altimetría 3D (Escala 10km) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="Altimetría 3D 200m" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_1km.png" width="220" alt="Altimetría 3D 1km" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_10km.png" width="220" alt="Altimetría 3D 10km" /> |

| 🏔️ Altimetría 3D (Escala 20km) | 🏔️ Altimetría 3D (Escala 200km) | |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_20km.png" width="220" alt="Altimetría 3D 20km" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_200km.png" width="220" alt="Altimetría 3D 200km" /> | |

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
* **$\le -10.0\%$**: Azul marino oscuro `#041E42` (Descenso pronunciado).
* **$-10.0\%$ a $-5.0\%$**: Azul oscuro `#004B87` (Descenso medio).
* **$-5.0\%$ a $-2.0\%$**: Azul medio `#0072CE` (Descenso suave).
* **$-2.0\%$ a $<0.0\%$**: Azul claro celeste `#41B6E6` (Falso llano bajada).
* **$0.0\%$ a $3.0\%$**: Verde bosque intenso `#388E3C`.
* **$3.0\%$ a $5.0\%$**: Amarillo fuerte `#FBC02D`.
* **$5.0\%$ a $8.0\%$**: Naranja intenso `#F57C00`.
* **$8.0\%$ a $10.0\%$**: Naranja oscuro / teja `#E65100`.
* **$10.0\%$ a $13.0\%$**: Rojo intenso `#D32F2F`.
* **$13.0\%$ a $17.0\%$**: Granate rojo oscuro `#B71C1C`.
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

## 🆚 ALTGRAPH vs Climber Nativo de Karoo

ALTGRAPH no sustituye el Climber nativo, sino que lo complementa ofreciendo una **vista gráfica integral** y personalizable como un campo de datos (`Data Field`) o a pantalla completa. Sus principales diferencias son:
- **Resolución Adaptativa y Renderizado Continuo**: El perfil avanza físicamente en ventanas rodantes de 50m. Las barras no pegan "saltos" estáticos, fluyen hacia ti con la cadencia de tu pedaleo.
- **Modelos de Visualización**: El nativo tiene una vista fija; ALTGRAPH te ofrece 5 modelos revolucionarios (Clásica 3D, Horizonte Isométrico, Oasis Tácticos, Campo de Fuerza y Monolito).
- **Escalas Dinámicas al Toque**: Puedes cambiar instantáneamente el zoom de la ruta (200m, 1km, 10km, 20km, 50km, etc.) simplemente tocando el botón de lupa sin salir de tu entrenamiento.
- **Grado de Fatiga (GF)**: Única herramienta en Karoo que calcula la dureza científica de un puerto basándose en el coeficiente APM (Altimetrías de Puertos de Montaña).

---

## ⚙️ Características y Funciones en Detalle

- **Panel Táctico (Dashboard)**: Incorpora campos de datos exclusivos de la extensión para añadirlos libremente a tus pantallas: *Estratega de Altimetría*, *Grado de Fatiga (GF)*, *Ritmo VAM Objetivo* y *Tendencia 3D*.
- **Iconos Topográficos Oficiales**: Identificación de Cimas, Puertos de Montaña (Map-Pin), Pueblos y Fuentes extraídos directamente de la ruta o del relieve.
- **Detección Matemática de Curvas**: El algoritmo detecta giros cerrados (tornanti/herraduras) y te avisa gráficamente.
- **Cálculo Topográfico Puro**: Opcionalmente, puedes calcular las distancias en base a proyección horizontal para un rigor absoluto en puertos extremos de alta montaña.
- **Temas Oscuros y de Alto Contraste**: Interfaz diseñada para una legibilidad instantánea bajo el sol directo o con gafas fotocromáticas.

---

## 📲 Instalación en Karoo 2 y Karoo 3

Al estar basada en el SDK oficial `karoo-ext`, la instalación **no requiere root** ni modificaciones peligrosas del sistema operativo. Es 100% segura.

**Vía Sideloading (Recomendado para Karoo 3)**
1. Descarga el archivo `.apk` de la última versión desde la sección [Releases](https://github.com/DAVOE75/ALTGRAPH/releases).
2. Envíalo a tu Karoo usando la app oficial **Hammerhead Companion** en tu smartphone.
3. Una vez instalada, la extensión ALTGRAPH buscará actualizaciones futuras de manera automática leyendo el `manifest.json`.

**Vía ADB (Avanzado)**
1. Habilita las Opciones de Desarrollador y la depuración USB en tu dispositivo Karoo.
2. Conecta el dispositivo al PC y ejecuta: `adb install -r altgraph.apk`

---

## 🔧 Configuración Inicial

Para dar vida a ALTGRAPH en tu pantalla de entrenamiento:
1. En tu Karoo, ve a **Profiles** (Perfiles de usuario) y edita tu perfil habitual (ej: Carretera o MTB).
2. Edita una de las páginas de datos y selecciona la disposición (layout) que prefieras (soporta desde el bloque gráfico central hasta pantalla completa 100%).
3. Toca la celda para elegir el campo, baja a la sección de extensiones y elige **ALTGRAPH**.
4. ¡Opcional!: Abre la app compañera de ALTGRAPH desde el cajón principal de apps (App Launcher) para ajustar tus preferencias, como el modelo 3D por defecto o tu VAM objetivo.

---

## 🧠 Arquitectura y Cómo Funciona

- **Integración Nativa Total**: Extrae la ruta, el track y el progreso en vivo desde el `OnNavigationState` de la API oficial `karoo-ext`. Utiliza los mismos datos en crudo que el sistema de escalada de Hammerhead.
- **Procesamiento de Telemetría**: El progreso del ciclista se triangula cruzando el flujo `DISTANCE_TO_DESTINATION` con la polilínea de elevación original, filtrando errores del GPS.
- **Renderizado Zero-Allocation**: Todo el motor gráfico 3D ha sido programado sobre un Canvas puro en Android. No se generan objetos "basura" en bucle (Zero GC pauses), lo que garantiza 0 tirones (lags) visuales y un impacto mínimo en la batería.

---

## ⚠️ Limitaciones Conocidas

- Si te sales de la ruta cargada (Off-Route), el gráfico dejará de avanzar hasta que el sistema de navegación recalcule o vuelvas a la trazada oficial.
- Si Karoo no proporciona la polilínea de elevación para una ruta importada de terceros, ALTGRAPH se basará en medias para trazar el perfil, disminuyendo la fidelidad hiperrealista.

---

## 📦 Compilación para Desarrolladores

El proyecto utiliza Gradle y Kotlin. Requiere JDK 17 y Android SDK (Plataforma 34).
```bash
./gradlew assembleDebug # Compila el APK de prueba
./gradlew testDebugUnitTest # Ejecuta los test matemáticos
```

---

## 🤝 Créditos y Agradecimientos

- Construido sobre el SDK oficial **[karoo-ext](https://github.com/hammerheadnav/karoo-ext)** de Hammerhead (Licencia Apache 2.0).
- Inspirado por la comunidad open-source de modding para Karoo (como la mítica extensión *Ki2* o *Climber+*).
- Desarrollado por **David García Pascual**.

---

## 📄 Licencia y Descargo de Responsabilidad

Este proyecto de código abierto se distribuye bajo la licencia **MIT** - Copyright 2026 David García Pascual.

*Descargo de responsabilidad: Esta extensión no está afiliada, respaldada, patrocinada ni soportada por Hammerhead o SRAM. Úsala bajo tu propio riesgo y, por favor, mantén siempre los ojos en la carretera y las manos en el manillar.*


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
