# ALTGRAPH (v0.4.1 STABLE)

*Lire en: [Español](README.md) | [English](README_en.md) | [Français](README_fr.md) | [Italiano](README_it.md) | [Deutsch](README_de.md)*

ALTGRAPH est une extension professionnelle de pointe pour l'altimétrie et les performances, destinée aux compteurs vélo Hammerhead Karoo (Karoo 2 et Karoo 3), développée par David García Pascual en utilisant le SDK officiel karoo-ext.

Elle révolutionne le concept traditionnel de l'altimétrie cycliste en intégrant une suite de 5 modèles de visualisation altimétrique avec un sélecteur dynamique en direct, une échelle monochromatique continue de 15 segments, une fenêtre de défilement quantique de 50 mètres, une résolution adaptative multi-échelles, une analyse des cols de montagne, un calcul topographique optionnel (projection horizontale pure), une détection mathématique des virages en épingle (tornanti), un filtrage par catégories de points d'intérêt (Villages, Points d'eau, Points de vue, Sommets), un commutateur d'échelle de zoom tactile, la typographie Google Sans / Condensed, un mode paysage plein écran pivoté à 90°, une allure VAM double avec vitesse recommandée, et un calcul scientifique du Degré de Fatigue (GF).

## 📸 Captures d'Écran Réelles du Karoo 3
- 🏔️ Altimétrie 3D et Itinéraire
- 🎨 Onglet Style et Sélecteur
- 🏔️ Onglet 3D
- 📊 Onglet 2D
- 🚴 Onglet VAM

## 🌐 Portail Web Officiel et Support Multilingue (5 Langues)
Visitez le [Portail Web Officiel ALTGRAPH](https://davoe75.github.io/ALTGRAPH/) pour la documentation interactive en espagnol, anglais, français, italien et allemand.

## 🚀 Nouveautés et Fonctionnalités Phares (v0.4.1 Stable)

### 🌟 Suite de 5 Modèles d'Altimétrie Révolutionnaires (Basculement à la volée)
Permet au cycliste de choisir entre 5 façons visuelles d'interpréter la montagne depuis la carte supérieure de l'onglet 🎨 Style :
- **🏔️ 3D Classique (Par défaut)** : Profil professionnel avec un subtil biseau 3D, dégradé monochromatique doux de 0 à 15 %, marqueurs d'altitude pivotés et capsules sombres à fort contraste avec une typographie nette pour les pourcentages.
- **🌅 Horizon Isométrique** : Perspective de cockpit 3D projetée en profondeur vers l'horizon, voie centrale en pointillés façon piste de décollage, et faisceau lumineux frontal dynamique projeté par la balise du cycliste qui s'incline selon la pente physique de la rampe imminente.
- **❄️ Oasis et Creusets Tactiques** : Détection intelligente des replats de récupération (≤3% et ≥30m) illuminés en bleu glace avec des badges dynamiques ❄️ OASIS, creusets de feu pour les rampes critiques (≥12%) avec des badges tactiques 🔥 MUR, et brume atmosphérique d'hypoxie aux altitudes ≥1400m.
- **⚡ Champ de Force et Fatigue** : Relief avec lignes de tension gravitationnelle ancrées au pixel près à la pente, onde cinétique sinusoïdale à la base (cyan en avance par inertie, cramoisi rapide quand la pente fait chuter la cadence), et ligne guide de VAM flottant doucement au-dessus du profil.
- **💎 Monolithe d'Obsidienne et Plasma** : Montagne sculptée en cristal d'obsidienne sombre à facettes (#151D2C à #04070D), facette supérieure 3D en cristal poli, noyau intérieur de plasma rayonnant, et crête supérieure en faisceau laser blanc avec lueur néon bleu clair.

### 🎨 Échelle de Dégradés Intense à Haute Visibilité
* **≤ -10.0%** : Bleu marine foncé `#041E42` (Descente prononcée).
* **-10.0% à -5.0%** : Bleu foncé `#004B87` (Descente moyenne).
* **-5.0% à -2.0%** : Bleu moyen `#0072CE` (Descente douce).
* **-2.0% à < 0.0%** : Bleu ciel clair `#41B6E6` (Faux plat descendant).
* **0.0% à 3.0%** : Vert forêt intense `#388E3C`.
* **3.0% à 5.0%** : Jaune fort `#FBC02D`.
* **5.0% à 8.0%** : Orange intense `#F57C00`.
* **8.0% à 10.0%** : Orange foncé / Rouille `#E65100`.
* **10.0% à 13.0%** : Rouge intense `#D32F2F`.
* **13.0% à 17.0%** : Grenat rouge foncé `#B71C1C`.
* **> 17.0%** : Noir jais `#000000`.

### 🔄 Fenêtre Roulante Quantique de 50m et Avance Physique
- La balise du cycliste grimpe physiquement le contour supérieur de la pente de 0 à 50m.
- À l'achèvement de chaque multiple de 50 mètres, la fenêtre se décale proprement de 50m vers la gauche sans sauts d'échelle brusques.

### 📏 Résolution Adaptative Multi-Échelles
- **≤ 500m** : Sous-blocs de 50m dans des blocs de 100m.
- **500m à 5km** : Blocs de 100m (blocs majeurs de 500m).
- **5km à 20km** : Blocs de 500m (blocs majeurs de 2km).
- **20km à 50km** : Blocs de 1km (blocs majeurs de 5km).
- **> 100km** : Sections de 10km (blocs majeurs de 20km).

### ⚡ Architecture Zéro-Allocation pour Karoo 3
- Toile de rendu sans allocation de mémoire : zéro pause du ramasse-miettes (GC) en arrière-plan.
- Réutilisation en temps réel des tampons Bitmap et Canvas.

### 📊 Triple En-tête et Télémétrie
- Lecture en direct de la PENTE ACTUELLE, de la PENTE MOYENNE de la section visible, et de la PENTE MAXIMALE.
- Détection vectorielle des virages en épingle (tornanti), filtrage des repères POI (Villages, Eau, Points de vue, Sommets), méthode topographique exacte, rythme VAM cible et Indice du Degré de Fatigue (GF).

## 📖 Manuel de l'Utilisateur
Consultez le [Manuel de l'Utilisateur (USER_MANUAL.md)](https://github.com/DAVOE75/ALTGRAPH/blob/main/USER_MANUAL.md) pour le guide complet de configuration et d'installation.

## 🛠️ Prérequis et Installation
**Prérequis de Développement**
- Android Studio : Ladybug / 2024.2.1 ou supérieur.
- JDK : Java 17 ou supérieur.
- SDK Android : compileSdk = 34, minSdk = 26.
- Appareil : Hammerhead Karoo 2 ou Karoo 3 avec ADB activé.

**📦 Compilation**
```bash
# Compiler le projet
./gradlew assembleDebug

# Installer sur le Karoo connecté par USB
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 👨‍💻 Développeur
Développé avec passion par David García Pascual.

## 📄 Licence
Ce projet est distribué sous la licence MIT.
