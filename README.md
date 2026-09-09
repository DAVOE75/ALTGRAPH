# ALTGRAPH (v0.1)

**ALTGRAPH** es una extensión para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3) desarrollada con el SDK oficial `karoo-ext`. Proporciona un análisis dinámico de altimetría en tiempo real y el cálculo de la dificultad de subidas utilizando el **Coeficiente APM (Altitud, Pendiente, Metros)**.

---

## 🚀 Características principales (v0.1)

* 📊 **Gráfico de Altimetría Dinámica**:
  * Visualización por bloques ajustables de distancia (por defecto 100m).
  * Código de colores según dureza de la pendiente.
  * Alertas visuales de ataque en tramos exigentes (>10%).
  * Indicación de distancia restante a la cima, pendiente media restante y tiempo estimado de llegada.

* ⛰️ **Coeficiente APM (`apm_score`)**:
  * Campo de datos en tiempo real que calcula la dificultad acumulada de la subida restante.
  * Basado en una tabla de ponderación no lineal según el porcentaje de pendiente (décimas de porcentaje del 0% al 30%).

* ⚡ **Integración con Karoo Extension SDK 1.1.4**:
  * Lectura eficiente de sensores mediante flujos reactivos de velocidad y elevación barométrica.
  * Compatibilidad total con el ecosistema de aplicaciones de Karoo.

---

## 🛠️ Requisitos e Instalación

### Requisitos de desarrollo
* **Android Studio**: Ladybug / 2024.2.1 o superior.
* **JDK**: Java 17 o superior.
* **Android SDK**: `compileSdk = 34`, `minSdk = 26`.
* **Dispositivo**: Hammerhead Karoo 2 o Karoo 3 con opciones de desarrollador (ADB) activadas.

### Configuración del proyecto (`local.properties`)
Dado que la librería `io.hammerhead:karoo-ext` se distribuye a través del repositorio de GitHub Packages, necesitas agregar tus credenciales en el archivo `local.properties`:

```properties
gpr.user=TU_USUARIO_GITHUB
gpr.key=TU_GITHUB_PERSONAL_ACCESS_TOKEN
```

---

## 📦 Compilación

Para compilar el archivo APK de depuración desde la terminal:

```bash
# Compilar proyecto
./gradlew assembleDebug
```

Para instalar la extensión directamente en un Karoo conectado por USB via ADB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📁 Estructura del proyecto

```
ALTGRAPH/
├── app/
│   └── src/main/
│       ├── java/com/example/altgraph/
│       │   ├── AltimetriaDataField.kt       # Gestión de eventos Karoo y cálculo de estrategia
│       │   ├── AltimetriaExtensionService.kt # Servicio Karoo Extension principal
│       │   ├── AltimetriaView.kt             # Renderizado visual y gráficos de perfil
│       │   ├── ApmCalculator.kt              # Tabla e interpolación de coeficientes APM
│       │   ├── ApmDataField.kt               # Campo de datos individual para el Coeficiente APM
│       │   └── ClimbStateManager.kt          # Estado reactivo global del análisis de subida
│       └── res/
│           └── drawable/
│               └── ic_altigraph_logo.xml    # Logotipo vectorial (Vector Drawable)
└── build.gradle.kts
```

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT**. Consulta el archivo `LICENSE` para más detalles.
