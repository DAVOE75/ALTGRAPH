<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.2.3)

**ALTGRAPH** es una extensión avanzada de altimetría y rendimiento para ciclocomputadores **Hammerhead Karoo** (Karoo 2 y Karoo 3) desarrollada por **David García Pascual** con el SDK oficial `karoo-ext`.

Proporciona análisis dinámico de puertos de montaña, gráficos 3D en perspectiva isométrica con extrusión de paredes de montaña, sub-seccionado de micro-bloques de 50m dentro de cada kilómetro, selección de fuentes tipográficas Open-Source (Google Sans / Condensed), modo apaisado rotado a 90° a pantalla completa, globo flotante 3D de pendiente instantánea, delimitación personalizada de rango de rampas, ritmo **VAM** dual con velocidad recomendada y cálculo científico del **Grado de Fatiga (GF)**.

---

## 📸 Capturas en Pantalla Real de Karoo 3

| 🏔️ Altimetría 3D e Itinerario | 🎨 Pestaña Estilo | 🏔️ Pestaña 3D |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="Altimetría 3D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="Panel Estilo" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_3d.png" width="220" alt="Panel 3D" /> |

| 📊 Pestaña 2D | 🚴 Pestaña VAM |
| :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_2d.png" width="220" alt="Panel 2D" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_vam.png" width="220" alt="Panel VAM" /> |

---

## 🌐 Portal Web Oficial y Soporte Multilingüe
Visita el **[Portal Web Oficial de ALTGRAPH (davoe75.github.io/ALTGRAPH)](https://davoe75.github.io/ALTGRAPH/)** para la documentación interactiva completa en español e inglés.

---

## 🚀 Novedades y Características (v0.2.3)

* 🔤 **Desplegable de Tipo de Letra (Google Sans / Condensed)**:
  * Selección de fuente Open-Source directamente desde el panel de control de la app:
    * **`Condensed (Estrecha y Alta)`**: Fuente condensada de Google/Android que dibuja números estilizados, estrechos y altos para ver las cifras de % y métricas en tamaño gigante de un vistazo.
    * **`Sans-Serif (Estándar)`**: Fuente sans-serif estándar de Android.
    * **`Sans Medium`**: Versión con trazo de peso medio.
    * **`Sans Black (Extra Gruesa)`**: Versión con trazo pesado de alta densidad.
    * **`Monospace Digital`**: Dígitos monospaciados tipo marcador digital.

* 🔄 **Modo Apaisado Rotado 90° (Landscape Mode)**:
  * Opción en el panel para girar la gráfica 90° a la derecha en el sentido de las agujas del reloj (`Sí / No`).
  * Expande el lienzo de $480\text{px}$ a un **ancho apaisado gigante de $800\text{px}$**, liberando la compresión de píxeles y permitiendo que la montaña 3D y la tipografía luzcan con el máximo tamaño y limpieza visual.

* 🏔️ **Sub-Seccionado de Micro-Bloques de 50m dentro de cada Kilómetro**:
  * Al configurar la vista por kilómetros ($1\text{km}$ a $10\text{km}$), la montaña 3D no traza una línea recta plana, sino que divide cada kilómetro en **20 micro-bloques de 50m** con sus colores independientes (azul descensos, verde llanos, naranja repechos, rojo rampas).
  * Refleja de forma continua los **descansillos, llanos y rampas duras reales** dentro de cada kilómetro.

* 📍 **Globo Flotante 3D de Pendiente Actual sobre el Ciclista**:
  * Cápsula flotante **`📍 7.2%`** unida por un puntero al marcador del ciclista (*Beacon*) que te acompaña sobre el relieve 3D con el color dinámico de la rampa instantánea.

* 🚩 **Rango Personalizado de Rampas Duras Delimitado**:
  * Ajuste de **Pendiente Mínima** (por defecto $10\%$) y **Pendiente Máxima** (por defecto $15\%$) para señalar con flecha roja las 2 o 3 rampas más duras contenidas en ese intervalo.

* 📏 **Anticipación Progresiva 3D (200m a 10 km)**:
  * Secuencia de anticipación de $50\text{m}$ en $50\text{m}$ hasta $500\text{m}$, salto a **`1 km`** y escala de $1\text{km}$ en $1\text{km}$ hasta **`10 km`**.

* 📊 **Estratega de Altimetría (`altimetria_graph`)**:
  * Perfil por bloques ajustables de distancia (50m, 100m, 250m, 500m, 1km) con **número de tramos visibles personalizable (1 a 10, por defecto 5)**.
  * Alertas visuales de ataque (**¡ATACA! / ATTACK!**) al detectar rampas >10%.

* ⛰️ **Índice Grado de Fatiga GF (`fatigue_grade`)**:
  * Cálculo científico de la dureza acumulada de la subida restante basándose en el modelo científico:
    $$\text{GF} = \sum \text{DU}^* + \text{TA} + \left(\frac{\text{PMx}}{5}\right)$$

* 🚴 **Ritmo VAM Objetivo (`climb_pacing`) — *Opción C Dual***:
  * Asistente de VAM instantánea (`850 m/h`) junto a la velocidad objetivo requerida (`12.5 km/h`) en la pendiente actual.

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
