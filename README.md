<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.4.0 STABLE)

**ALTGRAPH** es una extensión de rendimiento y altimetría profesional de última generación para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3) desarrollada por **David García Pascual** utilizando el SDK oficial `karoo-ext`.

Revoluciona el concepto tradicional de altimetría ciclista incorporando una **Suite de 5 Modelos de Visualización Altimétrica** con selector dinámico en vivo, escala monocromática continua de 15 tramos, avance cuántico en ventana rodante de 50 metros, resolución adaptativa multiescala, análisis de puertos de montaña, cálculo topográfico opcional (proyección horizontal pura), detección matemática de curvas de herradura (tornanti), filtrado por categorías de Hitos (Pueblos, Fuentes, Miradores, Cimas), conmutador de escala de zoom al tocar la pantalla, tipografías Google Sans / Condensed, modo apaisado rotado a 90° a pantalla completa, ritmo **VAM** dual con velocidad recomendada y cálculo científico del **Grado de Fatiga (GF)**.

---

## 📸 Capturas en Pantalla Real de Karoo 3

| 🏔️ Altimetría 3D e Itinerario | 🎨 Pestaña Estilo y Selector | 🏔️ Pestaña 3D |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="Altimetría 3D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="Panel Estilo" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_3d.png" width="220" alt="Panel 3D" /> |

| 📊 Pestaña 2D | 🚴 Pestaña VAM |
| :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_2d.png" width="220" alt="Panel 2D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_vam.png" width="220" alt="Panel VAM" /> |

---

## 🌐 Portal Web Oficial y Soporte Multilingüe (5 Idiomas)
Visita el **[Portal Web Oficial de ALTGRAPH (davoe75.github.io/ALTGRAPH)](https://davoe75.github.io/ALTGRAPH/)** para la documentación interactiva en español, inglés, francés, italiano y alemán.

---

## 🚀 Novedades y Funciones Destacadas (v0.4.0 Stable)

### 🌟 Suite de 5 Modelos de Altimetría Revolucionarios (Conmutables al vuelo)
Permite al ciclista elegir entre 5 formas visuales de interpretar la montaña desde la tarjeta superior de la pestaña **🎨 Estilo** de la extensión:
1. **🏔️ Clásica 3D (Por defecto)**: Perfil profesional con bisel 3D sutil y estilizado, degradado monocromático suave del 0 al 15%, cotas de altitud rotadas y cápsulas oscuras de alto contraste con tipografía nítida para porcentajes.
2. **🌅 Horizonte Isométrico (`Horizonte Iso`)**: Perspectiva de cabina (*cockpit 3D*) proyectada en profundidad hacia el horizonte, carril central discontinuo tipo pista de despegue y **haz de luz frontal dinámico** proyectado por la baliza del ciclista que se inclina según la pendiente física de la rampa inminente.
3. **❄️ Oasis y Crisoles Tácticos (`Oasis Tácticos`)**: Detección inteligente de descansillos de recuperación ($\le 3\%$ y $\ge 30\text{m}$) iluminados en azul hielo con insignias dinámicas `❄️ OASIS [distancia]m`, crisoles de fuego para rampas críticas ($\ge 12\%$) con insignias tácticas `🔥 MURO [distancia]m`, y bruma atmosférica de hipoxia a altitudes $\ge 1400\text{m}$.
4. **⚡ Campo de Fuerza y Fatiga (`Campo Fuerza`)**: Relieve con líneas gravitatorias de tensión ancladas con precisión de píxel a la pendiente, onda cinética senoidal en la base (cian en avance con inercia, carmesí rápido cuando la pendiente frena la cadencia) y línea guía de ritmo ascensional (VAM) flotando suavemente sobre el perfil.
5. **💎 Monolito de Obsidiana y Plasma (`Monolito`)**: Montaña esculpida en cristal de obsidiana facetado oscuro (`#151D2C` a `#04070D`), faceta superior 3D en cristal pulido, núcleo interior de plasma radiante y cresta superior en haz láser blanco con resplandor neón celeste.

### 🎨 Escala Monocromática Continua de 15 Tramos
* **$< 0.0\%$**: Azul `#1D4ED8` (Descensos y pendientes negativas).
* **$0.0\%$ a $15.0\%$**: Transición suave de 1 en 1 en 15 tramos:
  * `0-1%`: Blanco puro `#FFFFFF`
  * `1-2%`: Crema suave `#FEF9C3`
  * `2-3%`: Amarillo muy claro `#FEF08A`
  * `3-4%`: Amarillo claro `#FDE047`
  * `4-5%`: Amarillo medio `#FACC15`
  * `5-6%`: Dorado `#EAB308`
  * `6-7%`: Amarillo anaranjado `#F59E0B`
  * `7-8%`: Naranja claro / ámbar `#FB923C`
  * `8-9%`: Naranja puro `#F97316`
  * `9-10%`: Naranja intenso `#EA580C`
  * `10-11%`: Bermellón `#E03E1A`
  * `11-12%`: Rojo anaranjado `#EA2E1A`
  * `12-13%`: Rojo vivo `#E02424`
  * `13-14%`: Rojo puro `#DC2626`
  * `14-15%`: Rojo intenso `#B91C1C`
* **$> 15.0\%$ hasta $20.0\%$**: Rojo muy intenso `#991B1B`.
* **$> 20.0\%$**: Negro azabache `#000000`.

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
