import re
import json

filepath = 'c:/ALTGRAPH/docs/index.html'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

translations_to_add = {
    'es': {
        'radarBadge': '🌟 NUEVO EN v1.0.5',
        'radarTitle': '🛰️ Radar Altimétrico (Vista 3D)',
        'radarDesc': 'El nuevo modo <strong>Radar Altimétrico</strong> se superpone a tu mapa de Karoo permitiendo ver tanto la navegación pura como el relieve de lo que tienes por delante, todo integrado de manera exquisita en la misma pantalla.'
    },
    'en': {
        'radarBadge': '🌟 NEW IN v1.0.5',
        'radarTitle': '🛰️ Altimetry Radar (3D View)',
        'radarDesc': 'The new <strong>Altimetry Radar</strong> mode overlays your Karoo map, allowing you to view pure navigation alongside the 3D relief of what lies ahead, all exquisitely integrated into the same screen.'
    },
    'fr': {
        'radarBadge': '🌟 NOUVEAU DANS v1.0.5',
        'radarTitle': '🛰️ Radar Altimétrique (Vue 3D)',
        'radarDesc': 'Le nouveau mode <strong>Radar Altimétrique</strong> se superpose à votre carte Karoo, vous permettant de voir la navigation pure ainsi que le relief de ce qui vous attend, le tout magnifiquement intégré sur le même écran.'
    },
    'it': {
        'radarBadge': '🌟 NUOVO IN v1.0.5',
        'radarTitle': '🛰️ Radar Altimetrico (Vista 3D)',
        'radarDesc': 'Il nuovo <strong>Radar Altimetrico</strong> si sovrappone alla mappa del Karoo, consentendoti di vedere la navigazione pura e il rilievo 3D di ciò che ti aspetta, tutto squisitamente integrato nello stesso schermo.'
    },
    'de': {
        'radarBadge': '🌟 NEU IN v1.0.5',
        'radarTitle': '🛰️ Altimetrie-Radar (3D-Ansicht)',
        'radarDesc': 'Der neue <strong>Altimetrie-Radar</strong>-Modus überlagert Ihre Karoo-Karte, sodass Sie reine Navigation zusammen mit dem 3D-Relief der vor Ihnen liegenden Strecke sehen können – alles exquisit in denselben Bildschirm integriert.'
    },
    'pt': {
        'radarBadge': '🌟 NOVO NA v1.0.5',
        'radarTitle': '🛰️ Radar Altimétrico (Vista 3D)',
        'radarDesc': 'O novo modo <strong>Radar Altimétrico</strong> sobrepõe-se ao seu mapa Karoo, permitindo-lhe ver navegação pura em conjunto com o relevo 3D do que se avizinha, tudo de forma primorosa integrado no mesmo ecrã.'
    }
}

for lang in translations_to_add:
    target = f"{lang}: {{"
    replacement = f"{lang}: {{\n                radarBadge: `{translations_to_add[lang]['radarBadge']}`,\n                radarTitle: `{translations_to_add[lang]['radarTitle']}`,\n                radarDesc: `{translations_to_add[lang]['radarDesc']}`,"
    content = content.replace(target, replacement)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
