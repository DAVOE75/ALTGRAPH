<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.3.0 STABLE)

**ALTGRAPH** es una extensión de rendimiento de altimetría profesional para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3) desarrollada por **David García Pascual** utilizando el SDK oficial `karoo-ext`.

Proporciona análisis dinámico de puertos de montaña, gráficos 3D en perspectiva isométrica con volumen de montaña, cálculo topográfico opcional (proyección horizontal pura), detección matemática de curvas de herradura (tornanti), filtrado por categorías de Hitos (Pueblos, Fuentes, Miradores, Cimas), conmutador de escala de zoom al tocar la pantalla, selección de fuentes tipográficas Open-Source (Google Sans / Condensed), modo apaisado rotado a 90° a pantalla completa, ritmo **VAM** dual con velocidad recomendada y cálculo científico del **Grado de Fatiga (GF)**.

---

## 📸 Capturas en Pantalla Real de Karoo 3

| 🏔️ Altimetría 3D e Itinerario | 🎨 Pestaña Estilo | 🏔️ Pestaña 3D |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="Altimetría 3D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="Panel Estilo" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_3d.png" width="220" alt="Panel 3D" /> |

| 📊 Pestaña 2D | 🚴 Pestaña VAM |
| :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_2d.png" width="220" alt="Panel 2D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_vam.png" width="220" alt="Panel VAM" /> |

---

## 🌐 Portal Web Oficial y Soporte Multilingüe (5 Idiomas)
Visita el **[Portal Web Oficial de ALTGRAPH (davoe75.github.io/ALTGRAPH)](https://davoe75.github.io/ALTGRAPH/)** para la documentación interactiva en español, inglés, francés, italiano y alemán.

---

## 🚀 Resumen de Funciones (v0.3.0 Stable)

* 📊 **Encabezado Triple en Altimetría 3D**:
  * Lectura en vivo de **PENDIENTE ACTUAL**, **PENDIENTE MEDIA** (promedio matemático real de la sección mostrada) y **PENDIENTE MÁXIMA**.

* 🏎️ **Detección Matemática Vectorial de Curvas de Herradura (Tornanti)**:
  * Medición de ángulos en tiempo real sobre el trazado GPS. Dibuja marcas de herradura de trazo negro del 20% en el relieve.

* 📍 **Filtro de Puntos de Interés (Hitos POIs)**:
  * Selección independiente por categorías para mostrar/ocultar: 🏘️ Pueblos, 💧 Fuentes de Agua, 📸 Miradores y 📡 Cimas/Puertos.

* 📐 **Método de Cálculo Topográfico de Pendiente**:
  * Opción para calcular la pendiente % sobre la proyección horizontal del mapa, eliminando el sesgo de la hipotenusa en rampas extremas.

* 🔍 **Ciclo de Zoom Táctil en Pantalla**:
  * Al tocar la gráfica 3D en la pantalla de carrera del Karoo, alterna en vivo la escala de anticipación (`200m -> 350m -> 500m -> 1km -> 2km -> 5km -> 10km`).

* 🔤 **Desplegable de Tipo de Letra (Google Sans / Condensed)**:
  * Fuentes condensadas y sans-serif de Google para números altos y estilizados.

* 🔄 **Modo Apaisado Rotado 90° (Landscape Mode)**:
  * Expande el lienzo a **$800\text{px} \times 480\text{px}$** a pantalla completa.

* 🚴 **Ritmo VAM Objetivo (`climb_pacing`)**:
  * Asistente de VAM instantánea (`850 m/h`) junto a la velocidad objetivo requerida (`12.5 km/h`).

* ⛰️ **Índice Grado de Fatiga GF (`fatigue_grade`)**:
  * Cálculo científico de la dureza acumulada con el modelo:
    $$\text{GF} = \sum \text{DU}^* + \text{TA} + \left(\frac{\text{PMx}}{5}\right)$$

---

## 📖 Manual de Usuario
Consulta el [**Manual de Usuario (`USER_MANUAL.md`)**](USER_MANUAL.md) para ver la guía completa de configuración e instalación.

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
