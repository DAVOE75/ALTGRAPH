import glob
import os

files = glob.glob('c:/ALTGRAPH/*.md') + glob.glob('c:/ALTGRAPH/docs/*.md')
for f in files:
    with open(f, 'r', encoding='utf-8') as file:
        content = file.read()
    if 'v1.0.4' in content:
        content = content.replace('v1.0.4', 'v1.0.5')
        with open(f, 'w', encoding='utf-8') as file:
            file.write(content)
