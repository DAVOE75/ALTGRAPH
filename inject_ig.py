import re

with open('c:/ALTGRAPH/docs/index.html', 'r', encoding='utf-8') as f:
    content = f.read()

# Header link
content = content.replace('<a href="https://github.com/DAVOE75/ALTGRAPH" target="_blank">GitHub</a>', '<a href="https://github.com/DAVOE75/ALTGRAPH" target="_blank">GitHub</a>\n                <a href="https://www.instagram.com/altgraph.app/" target="_blank" style="color: #eab308; font-weight: 800;">Instagram</a>')

# Footer default text
content = content.replace('Código Fuente en GitHub</a></p>', 'Código Fuente en GitHub</a> &bull; <a href="https://www.instagram.com/altgraph.app/" target="_blank">Instagram</a></p>')

# Translations (ES, EN, FR, IT, DE, PT)
content = content.replace('Código Fuente en GitHub</a>`', 'Código Fuente en GitHub</a> &bull; <a href="https://www.instagram.com/altgraph.app/" target="_blank">Instagram</a>`')
content = content.replace('Source Code on GitHub</a>`', 'Source Code on GitHub</a> &bull; <a href="https://www.instagram.com/altgraph.app/" target="_blank">Instagram</a>`')
content = content.replace('Code Source sur GitHub</a>`', 'Code Source sur GitHub</a> &bull; <a href="https://www.instagram.com/altgraph.app/" target="_blank">Instagram</a>`')
content = content.replace('Codice Sorgente su GitHub</a>`', 'Codice Sorgente su GitHub</a> &bull; <a href="https://www.instagram.com/altgraph.app/" target="_blank">Instagram</a>`')
content = content.replace('Quellcode auf GitHub</a>`', 'Quellcode auf GitHub</a> &bull; <a href="https://www.instagram.com/altgraph.app/" target="_blank">Instagram</a>`')
content = content.replace('Repositório Oficial do GitHub Open Source</a>`', 'Repositório Oficial do GitHub Open Source</a> &bull; <a href="https://www.instagram.com/altgraph.app/" target="_blank">Instagram</a>`')

with open('c:/ALTGRAPH/docs/index.html', 'w', encoding='utf-8') as f:
    f.write(content)

print('Added Instagram links')
