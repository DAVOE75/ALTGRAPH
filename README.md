# ALTGRAPH (v0.2)

**ALTGRAPH** es una extensión avanzada de altimetría y rendimiento para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3) desarrollada con el SDK oficial `karoo-ext`.

Proporciona análisis dinámico de puertos de montaña, alertas de ataque en rampas duras, estimación de ritmo **VAM** y el cálculo de la dificultad de subidas mediante el **Coeficiente APM (Altitud, Pendiente, Metros)**.

---

## 🌐 Soporte Multilingüe (English / Español)
La extensión detecta automáticamente el idioma de tu sistema Karoo:
* **Inglés** (idioma predeterminado)
* **Español**

---

## 🚀 Características y Campos de Datos (v0.2)

* 📊 **Estratega de Altimetría (`altimetria_graph`)**:
  * Visualización del perfil por bloques ajustables de distancia (100 m) con código de colores según dureza.
  * Alertas visuales de ataque (**¡ATACA! / ATTACK!**) al detectar rampas >10%.
  * Distancia restante a la cima, tiempo estimado y pendiente media restante.

* ⛰️ **Índice de Dificultad APM (`apm_score`)**:
  * Campo de datos que calcula la puntuación acumulada de dificultad que te queda por superar antes de coronar.

* 🚴 **Ritmo VAM Objetivo (`climb_pacing`) — *Novedad v0.2***:
  * Asistente de ritmo basado en **VAM (m/h)**.
  * Calcula la velocidad objetivo (km/h) requerida en la pendiente actual para mantener tu ritmo de ascensión óptimo.

* 📈 **Tendencia 3D y Rampa Máxima (`gradient_trend`) — *Novedad v0.2***:
  * Indicador de tendencia en tiempo real (↗️ Endureciendo, ➔ Estable, ↘️ Suavizando) anticipándose al retraso del sensor barométrico.
  * Registro de la pendiente máxima (% max) alcanzada en el tramo.

---

## 📖 Manual de Usuario
Consulta el [**Manual de Usuario (`USER_MANUAL.md`)**](USER_MANUAL.md) para ver la guía completa de configuración e instalación en español e inglés.

---

## 🛠️ Requisitos e Instalación

### Requisitos de desarrollo
* **Android Studio**: Ladybug / 2024.2.1 o superior.
* **JDK**: Java 17 o superior.
* **Android SDK**: `compileSdk = 34`, `minSdk = 26`.
* **Dispositivo**: Hammerhead Karoo 2 o Karoo 3 con ADB activado.

### Configuración del proyecto (`local.properties`)
```properties
gpr.user=TU_USUARIO_GITHUB
gpr.key=TU_GITHUB_PERSONAL_ACCESS_TOKEN
```

---

## 📦 Compilación

```bash
# Compilar proyecto
./gradlew assembleDebug

# Instalar en Karoo conectado por USB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📁 Estructura del proyecto

```
ALTGRAPH/
├── app/
│   └── src/main/
│       ├── java/com/example/altgraph/
│       │   ├── AltimetriaExtensionService.kt # Servicio principal Karoo Extension (v0.2)
│       │   ├── AltimetriaGraphDataField.kt    # Campo gráfico de altimetría
│       │   ├── AltimetriaStrategyCalculator.kt# Algoritmo de cálculo de estrategia de puerto
│       │   ├── AltimetriaView.kt             # Renderizado optimizado (Zero-Allocation onDraw)
│       │   ├── ApmCalculator.kt              # Tabla de coeficientes APM
│       │   ├── ApmDataField.kt               # Campo de datos del Coeficiente APM
│       │   ├── ClimbPacingCalculator.kt      # Asistente de ritmo VAM (m/h)
│       │   ├── ClimbPacingDataField.kt       # Campo de datos ClimbPacing
│       │   ├── ClimbStateManager.kt          # Estado reactivo global
│       │   ├── GradientTrendDataField.kt     # Campo de datos de tendencia de pendiente
│       │   └── GradientTrendTracker.kt       # Algoritmo de detección de tendencia 3D
│       └── res/
│           ├── drawable/
│           │   └── ic_altigraph_logo.xml     # Logotipo vectorial de la aplicación
│           ├── values/
│           │   └── strings.xml               # Textos en Inglés (por defecto)
│           └── values-es/
│               └── strings.xml               # Textos en Español
├── USER_MANUAL.md                            # Manual de usuario (ES/EN)
└── README.md
```

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT**.
