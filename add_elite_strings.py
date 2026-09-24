import os
import re

translations = {
    'values': { # Default (English)
        'elite_status_unlocked': 'STATUS: UNLOCKED',
        'elite_status_locked': 'STATUS: LOCKED (Requires License Key)',
        'elite_hint_license': 'Enter License Key (e.g. ALTGRAPH-ELITE-2026)',
        'elite_btn_activate': 'ACTIVATE',
        'elite_strava_segments': 'Strava Live Segments 3D (Beta)',
        'elite_weather_overlay': 'Wind & Weather Overlay',
        'elite_toast_unlocked': 'Elite Features Unlocked!',
        'elite_toast_invalid': 'Invalid License Key'
    },
    'values-es': {
        'elite_status_unlocked': 'ESTADO: DESBLOQUEADO',
        'elite_status_locked': 'ESTADO: BLOQUEADO (Requiere Licencia)',
        'elite_hint_license': 'Introduzca Licencia (ej. ALTGRAPH-ELITE-2026)',
        'elite_btn_activate': 'ACTIVAR',
        'elite_strava_segments': 'Strava Live Segments 3D (Beta)',
        'elite_weather_overlay': 'Capa de Viento y Clima',
        'elite_toast_unlocked': '¡Funciones Elite Desbloqueadas!',
        'elite_toast_invalid': 'Licencia Inválida'
    },
    'values-fr': {
        'elite_status_unlocked': 'STATUT : DÉBLOQUÉ',
        'elite_status_locked': 'STATUT : BLOQUÉ (Licence requise)',
        'elite_hint_license': 'Entrez la licence (ex: ALTGRAPH-ELITE-2026)',
        'elite_btn_activate': 'ACTIVER',
        'elite_strava_segments': 'Strava Live Segments 3D (Bêta)',
        'elite_weather_overlay': 'Calque Vent & Météo',
        'elite_toast_unlocked': 'Fonctions Elite débloquées !',
        'elite_toast_invalid': 'Licence invalide'
    },
    'values-it': {
        'elite_status_unlocked': 'STATO: SBLOCCATO',
        'elite_status_locked': 'STATO: BLOCCATO (Richiede licenza)',
        'elite_hint_license': 'Inserisci Licenza (es. ALTGRAPH-ELITE-2026)',
        'elite_btn_activate': 'ATTIVA',
        'elite_strava_segments': 'Strava Live Segments 3D (Beta)',
        'elite_weather_overlay': 'Livello Vento e Meteo',
        'elite_toast_unlocked': 'Funzioni Elite Sbloccate!',
        'elite_toast_invalid': 'Licenza non valida'
    },
    'values-de': {
        'elite_status_unlocked': 'STATUS: ENTSPERRT',
        'elite_status_locked': 'STATUS: GESPERRT (Lizenz erforderlich)',
        'elite_hint_license': 'Lizenz eingeben (z.B. ALTGRAPH-ELITE-2026)',
        'elite_btn_activate': 'AKTIVIEREN',
        'elite_strava_segments': 'Strava Live Segments 3D (Beta)',
        'elite_weather_overlay': 'Wind- & Wetter-Overlay',
        'elite_toast_unlocked': 'Elite-Funktionen entsperrt!',
        'elite_toast_invalid': 'Ungültige Lizenz'
    },
    'values-pt': {
        'elite_status_unlocked': 'ESTADO: DESBLOQUEADO',
        'elite_status_locked': 'ESTADO: BLOQUEADO (Requer Licença)',
        'elite_hint_license': 'Inserir Licença (ex. ALTGRAPH-ELITE-2026)',
        'elite_btn_activate': 'ATIVAR',
        'elite_strava_segments': 'Strava Live Segments 3D (Beta)',
        'elite_weather_overlay': 'Camada de Vento e Clima',
        'elite_toast_unlocked': 'Recursos Elite Desbloqueados!',
        'elite_toast_invalid': 'Licença Inválida'
    }
}

base_path = r'C:\ALTGRAPH\app\src\main\res'

for folder, strings in translations.items():
    file_path = os.path.join(base_path, folder, 'strings.xml')
    if os.path.exists(file_path):
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        # Append strings before </resources>
        append_str = ""
        for k, v in strings.items():
            if f'name="{k}"' not in content:
                # Escape single quotes
                escaped_v = v.replace("'", r"\'")
                append_str += f'    <string name="{k}">{escaped_v}</string>\n'
                
        if append_str:
            content = content.replace('</resources>', append_str + '</resources>')
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
        print(f"Updated {file_path}")
