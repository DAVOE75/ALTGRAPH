<p align="center">
  <img src="art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.2)

**ALTGRAPH** es una extensión avanzada de altimetría y rendimiento para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3) desarrollada con el SDK oficial `karoo-ext`.

Proporciona análisis dinámico de puertos de montaña, vista de perfil 3D en relieve con itinerario, alertas de ataque en rampas duras, estimación de ritmo **VAM** y el cálculo del **Grado de Fatiga (GF)** basado en un modelo científico de dureza, tipo de asfalto y pendiente máxima.

---

## 🌐 Soporte Multilingüe (English / Español)
La extensión detecta automáticamente el idioma de tu sistema Karoo:
* **Inglés** (idioma predeterminado)
* **Español**

---

## 🚀 Características y Campos de Datos (v0.2)

* 🏔️ **Altimetría 3D y Recorrido (`altimetria_3d`) — *Novedad v0.2***:
  * Visualización del perfil e itinerario en perspectiva isométrica 3D con relieve extruido.
  * Cinta de color con inclinaciones en tiempo real y marcador de posición 3D (*Beacon*).

* 📊 **Estratega de Altimetría (`altimetria_graph`)**:
  * Visualización del perfil por bloques ajustables de distancia (50m, 100m, 250m, 500m) con código de colores según dureza.
  * Alertas visuales de ataque (**¡ATACA! / ATTACK!**) al detectar rampas >10%.
  * Distancia restante a la cima, tiempo estimado y pendiente media restante.

* ⛰️ **Índice Grado de Fatiga GF (`fatigue_grade`)**:
  * Cálculo científico de la dureza acumulada de la subida restante basado en la fórmula:
    $$\text{GF} = \sum \text{DU}^* + \text{TA} + \left(\frac{\text{PMx}}{5}\right)$$
  * Ponderación según porcentaje de pendiente ($\text{DU}^*$), tipo de asfalto ($\text{TA}$) y rampa máxima ($\text{PMx}$).
  * Clasificación automática por categorías (5ª Cat, 4ª Cat, 3ª Cat, 2ª Cat, 1ª Cat, Especial HC).

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

---

## 📦 Compilación

```bash
# Compilar proyecto
./gradlew assembleDebug

# Instalar en Karoo conectado por USB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 Licencia

Este proyecto se distribuye bajo la licencia **MIT**.
