import os
import glob
import re

pt_link = ' | <a href="https://github.com/DAVOE75/ALTGRAPH/blob/main/README_pt.md">🇵🇹 Português</a></p>'

for file in glob.glob("README*.md"):
    with open(file, "r", encoding="utf-8") as f:
        content = f.read()
    
    if "README_pt.md" not in content:
        content = re.sub(r'(<p>.*?(Leer en:|Read in:|Lire en:|Leggi in:|Auf Deutsch lesen:).*?)<\/p>', r'\1' + pt_link, content)
        
        with open(file, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"Updated {file}")

# also update the "Soporte Multilingüe (5 Idiomas)" to 6 Idiomas
for file in glob.glob("README*.md"):
    with open(file, "r", encoding="utf-8") as f:
        content = f.read()
    
    content = content.replace("Soporte Multilingüe (5 Idiomas)", "Soporte Multilingüe (6 Idiomas)")
    content = content.replace("Multilingual Support (5 Languages)", "Multilingual Support (6 Languages)")
    content = content.replace("Support Multilingue (5 Langues)", "Support Multilingue (6 Langues)")
    content = content.replace("Supporto Multilingua (5 Lingue)", "Supporto Multilingua (6 Lingue)")
    content = content.replace("Mehrsprachige Unterstützung (5 Sprachen)", "Mehrsprachige Unterstützung (6 Sprachen)")
    
    # Update language lists inside the text
    content = content.replace("español, inglés, francés, italiano y alemán", "español, inglés, francés, italiano, alemán y portugués")
    content = content.replace("Spanish, English, French, Italian, and German", "Spanish, English, French, Italian, German, and Portuguese")
    content = content.replace("espagnol, anglais, français, italien et allemand", "espagnol, anglais, français, italien, allemand et portugais")
    content = content.replace("spagnolo, inglese, francese, italiano e tedesco", "spagnolo, inglese, francese, italiano, tedesco e portoghese")
    content = content.replace("Spanisch, Englisch, Französisch, Italienisch und Deutsch", "Spanisch, Englisch, Französisch, Italienisch, Deutsch und Portugiesisch")

    with open(file, "w", encoding="utf-8") as f:
        f.write(content)
