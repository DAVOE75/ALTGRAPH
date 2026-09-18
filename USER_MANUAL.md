# 📖 Manual de Usuario - ALTGRAPH (v0.4.0 STABLE)

**ALTGRAPH** es una extensión avanzada de altimetría y análisis de rendimiento para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3), desarrollada por **David García Pascual**.

---

## 📸 Capturas de Pantalla en Karoo 3 Real

| 🏔️ Altimetría 3D | 🎨 Panel Estilo | 🏔️ Panel 3D | 📊 Panel 2D | 🚴 Panel VAM |
| :---: | :---: | :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="160" alt="3D Profile" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="160" alt="Estilo" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_3d.png" width="160" alt="3D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_2d.png" width="160" alt="2D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_vam.png" width="160" alt="VAM" /> |

---

## 🇪🇸 ESPAÑOL

### 1. Instalación y Configuración
1. Transfiere la APK `ALTGRAPH-v0.4.0.apk` a tu dispositivo Karoo mediante **ADB** (`adb install -r ALTGRAPH-v0.4.0.apk`).
2. En tu Karoo, entra en **Ajustes > Perfiles de Carrera** y selecciona el perfil donde quieras incluir los campos.
3. Edita una página de datos, pulsa en **Añadir Campo** y selecciona la categoría **ALTGRAPH**.

### 2. Panel de Configuración por Pestañas Categorizadas
Al abrir **ALTGRAPH** desde el menú de aplicaciones de tu Karoo 3, dispones de 4 pestañas organizadas:

* **🎨 Pestaña ESTILO**:
  - **VISTA Y MODELO DE ALTIMETRÍA (Selector de 5 Modelos)**:
    - 🏔️ **Clásica 3D (Por defecto)**: Perfil profesional con bisel 3D sutil, degradado monocromático suave de 15 tramos del 0 al 15% y cotas nítidas.
    - 🌅 **Horizonte Isométrico**: Perspectiva de cabina (*cockpit 3D*) inclinada hacia el horizonte, carril central tipo pista y haz de luz frontal de la baliza orientado según la pendiente inminente.
    - ❄️ **Oasis y Crisoles Tácticos**: Resalta descansillos de recuperación $\le 3\%$ en azul hielo con carteles `❄️ OASIS [dist]m` y rampas críticas $\ge 12\%$ en fuego térmico con avisos `🔥 MURO [dist]m`, con bruma de hipoxia $>1400\text{m}$.
    - ⚡ **Campo de Fuerza y Fatiga**: Líneas de tensión gravitatoria ancladas milimétricamente al relieve, onda cinética senoidal en la base (cian/carmesí según resistencia) y línea guía de ritmo VAM flotante.
    - 💎 **Monolito de Obsidiana y Plasma**: Montaña facetada en cristal de obsidiana ahumada, faceta superior 3D pulida, núcleo interior de plasma radiante y haz láser blanco con resplandor neón.
  - **Tipo de Letra (Google Sans / Condensed)**: `Condensed (Estrecha/Alta)`, `Sans-Serif (Estándar)`, `Sans Medium`, `Sans Black (Extra Gruesa)`, `Monospace Digital`.
  - **Girar Gráfico 90° (Modo Apaisado / Horizontal)**: Activado / Desactivado.
  - **Tamaño de Letra en Altimetría 3D**: `Normal`, `Grande` o `Extra Gr.`.
  - **Mostrar Controles de Zoom Táctil (🔍)**: Activado / Desactivado.

* **🏔️ Pestaña 3D**:
  - **Distancia de Anticipación 3D (Eje X)**: 200m a 500m (pasos de 50m), salto a 1 km y de 1 km en 1 km hasta 100 km.
  - **Mostrar Cotas de Altitud en Gráfico 3D**: Activado / Desactivado.
  - **Mostrar Indicadores de Rampas Duras (≥10%) en 3D**: Activado / Desactivado.
  - **Pendiente Mínima de Rampa (%)**: 5% a 20% (10% por defecto).
  - **Pendiente Máxima de Rampa (%)**: 10% a 30% (15% por defecto).
  - **Mostrar Curvas de Herradura (Líneas Negras)**: Activado / Desactivado.
  - **Filtro de Puntos de Interés (Hitos POIs)**: 🏘️ Pueblos, 💧 Fuentes, 📸 Miradores, 📡 Cimas.
  - **Método de Cálculo Topográfico (Proyección Horizontal)**: Activado / Desactivado.
  - **Mostrar Pendiente Máxima en Encabezado 3D**: Activado / Desactivado.

* **📊 Pestaña 2D**:
  - **Distancia por Bloque del Perfil**: 50 m, 100 m, 250 m, 500 m o 1 km.
  - **Número de Tramos Visibles en Perfil**: Ajuste de 1 a 10 tramos (5 por defecto).
  - **Mostrar % en Bloques del Perfil**: Activado / Desactivado.
  - **Activar Alertas Visuales de Ataque**: Activado / Desactivado.
  - **Umbral de Alerta de Ataque (%)**: 8%, 10%, 12% o 15%.

* **🚴 Pestaña VAM**:
  - **VAM Objetivo (m/h)**: Ajuste desde 500 m/h hasta 2000 m/h.
  - **Tipo / Calidad de Asfalto (TA)**: Muy Bueno (0.1), Bueno (0.5), Regular (1.2), Malo / Gravilla (1.7).

### 3. Campos de Datos Disponibles (v0.4.0)

#### 🏔️ 1. Altimetría 3D (`3D Altimetry`)
- **Descripción**: Perfil 3D avanzado con selector de 5 estilos (Clásico, Horizonte Iso, Oasis Tácticos, Campo Fuerza, Monolito Obsidiana). Incorpora ventana rodante cuántica de 50m, progresión física del ciclista por la cresta, resolución adaptativa multiescala (50m a 10km), escala monocromática suave de 15 tramos del 0 al 15% (blanco a rojo), cotas de altitud nítidas, cápsulas oscuras de alto contraste para %, lectura triple en encabezado (**Pendiente Actual**, **Pendiente Media del tramo** y **Pendiente Máxima**), marcas de herraduras, hitos POI y zoom táctil en pantalla.

#### 📊 2. Estratega de Altimetría (`Altimetry Profile & Strategy`)
- **Descripción**: Perfil de elevación por bloques ajustables (50m a 1km) con tramos visibles personalizables (1 a 10), código de colores y % de pendiente impreso en cada bloque.

#### ⛰️ 3. Índice Grado de Fatiga GF (`Fatigue Grade GF`)
- **Descripción**: Muestra el Grado de Fatiga acumulado de la subida restante basándose en el modelo científico:
  $$\text{GF} = \sum \text{DU}^* + \text{TA} + \left(\frac{\text{PMx}}{5}\right)$$

#### 🚴 4. Ritmo VAM Objetivo (`ClimbPacing VAM Target`) — *Opción C Dual*
- **Descripción**: Asistente de ritmo con VAM instantánea (`850 m/h`), velocidad objetivo recomendada en km/h (`12.5 km/h`) e insignia de estado (`🟢 EN RITMO`, `🔴 SOBREESFUERZO`, `🔵 POR DEBAJO`).

#### 📈 5. Tendencia 3D y Rampa Máxima (`3D Gradient Trend & Max Ramp`)
- **Descripción**: Anticipa los cambios de inclinación analizando la tendencia inmediata (↗️ Endureciendo, ➔ Estable, ↘️ Suavizando) e indica el % de rampa máxima alcanzada.

---

## 🇬🇧 ENGLISH

### 1. Installation & Setup
1. Transfer `ALTGRAPH-v0.4.0.apk` to your Karoo device via **ADB** (`adb install -r ALTGRAPH-v0.4.0.apk`).
2. On your Karoo, go to **Settings > Ride Profiles** and edit your preferred profile.
3. Add a new Data Field and select the **ALTGRAPH** category.

### 2. Tabbed Extension Settings Panel
Open **ALTGRAPH** from your Karoo app launcher to navigate 4 structured tabs:

* **🎨 STYLE Tab**:
  - **ALTIMETRY VIEW & MODEL (5 Models Suite Selector)**:
    - 🏔️ **Classic 3D (Default)**: Subtle 3D bevel, continuous 15-step monochromatic gradient from 0 to 15% (white to red), crisp cotas.
    - 🌅 **Horizon Isometric**: 3D cockpit perspective converging to horizon, runway center striping, and beacon headlight beam oriented by road slope.
    - ❄️ **Tactical Oases & Crucibles**: Highlights recovery shelves $\le 3\%$ in ice cyan with `❄️ OASIS [dist]m` badges and steep walls $\ge 12\%$ with `🔥 MURO [dist]m`, plus hypoxia altitude haze $>1400\text{m}$.
    - ⚡ **Dynamic Force Field**: Gravitational tension lines anchored along the slope, kinetic baseline sine wave, and floating target VAM pacing guideline.
    - 💎 **Monolithic Obsidian & Plasma**: Faceted smoky obsidian crystal mountain, polished top facet, inner radiant plasma core, and neon laser ridge.
  - **Font Family**: Google Sans / Condensed options.
  - **90° Landscape Rotation Mode**: Fullscreen 800x480px view.
  - **3D Slope Font Scale**: Normal, Large, Extra Large.
  - **Touch Zoom Controls toggle**: On/Off.

* **🏔️ 3D Tab**: Lookahead Distance (200m to 100km), Cotas, Ramp Flags, Ramp Min/Max Range, Hairpins, POI Filter, Topographic Method, Max Slope readout.
* **📊 2D Tab**: Profile Block Distance, Visible Blocks, Attack Alert Threshold.
* **🚴 VAM Tab**: Target VAM, Asphalt Quality Factor.

### 3. Available Data Fields (v0.4.0)

#### 🏔️ 1. 3D Altimetry
- **Description**: Next-generation 3D elevation profile with 5 selectable visualization models, 50m quantum rolling window, physical beacon climbing along top edge, adaptive multi-scale resolution, 15-step monochromatic gradient, high-contrast badges, triple header telemetry, GPS hairpins, POIs, and touch zoom.

#### 📊 2. Altimetry Profile & Strategy
- **Description**: Dynamic elevation profile divided into customizable distance blocks with attack warning alerts.

#### ⛰️ 3. Fatigue Grade Index (GF)
- **Description**: Displays accumulated fatigue difficulty based on the scientific formula:
  $$\text{GF} = \sum \text{DU}^* + \text{TA} + \left(\frac{\text{PMx}}{5}\right)$$

#### 🚴 4. ClimbPacing VAM Target — *Option C Dual*
- **Description**: Pacing assistant with instantaneous VAM (`850 m/h`), recommended target speed (`12.5 km/h`), and status badge.

#### 📈 5. 3D Gradient Trend & Max Ramp
- **Description**: Predicts slope changes (↗️ Steepening, ➔ Steady, ↘️ Easing) and tracks peak % ramp.

