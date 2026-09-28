import os
import glob
import re

updates = {
    "README.md": """
## 🐛 VERSIÓN 1.0.1: CORRECCIONES CRÍTICAS 🐛

- **Corrección de Bug en Rutas Circulares/Ida y Vuelta**: Solucionado un problema donde el GPS se anclaba erróneamente al final de la ruta al inicio de la misma, haciendo que la gráfica avanzara en retroceso ("snap-to-end bug").
- **Mejora en la Interfaz (Radar)**: El indicador de pendiente máxima ahora muestra una flecha de color (Roja para ascenso, Azul Oscuro para descenso) sobre el porcentaje de desnivel, mejorando su visibilidad y estilo.
""",
    "README_en.md": """
## 🐛 VERSION 1.0.1: CRITICAL FIXES 🐛

- **Circular/Out-and-Back Routes Bug Fix**: Resolved an issue where the GPS incorrectly snapped to the end of the route right at the start, causing the graph to track backwards ("snap-to-end bug").
- **UI Improvement (Radar)**: The maximum grade indicator now displays a colored arrow (Red for ascent, Dark Blue for descent) above the grade percentage, significantly improving visibility and style.
""",
    "README_fr.md": """
## 🐛 VERSION 1.0.1: CORRECTIONS CRITIQUES 🐛

- **Correction de Bug sur les Itinéraires Circulaires/Aller-Retour**: Résolution d'un problème où le GPS s'accrochait de manière incorrecte à la fin de l'itinéraire dès le départ, provoquant le suivi à l'envers du graphique ("bug snap-to-end").
- **Amélioration de l'Interface (Radar)**: L'indicateur de pente maximale affiche désormais une flèche de couleur (Rouge pour la montée, Bleu Foncé pour la descente) au-dessus du pourcentage, améliorant considérablement la lisibilité et le style.
""",
    "README_it.md": """
## 🐛 VERSIONE 1.0.1: CORREZIONI CRITICHE 🐛

- **Correzione Bug su Percorsi Circolari/Andata e Ritorno**: Risolto un problema in cui il GPS si agganciava erroneamente alla fine del percorso fin dall'inizio, facendo sì che il grafico procedesse a ritroso ("bug snap-to-end").
- **Miglioramento dell'Interfaccia (Radar)**: L'indicatore di pendenza massima ora mostra una freccia colorata (Rossa per salita, Blu Scuro per discesa) sopra la percentuale, migliorandone visibilità e stile.
""",
    "README_de.md": """
## 🐛 VERSION 1.0.1: KRITISCHE KORREKTUREN 🐛

- **Bugfix für Rundkurse/Hin- und Rückwege**: Ein Problem wurde behoben, bei dem das GPS am Start fälschlicherweise an das Ende der Route sprang, wodurch der Graph rückwärts lief ("snap-to-end bug").
- **UI-Verbesserung (Radar)**: Die Anzeige der maximalen Steigung zeigt nun einen farbigen Pfeil (Rot für Anstieg, Dunkelblau für Abstieg) über dem Prozentwert, was Sichtbarkeit und Design erheblich verbessert.
""",
    "README_pt.md": """
## 🐛 VERSÃO 1.0.1: CORREÇÕES CRÍTICAS 🐛

- **Correção de Bug em Rotas Circulares/Ida e Volta**: Resolvido um problema em que o GPS saltava incorretamente para o final da rota logo no início, fazendo com que o gráfico avançasse para trás ("bug snap-to-end").
- **Melhoria de Interface (Radar)**: O indicador de inclinação máxima agora exibe uma seta colorida (Vermelha para subida, Azul Escuro para descida) acima da porcentagem, melhorando bastante a visibilidade e o estilo.
"""
}

# Update all readmes to inject the v1.0.1 changelog below the v1.0.0 changelog
def inject_1_0_1(filepath, new_content):
    if not os.path.exists(filepath):
        return
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # We will inject right before the screenshot section of 1.0.0 or just after the 1.0.0 features
    # Let's just find "## " followed by "NOVEDADES" or "WHATS" or similar
    # Actually the easiest is to put it right at the top, below `# ALTGRAPH (v0.7.0 ELITE)` or whatever the title is.
    # Let's replace the title version from 1.0.0 to 1.0.1, and inject the block
    
    # Update title
    content = re.sub(r'# ALTGRAPH \(v[0-9\.]+( ELITE)?\)', r'# ALTGRAPH (v1.0.1\1)', content)
    
    # Inject new block
    # Find the first "## 🚀" and insert before it
    if "## 🐛" not in content:
        split_point = content.find("## 🚀")
        if split_point != -1:
            content = content[:split_point] + new_content + "\n" + content[split_point:]
            
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Updated {filepath}")

for filename, text in updates.items():
    inject_1_0_1(filename, text)

print("All README files updated to v1.0.1")
