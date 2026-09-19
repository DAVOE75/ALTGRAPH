<p align="center">
  <img src="https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/art/logo.png" alt="ALTGRAPH Logo" width="160" />
</p>

# ALTGRAPH (v0.4.1 STABLE)

*Lire en : [Español](README.md) | [English](README_en.md) | [Français](README_fr.md) | [Italiano](README_it.md) | [Deutsch](README_de.md)*

**ALTGRAPH** est une extension professionnelle d'altimétrie et de performance de nouvelle génération pour les compteurs GPS **Hammerhead Karoo** (Karoo 2 et Karoo 3), développée par **David García Pascual** à l'aide du SDK officiel `karoo-ext`.

Elle révolutionne le concept traditionnel de l'altimétrie cycliste en intégrant une **Suite de 5 Modèles de Visualisation Altimétrique** avec sélecteur dynamique en direct, une échelle monochromatique continue de 15 niveaux, une avancée quantique en fenêtre glissante de 50 mètres, une résolution adaptative multi-échelle, l'analyse des cols de montagne, le calcul topographique (projection horizontale pure), la détection mathématique des virages en épingle (tornanti), le filtrage par catégories de POI, le commutateur d'échelle tactile, le mode paysage pivoté à 90°, l'allure **VAM** et le calcul scientifique du **Degré de Fatigue (GF)**.

---

## 🆚 ALTGRAPH vs Climber Natif du Karoo

ALTGRAPH ne remplace pas le Climber natif, mais le complète en offrant une **vue graphique complète** et personnalisable. Ses principales différences sont :
- **Résolution Adaptative et Rendu Continu** : Le profil avance physiquement par fenêtres glissantes de 50m. Les barres ne "sautent" pas de manière statique, elles glissent vers vous au rythme de votre pédalage.
- **Modèles de Visualisation** : Le Climber natif a une vue fixe ; ALTGRAPH vous offre 5 modèles révolutionnaires.
- **Échelles Tactiles Dynamiques** : Changez instantanément le zoom de l'itinéraire (200m, 1km, 10km, 20km, 50km, etc.) en touchant simplement la loupe.
- **Degré de Fatigue (GF)** : Le seul outil sur Karoo qui calcule la dureté scientifique d'un col en se basant sur le coefficient APM.

---

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

---

## 📲 Installation sur Karoo 2 et Karoo 3

Basée sur le SDK officiel `karoo-ext`, l'installation **ne nécessite pas de root** ni de modifications dangereuses du système.

**Via Sideloading (Recommandé pour Karoo 3)**
1. Téléchargez le dernier fichier `.apk` depuis la section [Releases](https://github.com/DAVOE75/ALTGRAPH/releases).
2. Envoyez-le à votre Karoo via l'application **Hammerhead Companion**.
3. Une fois installée, l'extension se mettra à jour automatiquement via le `manifest.json`.

---

## 🔮 Roadmap et Prochaines Avancées (Mode Free-Ride)

La prochaine grande étape du développement d'ALTGRAPH est la **Génération Altimétrique en Temps Réel (Mode Free-Ride)**. 

Actuellement, l'extension nécessite le chargement d'un itinéraire (GPX) pour obtenir les profils. Dès qu'Hammerhead publiera l'accès aux données d'élévation imminentes dans son SDK, **ALTGRAPH générera le graphique 3D et toutes ses métriques tactiques à la volée**, sans avoir besoin d'un itinéraire chargé. Vous pourrez explorer librement et la montagne se dessinera devant vous en temps réel.

---

## 📄 Licence et Clause de Non-responsabilité
Ce projet open-source est distribué sous la licence **MIT** - Copyright 2026 David García Pascual.
*Clause de non-responsabilité : Cette extension n'est pas affiliée, approuvée, sponsorisée ou soutenue par Hammerhead ou SRAM.*
