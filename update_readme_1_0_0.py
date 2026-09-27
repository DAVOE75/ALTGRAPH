import os
import glob
import re

updates = {
    "README.md": """
## 🚀 NOVEDADES VERSIÓN 1.0.0: RADAR ALTIMÉTRICO Y EXPERIENCIA PRO 🚀

¡Hemos alcanzado la versión **1.0.0**! Esta actualización trae un rediseño completo de la experiencia, haciendo que el control y la visualización sean más precisos y profesionales que nunca.

### 🌟 Radar Altimétrico (Nuevo Modo de Vista 3D)
El nuevo modo **Radar Altimétrico** se superpone a tu mapa de Karoo permitiendo ver tanto la navegación pura como el relieve de lo que tienes por delante, todo integrado de manera exquisita en la misma pantalla.
<p align="center">
  <img src="art/screenshot_radar_altimetrico.png" width="400" />
</p>

### 🔧 Nuevas Funcionalidades y Mejoras:
- **Barra de Zonas de Entrenamiento**: Integración completa de la barra inferior de colores (7 zonas) basada en tus datos de potenciómetro o frecuencia cardíaca en el modo Radar Altimétrico.
- **Control Fino de Zoom (+ / -)**: Sustitución de la antigua lupa por botones dedicados de **[+]** y **[-]**. Ahora puedes bajar la escala **hasta bloques de 50 metros** saltándote el algoritmo de "Zoom Inteligente" si deseas control manual absoluto.
- **Desplazamiento Horizontal (< / >)**: Nuevos botones interactivos en la vista del perfil que permiten arrastrar (panear) la gráfica libremente a izquierda y derecha sin alterar la escala. ¡Anticípate a la subida!
- **Modo Apaisado (Landscape) Nativo**: Soporte total para Karoo montado en horizontal. Ahora los botones táctiles y el perfil se re-orientan perfectamente de acuerdo a tu campo visual, situando los controles táctiles de forma ergonómica en los bordes de la pantalla.
- **Icono del Ciclista en Tiempo Real**: La bola (posición actual del ciclista) ahora avanza fluidamente a través del perfil gráfico y de los colores de desnivel, dándote un feedback inmediato de dónde estás en la ruta.
- **Brújula Dinámica**: Ocultación inteligente de la brújula en las vistas que no son de Mapa Isométrico Global, limpiando la interfaz para mostrar solo lo relevante.
""",
    "README_en.md": """
## 🚀 WHATS NEW IN VERSION 1.0.0: ALTIMETRY RADAR & PRO EXPERIENCE 🚀

We have reached version **1.0.0**! This update brings a complete redesign of the experience, making control and visualization more precise and professional than ever.

### 🌟 Altimetry Radar (New 3D View Mode)
The new **Altimetry Radar** mode overlays your Karoo map, allowing you to see both pure navigation and the upcoming terrain relief, all exquisitely integrated into the same screen.
<p align="center">
  <img src="art/screenshot_radar_altimetrico.png" width="400" />
</p>

### 🔧 New Features and Improvements:
- **Training Zones Bar**: Full integration of the bottom colored bar (7 zones) based on your power meter or heart rate data in the Altimetry Radar mode.
- **Fine Zoom Control (+ / -)**: Replaced the old magnifying glass with dedicated **[+]** and **[-]** buttons. You can now zoom down to **50-meter blocks**, overriding the "Smart Zoom" algorithm if you want absolute manual control.
- **Horizontal Panning (< / >)**: New interactive buttons in the profile view that allow you to freely drag (pan) the graph left and right without altering the scale. Anticipate the climb!
- **Native Landscape Mode**: Full support for Karoo mounted horizontally. Now touch buttons and the profile perfectly re-orient according to your visual field, placing touch controls ergonomically at the edges of the screen.
- **Real-Time Cyclist Icon**: The ball (current cyclist position) now moves fluidly across the graphical profile and elevation colors, giving you immediate feedback on where you are on the route.
- **Dynamic Compass**: Smart hiding of the compass in views other than the Global Isometric Map, cleaning up the interface to show only what is relevant.
""",
    "README_fr.md": """
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
""",
    "README_it.md": """
## 🚀 NOVITÀ NELLA VERSIONE 1.0.0: RADAR ALTIMETRICO E ESPERIENZA PRO 🚀

Siamo arrivati alla versione **1.0.0**! Questo aggiornamento porta una riprogettazione completa dell'esperienza, rendendo il controllo e la visualizzazione più precisi e professionali che mai.

### 🌟 Radar Altimetrico (Nuova Modalità di Visualizzazione 3D)
La nuova modalità **Radar Altimetrico** si sovrappone alla mappa del Karoo, consentendoti di vedere sia la navigazione pura che il rilievo del terreno in arrivo, tutto splendidamente integrato nella stessa schermata.
<p align="center">
  <img src="art/screenshot_radar_altimetrico.png" width="400" />
</p>

### 🔧 Nuove Funzionalità e Miglioramenti:
- **Barra delle Zone di Allenamento**: Integrazione completa della barra colorata inferiore (7 zone) basata sui dati del misuratore di potenza o della frequenza cardiaca nella modalità Radar Altimetrico.
- **Controllo Preciso dello Zoom (+ / -)**: Sostituzione della vecchia lente d'ingrandimento con i pulsanti dedicati **[+]** e **[-]**. Ora puoi ingrandire fino a **blocchi da 50 metri**, bypassando l'algoritmo "Smart Zoom" se desideri il controllo manuale assoluto.
- **Scorrimento Orizzontale (< / >)**: Nuovi pulsanti interattivi nella vista del profilo che ti consentono di trascinare liberamente il grafico a sinistra e a destra senza alterare la scala. Anticipa la salita!
- **Modalità Paesaggio (Landscape) Nativa**: Pieno supporto per Karoo montato orizzontalmente. Ora i pulsanti touch e il profilo si riorientano perfettamente in base al tuo campo visivo, posizionando i controlli in modo ergonomico.
- **Icona del Ciclista in Tempo Reale**: La pallina (posizione attuale del ciclista) ora si sposta fluidamente attraverso il profilo grafico e i colori di elevazione, fornendoti un feedback immediato su dove ti trovi lungo il percorso.
- **Bussola Dinamica**: Nascondimento intelligente della bussola in visualizzazioni diverse dalla mappa isometrica globale, ripulendo l'interfaccia.
""",
    "README_de.md": """
## 🚀 NEUIGKEITEN IN VERSION 1.0.0: ALTIMETRIE-RADAR & PRO-ERLEBNIS 🚀

Wir haben die Version **1.0.0** erreicht! Dieses Update bringt ein komplettes Redesign der Erfahrung, wodurch Steuerung und Visualisierung präziser und professioneller denn je werden.

### 🌟 Altimetrie-Radar (Neuer 3D-Ansichtsmodus)
Der neue Modus **Altimetrie-Radar** legt sich über Ihre Karoo-Karte, sodass Sie sowohl die reine Navigation als auch das kommende Geländerelief sehen können, alles wunderbar auf demselben Bildschirm integriert.
<p align="center">
  <img src="art/screenshot_radar_altimetrico.png" width="400" />
</p>

### 🔧 Neue Funktionen und Verbesserungen:
- **Trainingszonen-Leiste**: Vollständige Integration der unteren farbigen Leiste (7 Zonen) basierend auf Ihren Powermeter- oder Herzfrequenzdaten im Modus Altimetrie-Radar.
- **Feine Zoom-Steuerung (+ / -)**: Die alte Lupe wurde durch dedizierte **[+]** und **[-]** Tasten ersetzt. Sie können nun auf bis zu **50-Meter-Blöcke** hineinzoomen, wobei der "Smart Zoom"-Algorithmus überschrieben wird, wenn Sie die absolute manuelle Kontrolle wünschen.
- **Horizontales Scrollen (< / >)**: Neue interaktive Schaltflächen in der Profilansicht, mit denen Sie das Diagramm frei nach links und rechts ziehen können, ohne den Maßstab zu verändern. Nehmen Sie den Anstieg vorweg!
- **Nativer Querformatmodus (Landscape)**: Volle Unterstützung für horizontal montierte Karoos. Jetzt richten sich Touch-Buttons und das Profil perfekt nach Ihrem Sichtfeld aus und platzieren die Touch-Steuerung ergonomisch an den Bildschirmrändern.
- **Echtzeit-Radfahrer-Symbol**: Die Kugel (aktuelle Position des Radfahrers) bewegt sich nun flüssig über das grafische Profil und die Höhenfarben, was Ihnen ein sofortiges Feedback darüber gibt, wo Sie sich auf der Route befinden.
- **Dynamischer Kompass**: Intelligentes Ausblenden des Kompasses in Ansichten außerhalb der globalen Isometriekarte, um die Benutzeroberfläche übersichtlicher zu gestalten.
""",
    "README_pt.md": """
## 🚀 NOVIDADES NA VERSÃO 1.0.0: RADAR ALTIMÉTRICO E EXPERIÊNCIA PRO 🚀

Chegamos à versão **1.0.0**! Esta atualização traz um redesenho completo da experiência, tornando o controle e a visualização mais precisos e profissionais do que nunca.

### 🌟 Radar Altimétrico (Novo Modo de Visualização 3D)
O novo modo **Radar Altimétrico** se sobrepõe ao mapa do seu Karoo, permitindo que você veja tanto a navegação pura quanto o relevo do terreno que está por vir, tudo de forma primorosa e integrada na mesma tela.
<p align="center">
  <img src="art/screenshot_radar_altimetrico.png" width="400" />
</p>

### 🔧 Novas Funcionalidades e Melhorias:
- **Barra de Zonas de Treinamento**: Integração completa da barra colorida inferior (7 zonas) com base nos dados do seu medidor de potência ou frequência cardíaca no modo Radar Altimétrico.
- **Controle de Zoom Preciso (+ / -)**: A antiga lupa foi substituída por botões dedicados **[+]** e **[-]**. Agora você pode aumentar o zoom para blocos de até **50 metros**, ignorando o algoritmo "Smart Zoom" se desejar um controle manual absoluto.
- **Deslocamento Horizontal (< / >)**: Novos botões interativos na visualização do perfil que permitem arrastar livremente o gráfico para a esquerda e direita sem alterar a escala. Antecipe a subida!
- **Modo Paisagem (Landscape) Nativo**: Suporte total para Karoo montado horizontalmente. Agora os botões de toque e o perfil se reorientam perfeitamente de acordo com o seu campo de visão, colocando os controles de toque de forma ergonômica nas bordas da tela.
- **Ícone de Ciclista em Tempo Real**: A bola (posição atual do ciclista) agora avança fluidamente pelo perfil gráfico e pelas cores de elevação, dando-lhe feedback imediato sobre onde você está na rota.
- **Bússola Dinâmica**: Ocultação inteligente da bússola em visualizações fora do mapa isométrico global, simplificando a interface para mostrar apenas o que é relevante.
"""
}

for file, extra_content in updates.items():
    if os.path.exists(file):
        with open(file, "r", encoding="utf-8") as f:
            content = f.read()
        
        # Insert after the main title or badges
        if "## 📖 " in content:
            # Find the first H2
            parts = content.split("## 📖 ", 1)
            new_content = parts[0] + extra_content + "\n## 📖 " + parts[1]
        elif "## " in content:
            parts = content.split("## ", 1)
            new_content = parts[0] + extra_content + "\n## " + parts[1]
        else:
            new_content = extra_content + "\n" + content
            
        with open(file, "w", encoding="utf-8") as f:
            f.write(new_content)
        print(f"Updated {file}")
