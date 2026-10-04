<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>
# ALTGRAPH (v0.5.0 STABLE)
<p>Lire en : <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README.md">🇪🇸 Español</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_en.md">🇬🇧 English</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_fr.md">🇫🇷 Français</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_it.md">🇮🇹 Italiano</a> | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_de.md">🇩🇪 Deutsch</a></p>
**ALTGRAPH** est une extension professionnelle d'altimétrie et de performance de nouvelle génération pour les compteurs GPS **Hammerhead Karoo** (Karoo 2 et Karoo 3), développée par **David García Pascual** à l'aide du SDK officiel `karoo-ext`.
Elle révolutionne le concept traditionnel de l'altimétrie cycliste en intégrant une **Suite de 6 Modèles de Visualisation Altimétrique** avec sélecteur dynamique en direct, une échelle monochromatique continue de 15 niveaux, une avancée quantique en fenêtre glissante de 50 mètres, une résolution adaptative multi-échelle, l'analyse des cols de montagne, le calcul topographique (projection horizontale pure), la détection mathématique des virages en épingle (tornanti), le filtrage par catégories de POI, le commutateur d'échelle tactile, le mode paysage pivoté à 90°, l'allure **VAM** et le calcul scientifique du **Degré de Fatigue (GF)**.
---



## 🐛 VERSION 1.0.2: CORRECTIONS CRITIQUES 🐛

- **Correction de Bug sur les Itinéraires Circulaires/Aller-Retour**: Résolution d'un problème où le GPS s'accrochait de manière incorrecte à la fin de l'itinéraire dès le départ, provoquant le suivi à l'envers du graphique ("bug snap-to-end").
- **Amélioration de l'Interface (Radar)**: L'indicateur de pente maximale affiche désormais une flèche de couleur (Rouge pour la montée, Bleu Foncé pour la descente) au-dessus du pourcentage, améliorant considérablement la lisibilité et le style.

## 🚀 NOUVEAUTÉS DE LA VERSION 1.0.0: RADAR ALTIMÉTRIQUE ET EXPÉRIENCE PRO 🚀

Nous avons atteint la version **1.0.0** ! Cette mise à jour apporte une refonte complète de l'expérience, rendant le contrôle et la visualisation plus précis et professionnels que jamais.

### 🌟 Radar Altimétrique (Nouveau Mode de Vue 3D)
Le nouveau mode **Radar Altimétrique** se superpose à votre carte Karoo, vous permettant de voir à la fois la navigation pure et le relief à venir, le tout magnifiquement intégré sur le même écran.
<p align="center">
  <img src="art/screenshot_radar_altimetrico.png" width="400" />
</p>

### 🔧 Nouvelles Fonctionnalités et Améliorations :
- **Barre des Zones d'Entraînement** : Intégration complète de la barre de couleur inférieure (7 zones) basée sur les données de votre capteur de puissance ou de votre fréquence cardiaque dans le mode Radar Altimétrique.
- **Contrôle Précis du Zoom (+ / -)** : Remplacement de l'ancienne loupe par des boutons dédiés **[+]** et **[-]**. Vous pouvez désormais zoomer jusqu'à des **blocs de 50 mètres**, en contournant l'algorithme "Smart Zoom" si vous souhaitez un contrôle manuel absolu.
- **Défilement Horizontal (< / >)** : Nouveaux boutons interactifs dans la vue de profil qui vous permettent de faire glisser librement le graphique de gauche à droite sans modifier l'échelle. Anticipez la montée !
- **Mode Paysage (Landscape) Natif** : Prise en charge complète du Karoo monté horizontalement. Désormais, les boutons tactiles et le profil se réorientent parfaitement en fonction de votre champ visuel, plaçant les commandes de manière ergonomique.
- **Icône du Cycliste en Temps Réel** : La balle (position actuelle du cycliste) se déplace désormais de manière fluide à travers le profil graphique et les couleurs d'élévation, vous donnant un retour immédiat sur votre position.
- **Boussole Dynamique** : Masquage intelligent de la boussole dans les vues autres que la carte isométrique globale, épurant l'interface.

## 📸 Capturas en Pantalla Real de Karoo 3

| 🎨 Onglet Style | 🎛️ Menu Sélection | 📊 Tableau de Bord |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_settings_estilo.png" width="220" alt="🎨 Onglet Style" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_data_selection.png" width="220" alt="🎛️ Menu Sélection" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_dashboard_completo.png" width="220" alt="📊 Tableau de Bord" /> |

| 🏔️ Altimétrie 3D (200m) | 🏔️ Altimétrie 3D (1km) | 🏔️ Altimétrie 3D (10km) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile.png" width="220" alt="🏔️ Altimétrie 3D (200m)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_1km.png" width="220" alt="🏔️ Altimétrie 3D (1km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_10km.png" width="220" alt="🏔️ Altimétrie 3D (10km)" /> |

| 🏔️ Altimétrie 3D (20km) | 🏔️ Altimétrie 3D (200km) | 🏔️ Visionneuse - 3ème Cat |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_20km.png" width="220" alt="🏔️ Altimétrie 3D (20km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_200km.png" width="220" alt="🏔️ Altimétrie 3D (200km)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer.png" width="220" alt="🏔️ Visionneuse - 3ème Cat" /> |

| 🏔️ Visionneuse - HC | 🏔️ Visionneuse (Isométrique) <div align="center">

| 🗺️ GPS Isométrique (Global) |
| :---: | :---: | :---: |
| <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_especial.png" width="220" alt="🏔️ Visionneuse - HC" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_climb_viewer_iso.png" width="220" alt="🏔️ Visionneuse (Isométrique)" /> | <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/screenshot_3d_profile_gps.png" width="220" alt="🗺️ GPS Isométrique (Global)" /> |

</div>

## 🆚 ALTGRAPH vs Climber Natif du Karoo
ALTGRAPH ne remplace pas le Climber natif, mais le complète en offrant une **vue graphique complète** et personnalisable. Ses principales différences sont :
- **Résolution Adaptative et Rendu Continu** : Le profil avance physiquement par fenêtres glissantes de 50m. Les barres ne "sautent" pas de manière statique, elles glissent vers vous au rythme de votre pédalage.
- **Modèles de Visualisation** : Le Climber natif a une vue fixe ; ALTGRAPH vous offre 5 modèles révolutionnaires.
- **Échelles Tactiles Dynamiques** : Changez instantanément le zoom de l'itinéraire (200m, 1km, 10km, 20km, 50km, etc.) en touchant simplement la loupe.
- **Degré de Fatigue (GF)** : Le seul outil sur Karoo qui calcule la dureté scientifique d'un col en se basant sur le coefficient APM.

## ⚙️ Caractéristiques et Fonctions en Détail
### 📈 Champs de Données Exclusifs (Tableau de Bord Tactique)
- **🧠 Stratège d'Altimétrie** : Affiche un bloc visuel intelligent résumant les prochains kilomètres.
- **⚡ Degré de Fatigue (GF)** : Moteur scientifique basé sur le coefficient APM évaluant la dureté réelle du col en direct.
- **🚀 Allure VAM Cible** : Calcule la pente exacte sous vos roues et vous dicte à quelle **vitesse (km/h)** vous devez rouler pour atteindre votre objectif au sommet.
- **📐 Tendance 3D et Rampe Max** : Vous avertit si la pente se durcit ou s'adoucit avant même que vos jambes ne le sentent.
### 🗺️ Navigation et Topographie Avancée
- **Icônes Topographiques Officielles** : Identification des Sommets, Cols (Map-Pin), Villages et Fontaines.
- **Détection Mathématique des Virages** : L'algorithme détecte les virages serrés et vous avertit graphiquement.
- **Calcul Topographique Pur** : Calculez les distances basées sur la projection horizontale pour une rigueur absolue.
- **Thèmes Sombres à Haut Contraste** : Interface conçue pour une lisibilité instantanée en plein soleil.

## 📲 Installation sur Karoo 2 et Karoo 3
Basée sur le SDK officiel `karoo-ext`, l'installation **ne nécessite pas de root** ni de modifications dangereuses du système.
**Via Sideloading (Recommandé pour Karoo 3)**
1. Téléchargez le dernier fichier `.apk` depuis la section [Releases](https://github.com/DAVOE75/ALTGRAPH/releases).
2. Envoyez-le à votre Karoo via l'application **Hammerhead Companion**.
3. Une fois installée, l'extension se mettra à jour automatiquement via le `manifest.json`.

#### 🔮 Roadmap et Prochaines Avancées (Mode Free-Ride)

La prochaine grande étape du développement d'ALTGRAPH est la **Génération Altimétrique en Temps Réel (Mode Free-Ride)**. 
Actuellement, l'extension nécessite le chargement d'un itinéraire (GPX) pour obtenir les profils. Dès qu'Hammerhead publiera l'accès aux données d'élévation imminentes dans son SDK, **ALTGRAPH générera le graphique 3D et toutes ses métriques tactiques à la volée**, sans avoir besoin d'un itinéraire chargé. Vous pourrez explorer librement et la montagne se dessinera devant vous en temps réel.

#### 📄 Licence et Clause de Non-responsabilité

Ce projet open-source est distribué sous la licence **MIT** - Copyright 2026 David García Pascual.
*Clause de non-responsabilité : Cette extension n'est pas affiliée, approuvée, sponsorisée ou soutenue par Hammerhead ou SRAM.*
