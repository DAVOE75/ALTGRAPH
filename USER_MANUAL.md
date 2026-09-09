# 📖 Manual de Usuario - ALTGRAPH (v0.2)

**ALTGRAPH** es una extensión avanzada de altimetría y análisis de rendimiento para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3).

---

## 🇪🇸 ESPAÑOL

### 1. Instalación y Configuración
1. Transfiere la APK `app-debug.apk` a tu dispositivo Karoo mediante **ADB** (`adb install -r app-debug.apk`).
2. En tu Karoo, entra en **Ajustes > Perfiles de Carrera** y selecciona el perfil donde quieras incluir los campos.
3. Edita una página de datos, pulsa en **Añadir Campo** y busca la categoría **ALTGRAPH**.

### 2. Campos de Datos Disponibles (v0.2)

#### 📊 1. Estratega de Altimetría (`Altimetry Profile & Strategy`)
- **Descripción**: Muestra un perfil de elevación dinámico por bloques ajustables (100 m) con un código de colores según el % de pendiente.
- **Alertas de Ataque**: Cuando detecta un tramo con pendiente mayor al 10%, muestra un aviso destacado en pantalla (**¡ATACA!**).
- **Métricas**: Distancia restante a la cima, tiempo estimado de llegada y pendiente media restante.

#### ⛰️ 2. Índice de Dificultad APM (`APM Difficulty Index`)
- **Descripción**: Muestra una puntuación numérica basada en la fórmula APM (Altitud, Pendiente, Metros).
- **Utilidad**: Permite saber exactamente cuánta energía/dificultad acumulada te queda por superar antes de coronar la cima.

#### 🚴 3. Ritmo VAM Objetivo (`ClimbPacing VAM Target`) — *Novedad v0.2*
- **Descripción**: Asistente de ritmo basado en tu **VAM (Velocidad de Ascensión Vertical en m/h)**.
- **Utilidad**: Te indica a qué velocidad objetiva (km/h) debes pedalear según la pendiente actual para mantener tu VAM objetivo (ej. 900 m/h) sin desfallecer.

#### 📈 4. Tendencia 3D y Rampa Máxima (`3D Gradient Trend & Max Ramp`) — *Novedad v0.2*
- **Descripción**: Anticipa los cambios de inclinación analizando la tendencia inmediata (↗️ Endureciendo, ➔ Estable, ↘️ Suavizando) e indica el % de rampa máxima alcanzada en el tramo.

---

## 🇬🇧 ENGLISH

### 1. Installation & Setup
1. Transfer the `app-debug.apk` to your Karoo device via **ADB** (`adb install -r app-debug.apk`).
2. On your Karoo, go to **Settings > Ride Profiles** and edit your preferred profile.
3. Add a new Data Field and select the **ALTGRAPH** category.

### 2. Available Data Fields (v0.2)

#### 📊 1. Altimetry Profile & Strategy
- **Description**: Displays a dynamic elevation profile divided into customizable distance blocks (100 m) with color coding based on gradient percentage.
- **Attack Alerts**: Displays a high-contrast visual alert (**ATTACK!**) when an upcoming block exceeds a 10% grade.
- **Metrics**: Distance remaining to summit, estimated time of arrival, and average remaining grade.

#### ⛰️ 2. APM Difficulty Index
- **Description**: Displays a difficulty score based on the non-linear APM formula (Altitude, Slope, Distance).
- **Utility**: Allows you to gauge exactly how much effort/difficulty remains before reaching the summit.

#### 🚴 3. ClimbPacing VAM Target — *New in v0.2*
- **Description**: Pacing assistant based on **VAM (Vertical Ascent Speed in m/h)**.
- **Utility**: Recommends the optimal target speed (km/h) for the current slope to sustain your target VAM (e.g. 900 m/h) without blowing up.

#### 📈 4. 3D Gradient Trend & Max Ramp — *New in v0.2*
- **Description**: Predicts slope changes by analyzing short-term gradient trends (↗️ Steepening, ➔ Steady, ↘️ Easing) and tracks the peak % ramp gradient in the segment.
