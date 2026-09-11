# 📖 Manual de Usuario - ALTGRAPH (v0.2.3)

**ALTGRAPH** es una extensión avanzada de altimetría y análisis de rendimiento para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3), desarrollada por **David García Pascual**.

---

## 🇪🇸 ESPAÑOL

### 1. Instalación y Configuración
1. Transfiere la APK `app-debug.apk` a tu dispositivo Karoo mediante **ADB** (`adb install -r app-debug.apk`).
2. En tu Karoo, entra en **Ajustes > Perfiles de Carrera** y selecciona el perfil donde quieras incluir los campos.
3. Edita una página de datos, pulsa en **Añadir Campo** y selecciona la categoría **ALTGRAPH**.

### 2. Panel de Configuración de la App
Al abrir **ALTGRAPH** desde el menú de aplicaciones de tu Karoo 3, podrás personalizar:
- **Tipo de Letra (Google Sans / Condensed)**:
  - `Condensed (Estrecha/Alta)` *(por defecto)*
  - `Sans-Serif (Estándar)`
  - `Sans Medium`
  - `Sans Black (Extra Gruesa)`
  - `Monospace Digital`
- **Distancia por Bloque del Perfil**: 50 m, 100 m, 250 m, 500 m o 1 km.
- **Número de Tramos Visibles en Perfil**: Ajuste de 1 a 10 tramos (5 por defecto).
- **Distancia de Anticipación 3D (Eje X)**: 200m a 500m (pasos de 50m), salto a 1 km y de 1 km en 1 km hasta 10 km.
- **Pendiente Mínima de Rampa (%)**: 5% a 20% (10% por defecto).
- **Pendiente Máxima de Rampa (%)**: 10% a 30% (15% por defecto).
- **Girar Gráfico 90° (Modo Apaisado / Horizontal)**: Activado / Desactivado.
- **VAM Objetivo (m/h)**: Ajuste desde 500 m/h hasta 2000 m/h.
- **Tipo / Calidad de Asfalto (TA)**:
  - Muy Bueno ($\text{TA} = 0.1$)
  - Bueno ($\text{TA} = 0.5$, *por defecto*)
  - Regular ($\text{TA} = 1.2$)
  - Malo / Gravilla ($\text{TA} = 1.7$)
- **Tamaño de Letra en Altimetría 3D**: Normal, Grande o Extra Grande.
- **Mostrar % en Bloques del Perfil**: Activado / Desactivado.
- **Mostrar Cotas de Altitud en Gráfico 3D**: Activado / Desactivado.
- **Mostrar Indicadores de Rampas Duras (≥10%) en 3D**: Activado / Desactivado.

### 3. Campos de Datos Disponibles (v0.2.3)

#### 🏔️ 1. Altimetría 3D (`3D Altimetry`)
- **Descripción**: Perfil 3D en perspectiva isométrica con sub-bloques de 50m dentro de cada kilómetro, dibujo continuo de descansillos y rampas, escala matemática proporcional de altitud, cotas verticales en metros, % de pendiente centrado en cada bloque, globo flotante 3D de pendiente actual (**`📍 7.2%`**) sobre el ciclista y soporte de orientación apaisada a 90°.

#### 📊 2. Estratega de Altimetría (`Altimetry Profile & Strategy`)
- **Descripción**: Perfil de elevación por bloques ajustables (50m, 100m, 250m, 500m, 1km) con tramos visibles personalizables (1 a 10, por defecto 5), código de colores y % de pendiente impreso en cada bloque.

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
1. Transfer `app-debug.apk` to your Karoo device via **ADB** (`adb install -r app-debug.apk`).
2. On your Karoo, go to **Settings > Ride Profiles** and edit your preferred profile.
3. Add a new Data Field and select the **ALTGRAPH** category.

### 2. Extension Settings Panel
When opening **ALTGRAPH** from your Karoo app launcher, you can customize:
- **Font Family (Google Sans / Condensed)**:
  - `Condensed (Narrow/Tall)` *(default)*
  - `Sans-Serif (Standard)`
  - `Sans Medium`
  - `Sans Black (Heavy)`
  - `Monospace Digital`
- **Profile Block Distance**: 50 m, 100 m, 250 m, 500 m, or 1 km.
- **Upcoming Visible Blocks Count**: 1 to 10 blocks (5 default).
- **3D Lookahead Distance (X Axis)**: 200m to 500m (50m steps), jump to 1 km, and 1 km steps up to 10 km.
- **Ramp Minimum Slope Alert (%)**: 5% to 20% (10% default).
- **Ramp Maximum Slope Alert (%)**: 10% to 30% (15% default).
- **Rotate Graph 90° Clockwise (Landscape Mode)**: Enabled / Disabled.
- **Target VAM (m/h)**: Adjust from 500 m/h to 2000 m/h.
- **Asphalt Quality (TA)**:
  - Very Good ($\text{TA} = 0.1$)
  - Good ($\text{TA} = 0.5$, *default*)
  - Regular ($\text{TA} = 1.2$)
  - Poor / Gravel ($\text{TA} = 1.7$)
- **3D Slope % Font Size**: Normal, Large, or XL.
- **Show Slope % Inside Profile Blocks**: Enabled / Disabled.
- **Show Altitude Labels in 3D Graph**: Enabled / Disabled.
- **Show Steep Ramp Callouts (≥10%) in 3D Graph**: Enabled / Disabled.

### 3. Available Data Fields (v0.2.3)

#### 🏔️ 1. 3D Altimetry
- **Description**: Renders a 3D isometric elevation profile with 50m micro-block sub-segmenting inside kilometer blocks, continuous micro-relief ribbon (flats & ramps), mathematical proportional elevation scale, 90° vertical cotas, live 3D floating rider slope balloon (**`📍 7.2%`**), and 90° landscape orientation support.

#### 📊 2. Altimetry Profile & Strategy
- **Description**: Displays a dynamic elevation profile divided into customizable distance blocks (50m, 100m, 250m, 500m, 1km) with customizable visible blocks count (1 to 10, default 5).

#### ⛰️ 3. Fatigue Grade Index (GF)
- **Description**: Displays accumulated fatigue difficulty based on the scientific formula:
  $$\text{GF} = \sum \text{DU}^* + \text{TA} + \left(\frac{\text{PMx}}{5}\right)$$

#### 🚴 4. ClimbPacing VAM Target — *Option C Dual*
- **Description**: Pacing assistant with instantaneous VAM (`850 m/h`), recommended target speed (`12.5 km/h`), and status badge.

#### 📈 5. 3D Gradient Trend & Max Ramp
- **Description**: Predicts slope changes (↗️ Steepening, ➔ Steady, ↘️ Easing) and tracks peak % ramp.
