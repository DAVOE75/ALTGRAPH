# 📖 Manual de Usuario - ALTGRAPH (v0.2)

**ALTGRAPH** es una extensión avanzada de altimetría y análisis de rendimiento para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3).

---

## 🇪🇸 ESPAÑOL

### 1. Instalación y Configuración
1. Transfiere la APK `app-debug.apk` a tu dispositivo Karoo mediante **ADB** (`adb install -r app-debug.apk`).
2. En tu Karoo, entra en **Ajustes > Perfiles de Carrera** y selecciona el perfil donde quieras incluir los campos.
3. Edita una página de datos, pulsa en **Añadir Campo** y selecciona la categoría **ALTGRAPH**.

### 2. Panel de Configuración de la App
Al abrir **ALTGRAPH** desde el menú de aplicaciones de tu Karoo 3, podrás personalizar:
- **Distancia por Bloque del Perfil**: 50 m, 100 m, 250 m o 500 m.
- **Umbral de Alerta de Ataque (%)**: 8%, 10%, 12% o 15%.
- **VAM Objetivo (m/h)**: Ajuste desde 500 m/h hasta 2000 m/h.
- **Tipo / Calidad de Asfalto (TA)**:
  - Muy Bueno ($\text{TA} = 0.1$)
  - Bueno ($\text{TA} = 0.5$, *por defecto*)
  - Regular ($\text{TA} = 1.2$)
  - Malo / Gravilla ($\text{TA} = 1.7$)

### 3. Campos de Datos Disponibles (v0.2)

#### 📊 1. Estratega de Altimetría (`Altimetry Profile & Strategy`)
- **Descripción**: Muestra un perfil de elevación dinámico por bloques ajustables (100 m) con un código de colores según el % de pendiente.
- **Alertas de Ataque**: Cuando detecta un tramo con pendiente mayor al umbral configurado, muestra un aviso destacado en pantalla (**¡ATACA!**).
- **Métricas**: Distancia restante a la cima, tiempo estimado de llegada y pendiente media restante.

#### ⛰️ 2. Índice Grado de Fatiga GF (`Fatigue Grade GF`)
- **Descripción**: Muestra el Grado de Fatiga acumulado de la subida restante basándose en el modelo científico:
  $$\text{GF} = \sum \text{DU}^* + \text{TA} + \left(\frac{\text{PMx}}{5}\right)$$
- **Clasificación**: Categoriza automáticamente el puerto según su puntuación (5ª Cat, 4ª Cat, 3ª Cat, 2ª Cat, 1ª Cat, Especial HC).

#### 🚴 3. Ritmo VAM Objetivo (`ClimbPacing VAM Target`) — *Novedad v0.2*
- **Descripción**: Asistente de ritmo basado en tu **VAM (Velocidad de Ascensión Vertical en m/h)**.
- **Utilidad**: Te indica a qué velocidad objetiva (km/h) debes pedalear según la pendiente actual para mantener tu VAM objetivo (ej. 900 m/h) sin desfallecer.

#### 📈 4. Tendencia 3D y Rampa Máxima (`3D Gradient Trend & Max Ramp`) — *Novedad v0.2*
- **Descripción**: Anticipa los cambios de inclinación analizando la tendencia inmediata (↗️ Endureciendo, ➔ Estable, ↘️ Suavizando) e indica el % de rampa máxima alcanzada en el tramo.

---

## 🇬🇧 ENGLISH

### 1. Installation & Setup
1. Transfer `app-debug.apk` to your Karoo device via **ADB** (`adb install -r app-debug.apk`).
2. On your Karoo, go to **Settings > Ride Profiles** and edit your preferred profile.
3. Add a new Data Field and select the **ALTGRAPH** category.

### 2. Extension Settings Panel
When opening **ALTGRAPH** from your Karoo app launcher, you can customize:
- **Profile Block Distance**: 50 m, 100 m, 250 m, or 500 m.
- **Attack Alert Threshold (%)**: 8%, 10%, 12%, or 15%.
- **Target VAM (m/h)**: Adjust from 500 m/h to 2000 m/h.
- **Asphalt Quality (TA)**:
  - Very Good ($\text{TA} = 0.1$)
  - Good ($\text{TA} = 0.5$, *default*)
  - Regular ($\text{TA} = 1.2$)
  - Poor / Gravel ($\text{TA} = 1.7$)

### 3. Available Data Fields (v0.2)

#### 📊 1. Altimetry Profile & Strategy
- **Description**: Displays a dynamic elevation profile divided into customizable distance blocks (100 m) with color coding based on gradient percentage.
- **Attack Alerts**: Displays a high-contrast visual alert (**ATTACK!**) when an upcoming block exceeds your alert threshold.
- **Metrics**: Distance remaining to summit, estimated time of arrival, and average remaining grade.

#### ⛰️ 2. Fatigue Grade Index (GF)
- **Description**: Displays accumulated fatigue difficulty based on the scientific formula:
  $$\text{GF} = \sum \text{DU}^* + \text{TA} + \left(\frac{\text{PMx}}{5}\right)$$
- **Rating**: Automatically categorizes climbs by difficulty score (Category 5 to Special/HC).

#### 🚴 3. ClimbPacing VAM Target — *New in v0.2*
- **Description**: Pacing assistant based on **VAM (Vertical Ascent Speed in m/h)**.
- **Utility**: Recommends the optimal target speed (km/h) for the current slope to sustain your target VAM (e.g. 900 m/h).

#### 📈 4. 3D Gradient Trend & Max Ramp — *New in v0.2*
- **Description**: Predicts slope changes by analyzing short-term gradient trends (↗️ Steepening, ➔ Steady, ↘️ Easing) and tracks the peak % ramp gradient.
