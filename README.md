<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.6.0 PRO)

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

| 🏔️ Altimetría 3D (Escala 20km) | 🏔️ Altimetría 3D (Escala 200km) | 🏔️ Visor de Puertos - 3ª Cat |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_20km.png" width="220" alt="Altimetría 3D 20km" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_200km.png" width="220" alt="Altimetría 3D 200km" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer.png" width="220" alt="Visor de Puertos 3ª Cat" /> |

| 🏔️ Visor de Puertos - Especial C.E. | | |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_especial.png" width="220" alt="Visor de Puertos Especial CE" /> | | |

---

## 🌐 Portal Web Oficial y Soporte Multilingüe (5 Idiomas)
Visita el **[Portal Web Oficial de ALTGRAPH (davoe75.github.io/ALTGRAPH)](https://davoe75.github.io/ALTGRAPH/)** para la documentación interactiva en español, inglés, francés, italiano y alemán.

---

## 🚀 Novedades y Funciones Destacadas (v0.6.0 PRO)

### 🏔️ Visor de Puertos de Montaña (NUEVO en v0.4.5+)
La funcionalidad más demandada por la comunidad ciclista. Un **campo de datos a pantalla completa** dedicado exclusivamente a la visualización integral de los puertos de montaña detectados automáticamente en tu ruta cargada:

- **Navegación por puertos**: Flechas `<` y `>` para explorar todos los puertos de la ruta antes de salir o durante la marcha.
- **Información de cada puerto**: Longitud exacta, desnivel acumulado, kilómetros restantes hasta el inicio y categoría oficial.
- **Categorización estilo La Vuelta a España**: Motor APM propio que calcula y asigna categoría **3ª, 2ª, 1ª Cat o Especial C.E.** con los mismos criterios que los grandes corredores de ciclismo profesional.
- **Perfil altimétrico 3D del puerto**: Visualización completa del perfil de cada puerto con el estilo gráfico 3D de ALTGRAPH.
- **Cotas topográficas de los tres picos más altos**: Detecta automáticamente los 3 puntos más elevados del recorrido y los marca con un triángulo y cota en metros.
- **Líneas verticales de kilómetros destacadas**: Las líneas que corresponden a los kilómetros enteros en el eje X se representan más gruesas y oscuras que las divisiones de sub-bloque.
- **Leyenda de escala inferior**: Dos líneas de información debajo de la gráfica indican en todo momento `Cada bloque = Xkm` y `Cada sub-bloque = Xm` para contextualizar la escala visual.
- **Baliza del ciclista inteligente**: La bola luminosa del ciclista solo aparece sobre la gráfica del puerto cuando el ciclista llega físicamente al kilómetro de inicio del puerto. Antes, la pantalla muestra el perfil completo limpio.
- **Botones de zoom `+` y `−`** (posición ajustada para no solaparse con las cotas del eje Y).
- **Texto del eje X más grande**: Etiquetas de distancia kilométrica más legibles.
- **Internacionalización completa**: Todos los textos disponibles en 🇪🇸 Español, 🇬🇧 English, 🇫🇷 Français, 🇮🇹 Italiano y 🇩🇪 Deutsch.

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
- **Visor de Puertos de Montaña**: Campo dedicado con perfil completo de cada puerto, categorización estilo La Vuelta y leyenda de escala dinámica.

---

## ⚙️ Características y Funciones en Detalle

### 📈 Campos de Datos Exclusivos (Dashboard Táctico)
Además del motor gráfico, ALTGRAPH expone métricas avanzadas como campos individuales para que diseñes tu pantalla perfecta:
- **🧠 Estratega de Altimetría**: Muestra un bloque visual inteligente con el resumen de los próximos kilómetros. Divide el horizonte en tramos de color para que sepas de un vistazo rápido si lo que viene es puerto duro (rojo), descanso (verde) o terreno rompepiernas.
- **🏔️ Visor de Puertos 3D**: Explora de forma independiente cada puerto de montaña detectado en tu ruta. Se renderiza a pantalla completa con navegación táctil, mostrando longitud, desnivel, kilómetros restantes, pendiente media y máxima, y categorización oficial tipo **La Vuelta a España** (3ª, 2ª, 1ª Cat o Especial C.E.) calculada matemáticamente. Incluye perfil 3D completo con cotas topográficas, leyenda de escala y baliza del ciclista inteligente.
- **⚡ Grado de Fatiga (GF)**: Motor científico basado en el coeficiente APM que evalúa la dureza real del puerto en vivo, mostrándote un coeficiente numérico de desgaste.
- **🚀 Ritmo VAM Objetivo**: Define tu VAM (Velocidad Ascensional Media) ideal en la configuración. Este campo calcula la pendiente exacta bajo tus ruedas y te dicta a qué **velocidad (km/h)** debes rodar en ese instante para cumplir tu objetivo en la cima.
- **📐 Tendencia 3D y Rampa Máxima**: Te avisa de si la pendiente se está endureciendo o suavizando antes de que tus piernas lo noten.

### 🗺️ Navegación y Topografía Avanzada
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

## 🔮 Roadmap y Próximos Avances (Free-Ride Mode)

El próximo gran hito en el desarrollo de ALTGRAPH es la **Generación Altimétrica en Tiempo Real (Modo Free-Ride)**. 

Actualmente la extensión requiere cargar una ruta (GPX) para obtener los perfiles. Tan pronto como Hammerhead libere y habilite el acceso a los datos de elevación inminente en su SDK para desarrolladores externos, **ALTGRAPH generará la gráfica 3D y todas sus métricas tácticas sobre la marcha**, sin necesidad de llevar un track cargado. Podrás salir a explorar libremente y la montaña se dibujará frente a ti en tiempo real.

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
## 🆕 What's new in v0.4.62 / Novedades v0.4.62

### 🇪🇸 ESPAÑOL
- **🏔️ Visor de Puertos de Montaña (Campo de datos a pantalla completa)**: Nuevo campo dedicado que muestra el perfil altimétrico 3D completo de cada puerto detectado en la ruta. Navegación `< >` entre puertos, longitud, desnivel, kilómetros restantes, pendiente media, pendiente máxima y categoría (3ª, 2ª, 1ª, Especial C.E.) al estilo La Vuelta a España.
- **Categorización de puertos ajustada a La Vuelta a España**: El motor APM recalibrado asigna categorías con la misma filosofía que los organizadores de la Vuelta a España (p.ej. 5,6km al 6,6% = 3ª Categoría).
- **Cotas topográficas de los 3 picos más altos**: Detección automática de los tres puntos más elevados de la gráfica con marcador triangular y cota en metros. Texto light (no bold) y tamaño reducido para no saturar la vista.
- **Líneas verticales de kilómetros más gruesas y oscuras**: Solo las líneas correspondientes a los kilómetros enteros del eje X aparecen con grosor 3.0px y color `#0F172A`, mientras las divisiones menores mantienen su línea fina `1.2px`.
- **Texto de distancia en eje X más grande**: Etiquetas kilométricas más legibles en pantalla.
- **Leyenda de escala en dos líneas**: Debajo de cada gráfica de puerto aparece `Cada bloque = Xkm` (texto grande) y `Cada sub-bloque = Xm` (texto más pequeño).
- **Baliza del ciclista inteligente**: La bola luminosa solo aparece cuando el ciclista está físicamente dentro del tramo del puerto. Antes de llegar, la gráfica muestra el perfil completo limpio.
- **Botones de zoom reposicionados**: Los botones `+` y `−` se han desplazado para no solaparse con las cotas del eje Y.
- **Internacionalización completa**: Todos los nuevos textos disponibles en Español, English, Français, Italiano y Deutsch.

### 🇬🇧 ENGLISH
- **🏔️ Mountain Pass Viewer (Full-screen data field)**: New dedicated field showing the complete 3D altimetric profile for each detected climb on the route. `< >` navigation between climbs, length, elevation gain, remaining kilometers, average grade, max grade and category (3rd, 2nd, 1st, Special HC) in the style of La Vuelta a España.
- **Climb categorization aligned with La Vuelta a España**: The recalibrated APM engine assigns categories using the same philosophy as Vuelta a España organizers (e.g., 5.6km at 6.6% = 3rd Category).
- **Topographic markers for the 3 highest peaks**: Automatic detection of the three highest points on the graph with a triangular pin and altitude in meters. Light (non-bold) text at reduced size to avoid visual clutter.
- **Thicker, darker vertical km lines**: Only lines corresponding to full kilometers on the X-axis are rendered at 3.0px width and `#0F172A` color; sub-block dividers stay thin at 1.2px.
- **Larger X-axis distance labels**: Kilometer labels are bigger and easier to read on screen.
- **Two-line scale legend**: Below each climb profile: `Each block = Xkm` (larger) and `Each sub-block = Xm` (smaller).
- **Smart cyclist beacon**: The glowing ball only appears when the rider is physically within the climb's distance range. Before reaching the start, the graph shows the full clean profile.
- **Repositioned zoom buttons**: `+` and `−` buttons repositioned to avoid overlapping with Y-axis altitude labels.
- **Full i18n**: All new text strings available in Spanish, English, French, Italian and German.

### 🇫🇷 FRANÇAIS
- **🏔️ Visionneuse de Cols (Champ plein écran)**: Nouveau champ dédié affichant le profil altimétrique 3D complet de chaque col détecté sur l'itinéraire. Navigation `< >` entre cols, longueur, dénivelé, kilomètres restants, pente moyenne, pente maximale et catégorie (3e, 2e, 1re, Hors Catégorie) à la manière de La Vuelta.
- **Catégorisation des cols alignée sur La Vuelta a España**: Le moteur APM recalibré attribue les catégories avec la même philosophie que les organisateurs de la Vuelta (ex. 5,6 km à 6,6 % = 3e Catégorie).
- **Repères topographiques des 3 sommets les plus élevés**: Détection automatique des trois points les plus hauts du graphique avec un repère triangulaire et l'altitude en mètres. Texte léger (non gras) et taille réduite pour ne pas surcharger la vue.
- **Lignes verticales km plus épaisses et plus sombres**: Seules les lignes correspondant aux kilomètres entiers de l'axe X sont tracées à 3,0 px et couleur `#0F172A`; les divisions mineures gardent leur ligne fine 1,2 px.
- **Étiquettes de distance axe X plus grandes**: Étiquettes kilométriques plus lisibles à l'écran.
- **Légende d'échelle en deux lignes**: Sous chaque profil de col : `Chaque bloc = Xkm` (grand) et `Chaque sous-bloc = Xm` (plus petit).
- **Balise cycliste intelligente**: La bille lumineuse n'apparaît que lorsque le cycliste est physiquement dans la plage du col. Avant d'y arriver, le graphique affiche le profil complet et propre.
- **Boutons de zoom repositionnés**: Les boutons `+` et `−` ont été déplacés pour ne pas chevaucher les étiquettes de l'axe Y.
- **i18n complet**: Toutes les nouvelles chaînes de texte disponibles en Espagnol, Anglais, Français, Italien et Allemand.

### 🇮🇹 ITALIANO
- **🏔️ Visualizzatore di Passi di Montagna (Campo a schermo intero)**: Nuovo campo dedicato che mostra il profilo altimetrico 3D completo di ogni salita rilevata sul percorso. Navigazione `< >` tra le salite, lunghezza, dislivello, chilometri mancanti, pendenza media, pendenza massima e categoria (3ª, 2ª, 1ª, Fuori Categoria) nello stile della Vuelta a España.
- **Categorizzazione delle salite allineata alla Vuelta a España**: Il motore APM ricalibrato assegna categorie con la stessa filosofia degli organizzatori della Vuelta (es. 5,6km al 6,6% = 3ª Categoria).
- **Marcatori topografici dei 3 punti più elevati**: Rilevamento automatico dei tre punti più alti del grafico con un segnaposto triangolare e la quota in metri. Testo light (non bold) a dimensione ridotta per non appesantire la vista.
- **Linee verticali km più spesse e scure**: Solo le linee corrispondenti ai chilometri interi dell'asse X vengono disegnate a 3,0px e colore `#0F172A`; le divisione secondarie mantengono la linea sottile 1,2px.
- **Etichette distanza asse X più grandi**: Etichette chilometriche più leggibili sullo schermo.
- **Legenda di scala a due righe**: Sotto ogni profilo di salita: `Ogni blocco = Xkm` (grande) e `Ogni sotto-blocco = Xm` (più piccolo).
- **Segnalatore ciclista intelligente**: La sfera luminosa appare solo quando il ciclista è fisicamente all'interno del tratto della salita. Prima di arrivare, il grafico mostra il profilo completo pulito.
- **Pulsanti zoom riposizionati**: I pulsanti `+` e `−` sono stati spostati per non sovrapporsi alle etichette dell'asse Y.
- **i18n completo**: Tutte le nuove stringhe di testo disponibili in Spagnolo, Inglese, Francese, Italiano e Tedesco.

### 🇩🇪 DEUTSCH
- **🏔️ Bergpass-Viewer (Vollbild-Datenfeld)**: Neues dediziertes Feld, das das vollständige 3D-Höhenprofil jedes erkannten Anstiegs auf der Route anzeigt. `< >`-Navigation zwischen Anstiegen, Länge, Höhengewinn, verbleibende Kilometer, Durchschnittssteigung, maximale Steigung und Kategorie (3., 2., 1., Hors Catégorie) im Stil der Vuelta a España.
- **Anstieskategorisierung gemäß Vuelta a España**: Die neu kalibrierte APM-Engine vergibt Kategorien nach der gleichen Philosophie wie die Vuelta-Veranstalter (z. B. 5,6 km bei 6,6 % = 3. Kategorie).
- **Topografische Markierungen der 3 höchsten Gipfel**: Automatische Erkennung der drei höchsten Punkte im Diagramm mit einem dreieckigen Symbol und der Höhe in Metern. Schlanker (nicht fetter) Text in reduzierter Größe, um die Ansicht nicht zu überladen.
- **Dickere, dunklere vertikale km-Linien**: Nur Linien für ganze Kilometer auf der X-Achse werden mit 3,0 px und Farbe `#0F172A` gezeichnet; kleinere Unterteilungen behalten ihre dünne Linie von 1,2 px.
- **Größere X-Achsen-Entfernungsbeschriftungen**: Kilometerbeschriftungen sind auf dem Bildschirm besser lesbar.
- **Zweizeilige Maßstabslegende**: Unter jedem Anstiegsprofil: `Jeder Block = Xkm` (größer) und `Jeder Teilblock = Xm` (kleiner).
- **Intelligenter Radfahrer-Beacon**: Die leuchtende Kugel erscheint nur, wenn der Fahrer physisch im Streckenabschnitt des Anstiegs ist. Vor der Ankunft zeigt das Diagramm das vollständige, saubere Profil.
- **Zoom-Schaltflächen neu positioniert**: Die Schaltflächen `+` und `−` wurden verschoben, damit sie sich nicht mit den Y-Achsen-Höhenbeschriftungen überschneiden.
- **Vollständige i18n**: Alle neuen Textzeichenketten in Spanisch, Englisch, Französisch, Italienisch und Deutsch verfügbar.


---
## 🆕 What's new in v0.5.0 / Novedades v0.5.0

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

---
## 🚀 What's new in v0.6.0 (World Tour Pro Features) / Novedades v0.6.0

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
