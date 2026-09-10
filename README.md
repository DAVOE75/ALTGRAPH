<p align="center">
  <img src="art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.2.2)

**ALTGRAPH** es una extensión avanzada de altimetría y rendimiento para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3) desarrollada por **David García Pascual** con el SDK oficial `karoo-ext`.

Proporciona análisis dinámico de puertos de montaña, gráficos 3D en perspectiva isométrica con extrusión de paredes de montaña, cotas verticales a 90°, etiquetas de % serigrafiadas en cada bloque sin recuadros, indicadores de rampas duras (≥10%), alertas de ataque, ritmo **VAM** dual con velocidad recomendada y cálculo científico del **Grado de Fatiga (GF)**.

---

## 🌐 Soporte Multilingüe (English / Español)
La extensión detecta automáticamente el idioma de tu sistema Karoo:
* **Inglés** (idioma predeterminado)
* **Español**

---

## 🚀 Características y Campos de Datos (v0.2.2)

* 🏔️ **Altimetría 3D (`altimetria_3d`)**:
  * Perfil en perspectiva isométrica 3D ampliada a pantalla completa con relieve extruido y sombras en degradado.
  * Porcentajes del **% de inclinación** serigrafiados directamente sobre cada bloque con **escalado de fuente personalizable** (`Normal`, `Grande`, `Extra Gr.`).
  * Cotas de altitud en metros rotadas a 90° corriendo en paralelo a las líneas verticales de separación de tramo (conmutador opcional `Sí / No`).
  * Flechas señalizadoras e indicadores de rampas duras (≥10%) exclusivas para subidas (conmutador opcional `Sí / No`).
  * Distancia de anticipación en el Eje X configurable desde **200 m a 500 m** (en pasos de 50m).

* 📊 **Estratega de Altimetría (`altimetria_graph`)**:
  * Visualización del perfil por bloques ajustables de distancia (50m, 100m, 250m, 500m) con **número de tramos visibles personalizable (1 a 10, por defecto 5)**.
  * Etiquetas del % de inclinación serigrafiadas dentro de cada bloque.
  * Alertas visuales de ataque (**¡ATACA! / ATTACK!**) al detectar rampas >10%.
  * Distancia restante a la cima, tiempo estimado y pendiente media restante.

* ⛰️ **Índice Grado de Fatiga GF (`fatigue_grade`)**:
  * Cálculo científico de la dureza acumulada de la subida restante basado en la fórmula:
    $$\text{GF} = \sum \text{DU}^* + \text{TA} + \left(\frac{\text{PMx}}{5}\right)$$
  * Ponderación según porcentaje de pendiente ($\text{DU}^*$), tipo de asfalto ($\text{TA}$) y rampa máxima ($\text{PMx}$).
  * Clasificación automática por categorías (5ª Cat, 4ª Cat, 3ª Cat, 2ª Cat, 1ª Cat, Especial HC).

* 🚴 **Ritmo VAM Objetivo (`climb_pacing`) — *Opción C Dual***:
  * Asistente de ritmo basado en **VAM (m/h)**.
  * Muestra la VAM instantánea (`850 m/h`) junto a la velocidad objetivo requerida (`12.5 km/h`) en la pendiente actual y la insignia de estado (`🟢 EN RITMO`, `🔴 SOBREESFUERZO`, `🔵 POR DEBAJO`).

* 📈 **Tendencia 3D y Rampa Máxima (`gradient_trend`)**:
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
