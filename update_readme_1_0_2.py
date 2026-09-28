import os
import glob
import re

def insert_after_heading(content, heading, text_to_insert):
    pattern = re.compile(f"({re.escape(heading)}\n)")
    if pattern.search(content):
        return pattern.sub(f"\\1\n{text_to_insert}\n", content)
    return content

updates = {
    "README.md": {
        "heading": "## 🚀 Novedades y Actualizaciones",
        "text": "### v1.0.2 - Parche Crítico: Rutas Circulares\n- **Seguimiento Inteligente de GPS**: Se ha reescrito el algoritmo de seguimiento (Rutas Circulares/Ida y vuelta). Ahora penaliza matemáticamente los saltos lejanos, eliminando por completo el bug donde el ciclista comenzaba a avanzar en sentido inverso hacia la salida al inicio de la ruta.\n- **Ajustes de Interfaz**: Refinamiento de la escala de kilómetros dinámica y rediseño compacto (menú desplegable) de los ajustes en la app móvil del Karoo.\n"
    },
    "README_en.md": {
        "heading": "## 🚀 News and Updates",
        "text": "### v1.0.2 - Critical Patch: Circular Routes\n- **Smart GPS Tracking**: The tracking algorithm for circular/out-and-back routes has been rewritten. It now mathematically penalizes far jumps, completely eliminating the bug where the cyclist would track backwards towards the start.\n- **UI Tweaks**: Refined dynamic kilometer scale and a more compact design (dropdowns) for the Karoo companion app settings.\n"
    },
    "README_fr.md": {
        "heading": "## 🚀 Nouveautés et Mises à jour",
        "text": "### v1.0.2 - Patch Critique : Itinéraires Circulaires\n- **Suivi GPS Intelligent** : L'algorithme de suivi pour les itinéraires circulaires/aller-retour a été réécrit. Il pénalise désormais mathématiquement les sauts éloignés, éliminant totalement le bug où le cycliste reculait vers le point de départ.\n- **Ajustements de l'Interface** : Affinement de l'échelle kilométrique dynamique et design plus compact (menus déroulants) des paramètres de l'application compagnon.\n"
    },
    "README_it.md": {
        "heading": "## 🚀 Novità e Aggiornamenti",
        "text": "### v1.0.2 - Patch Critica: Percorsi Circolari\n- **Tracciamento GPS Intelligente**: L'algoritmo di tracciamento per percorsi circolari/andata e ritorno è stato riscritto. Ora penalizza matematicamente i salti lontani, eliminando completamente il bug in cui il ciclista tornava indietro verso la partenza.\n- **Modifiche UI**: Affinamento della scala chilometrica dinamica e design più compatto (menu a tendina) delle impostazioni dell'app companion.\n"
    },
    "README_de.md": {
        "heading": "## 🚀 Neuigkeiten und Updates",
        "text": "### v1.0.2 - Kritischer Patch: Rundkurse\n- **Intelligentes GPS-Tracking**: Der Tracking-Algorithmus für Rundkurse/Hin- und Rückwege wurde neu geschrieben. Er bestraft nun mathematisch weite Sprünge und beseitigt vollständig den Fehler, bei dem der Radfahrer rückwärts zum Start verfolgt wurde.\n- **UI-Anpassungen**: Verfeinerte dynamische Kilometerskala und kompakteres Design (Dropdown-Menüs) für die Einstellungen der Companion-App.\n"
    },
    "README_pt.md": {
        "heading": "## 🚀 Novidades e Atualizações",
        "text": "### v1.0.2 - Patch Crítico: Rotas Circulares\n- **Rastreamento GPS Inteligente**: O algoritmo de rastreamento para rotas circulares/ida e volta foi reescrito. Agora penaliza matematicamente saltos distantes, eliminando completamente o bug onde o ciclista recuava em direção à largada.\n- **Ajustes de UI**: Escala quilométrica dinâmica refinada e design mais compacto (menus suspensos) nas configurações do app móvel.\n"
    }
}

for filename, info in updates.items():
    filepath = f"c:/ALTGRAPH/{filename}"
    if os.path.exists(filepath):
        with open(filepath, "r", encoding="utf-8") as f:
            content = f.read()
        
        # Don't add if already there
        if "### v1.0.2" not in content:
            new_content = insert_after_heading(content, info["heading"], info["text"])
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(new_content)
            print(f"Updated {filename}")
