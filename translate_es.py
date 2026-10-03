import os

replacements = [
    ('WORLD TOUR PRO FEATURES', 'FUNCIONES WORLD TOUR PRO'),
    ('Wind &amp; Weather Overlay', 'Capa de Viento y Clima'),
    ('Elite Features Unlocked!', '¡Funciones Elite Desbloqueadas!'),
    ('Invalid License Key', 'Clave de Licencia Inválida'),
    ('STATUS: LOCKED (Requires License Key)', 'ESTADO: BLOQUEADO (Requiere Clave)'),
    ('STATUS: UNLOCKED', 'ESTADO: DESBLOQUEADO'),
    ('Enter License Key (e.g. ALTGRAPH-ELITE-2026)', 'Ingresa Clave de Licencia (ej. ALTGRAPH-ELITE-2026)'),
    ('ACTIVATE', 'ACTIVAR'),
    ('🎯 Radar ELITE Features', '🎯 Funciones Radar ELITE'),
    ('Strava Live Segments 3D (Beta)', 'Segmentos Strava Live 3D (Beta)')
]

path = 'c:/ALTGRAPH/app/src/main/res/values-es/strings.xml'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

for old, new in replacements:
    content = content.replace(f">{old}<", f">{new}<")

with open(path, 'w', encoding='utf-8') as f:
    f.write(content)
