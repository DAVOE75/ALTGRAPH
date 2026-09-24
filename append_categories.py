import os

texts = {
    "README.md": """
### 🏅 Categorización de Puertos (Estilo La Vuelta)
ALTGRAPH detecta y clasifica automáticamente los puertos de montaña de tu ruta usando los criterios técnicos de las Grandes Vueltas (La Vuelta a España, Tour de France), basándose en el **Grado de Fatiga (APM)**. Los criterios de detección exigen un mínimo de 3 km al 3% (o muros cortos del 10%) o una dureza acumulada mínima de 20.0 APM. 
Las categorías oficiales asignadas son:
- **Especial (C.E. / HC)**: > 240 APM
- **1ª Categoría**: 150 - 240 APM
- **2ª Categoría**: 85 - 150 APM
- **3ª Categoría**: 40 - 85 APM
- **4ª Categoría**: 20 - 40 APM
""",
    "README_en.md": """
### 🏅 Climb Categorization (Grand Tour Style)
ALTGRAPH automatically detects and classifies mountain passes on your route using technical criteria from Grand Tours (La Vuelta a España, Tour de France), based on the **Fatigue Grade (APM)**. Detection criteria require a minimum of 3 km at 3% (or short 10% walls) or a minimum accumulated hardness of 20.0 APM.
The official categories assigned are:
- **Special (HC)**: > 240 APM
- **1st Category**: 150 - 240 APM
- **2nd Category**: 85 - 150 APM
- **3rd Category**: 40 - 85 APM
- **4th Category**: 20 - 40 APM
""",
    "README_fr.md": """
### 🏅 Catégorisation des Cols (Style Grand Tour)
ALTGRAPH détecte et classe automatiquement les cols de votre itinéraire en utilisant les critères techniques des Grands Tours (La Vuelta, Tour de France), basés sur le **Degré de Fatigue (APM)**. Les critères de détection exigent un minimum de 3 km à 3 % (ou des murs courts à 10 %) ou une dureté cumulée minimale de 20,0 APM.
Les catégories officielles attribuées sont :
- **Hors Catégorie (HC)**: > 240 APM
- **1ère Catégorie**: 150 - 240 APM
- **2ème Catégorie**: 85 - 150 APM
- **3ème Catégorie**: 40 - 85 APM
- **4ème Catégorie**: 20 - 40 APM
""",
    "README_it.md": """
### 🏅 Categorizzazione delle Salite (Stile Grand Tour)
ALTGRAPH rileva e classifica automaticamente i passi di montagna sul tuo percorso utilizzando i criteri tecnici dei Grandi Giri (La Vuelta, Tour de France), basandosi sul **Grado di Fatica (APM)**. I criteri di rilevamento richiedono un minimo di 3 km al 3% (o brevi muri al 10%) o una durezza accumulata minima di 20.0 APM.
Le categorie ufficiali assegnate sono:
- **Fuori Categoria (HC)**: > 240 APM
- **1ª Categoria**: 150 - 240 APM
- **2ª Categoria**: 85 - 150 APM
- **3ª Categoria**: 40 - 85 APM
- **4ª Categoria**: 20 - 40 APM
""",
    "README_de.md": """
### 🏅 Bergkategorisierung (Grand Tour Stil)
ALTGRAPH erkennt und klassifiziert Bergpässe auf Ihrer Route automatisch nach den technischen Kriterien der Grand Tours (La Vuelta, Tour de France), basierend auf dem **Ermüdungsgrad (APM)**. Die Erkennungskriterien erfordern ein Minimum von 3 km bei 3 % (oder kurze 10 %-Mauern) oder eine akkumulierte Mindesthärte von 20,0 APM.
Die zugewiesenen offiziellen Kategorien sind:
- **Ehrenkategorie (HC)**: > 240 APM
- **1. Kategorie**: 150 - 240 APM
- **2. Kategorie**: 85 - 150 APM
- **3. Kategorie**: 40 - 85 APM
- **4. Kategorie**: 20 - 40 APM
""",
    "README_pt.md": """
### 🏅 Categorização de Subidas (Estilo Grand Tour)
ALTGRAPH detecta e classifica automaticamente os passos de montanha na sua rota usando critérios técnicos dos Grand Tours (La Vuelta a España, Tour de France), com base no **Grau de Fadiga (APM)**. Os critérios de detecção exigem um mínimo de 3 km a 3% (ou muros curtos de 10%) ou uma dureza acumulada mínima de 20,0 APM.
As categorias oficiais atribuídas são:
- **Especial (HC)**: > 240 APM
- **1ª Categoria**: 150 - 240 APM
- **2ª Categoria**: 85 - 150 APM
- **3ª Categoria**: 40 - 85 APM
- **4ª Categoria**: 20 - 40 APM
"""
}

for file, extra_text in texts.items():
    if os.path.exists(file):
        with open(file, "a", encoding="utf-8") as f:
            f.write(extra_text)
            print(f"Appended to {file}")
