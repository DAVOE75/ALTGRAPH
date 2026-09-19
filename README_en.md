# ALTGRAPH (v0.4.1 STABLE)

*Read in: [Español](README.md) | [English](README_en.md) | [Français](README_fr.md) | [Italiano](README_it.md) | [Deutsch](README_de.md)*

ALTGRAPH is a next-generation professional altimetry and performance extension for Hammerhead Karoo cycling computers (Karoo 2 and Karoo 3) developed by David García Pascual using the official karoo-ext SDK.

It revolutionizes the traditional concept of cycling altimetry by incorporating a Suite of 5 Altimetry Visualization Models with a live dynamic selector, continuous 15-segment monochromatic scale, 50-meter quantum rolling window, multi-scale adaptive resolution, mountain pass analysis, optional topographic calculation (pure horizontal projection), mathematical hairpin (tornanti) detection, POI category filtering (Towns, Water, Viewpoints, Summits), touch-screen zoom scale switcher, Google Sans / Condensed typography, full-screen 90° rotated landscape mode, dual VAM pacing with recommended speed, and scientific Fatigue Grade (GF) calculation.

## 📸 Real Karoo 3 Screenshots
- 🏔️ 3D Altimetry and Itinerary
- 🎨 Style Tab and Selector
- 🏔️ 3D Tab
- 📊 2D Tab
- 🚴 VAM Tab

## 🌐 Official Web Portal & Multilingual Support (5 Languages)
Visit the [ALTGRAPH Official Web Portal](https://davoe75.github.io/ALTGRAPH/) for interactive documentation in Spanish, English, French, Italian, and German.

## 🚀 New & Highlighted Features (v0.4.1 Stable)

### 🌟 Suite of 5 Revolutionary Altimetry Models (On-the-fly Switching)
Allows the cyclist to choose between 5 visual ways to interpret the mountain from the top card of the 🎨 Style tab:
- **🏔️ 3D Classic (Default)**: Professional profile with subtle 3D bevel, smooth 0 to 15% monochromatic gradient, rotated altitude markers, and dark high-contrast capsules with crisp typography for percentages.
- **🌅 Isometric Horizon**: Cockpit 3D perspective projected deep into the horizon, dashed central runway-style lane, and dynamic front light beam projected by the cyclist's beacon that tilts according to the physical slope of the imminent ramp.
- **❄️ Tactical Oases & Crucibles**: Smart detection of recovery flats (≤3% and ≥30m) glowing in ice blue with dynamic ❄️ OASIS badges, fire crucibles for critical ramps (≥12%) with tactical 🔥 WALL badges, and hypoxia atmospheric mist at altitudes ≥1400m.
- **⚡ Force Field & Fatigue**: Relief with gravitational tension lines anchored with pixel precision to the slope, base sinusoidal kinetic wave (cyan on inertial advance, rapid crimson when the slope drops cadence), and VAM guide line floating smoothly over the profile.
- **💎 Obsidian & Plasma Monolith**: Mountain sculpted in dark faceted obsidian crystal (#151D2C to #04070D), polished crystal 3D top facet, radiant plasma inner core, and white laser beam top crest with light blue neon glow.

### 🎨 Intense High-Visibility Gradient Scale
* **≤ -10.0%**: Dark Navy Blue `#041E42` (Steep descent).
* **-10.0% to -5.0%**: Dark Blue `#004B87` (Medium descent).
* **-5.0% to -2.0%**: Medium Blue `#0072CE` (Mild descent).
* **-2.0% to < 0.0%**: Light Sky Blue `#41B6E6` (False flat descent).
* **0.0% to 3.0%**: Intense Forest Green `#388E3C`.
* **3.0% to 5.0%**: Strong Yellow `#FBC02D`.
* **5.0% to 8.0%**: Intense Orange `#F57C00`.
* **8.0% to 10.0%**: Dark Orange / Rust `#E65100`.
* **10.0% to 13.0%**: Intense Red `#D32F2F`.
* **13.0% to 17.0%**: Dark Crimson Red `#B71C1C`.
* **> 17.0%**: Jet Black `#000000`.

### 🔄 50m Quantum Rolling Window & Physical Advance
- The cyclist's beacon physically climbs the upper slope contour from 0 to 50m.
- Upon completing every 50-meter multiple, the window cleanly shifts 50m to the left without abrupt scale jumps.

### 📏 Adaptive Multi-Scale Resolution
- **≤ 500m**: 50m sub-blocks within 100m blocks.
- **500m to 5km**: 100m blocks (500m major blocks).
- **5km to 20km**: 500m blocks (2km major blocks).
- **20km to 50km**: 1km blocks (5km major blocks).
- **> 100km**: 10km sections (20km major blocks).

### ⚡ Zero-Allocation Architecture for Karoo 3
- Memory allocation-free rendering canvas: zero background Garbage Collector (GC) pauses.
- Real-time Bitmap and Canvas buffer reuse.

### 📊 Triple Header & Telemetry
- Live reading of CURRENT GRADE, AVERAGE GRADE of the visible section, and MAXIMUM GRADE.
- Vector hairpin (tornanti) detection, POI milestone filtering (Towns, Water, Viewpoints, Summits), exact topographic method, target VAM pacing, and Fatigue Grade (GF) Index.

## 📖 User Manual
Check the [User Manual (USER_MANUAL.md)](https://github.com/DAVOE75/ALTGRAPH/blob/main/USER_MANUAL.md) for the complete configuration and installation guide.

## 🛠️ Requirements & Installation
**Development Requirements**
- Android Studio: Ladybug / 2024.2.1 or higher.
- JDK: Java 17 or higher.
- Android SDK: compileSdk = 34, minSdk = 26.
- Device: Hammerhead Karoo 2 or Karoo 3 with ADB enabled.

**📦 Compilation**
```bash
# Compile project
./gradlew assembleDebug

# Install on USB connected Karoo
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 👨‍💻 Developer
Developed with passion by David García Pascual.

## 📄 License
This project is distributed under the MIT license.
