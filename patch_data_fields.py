import re

files = [
    r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\Altimetria3DGraphDataField.kt',
    r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\ClimbViewerDataField.kt'
]

for file_path in files:
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    orig = 'altimetria3DView.update3DData('
    new_code = '''altimetria3DView.oasisDistanceToNextCrucible = strategy.oasisDistanceToNextCrucible
                altimetria3DView.virtualPacerRelativeDistance = strategy.virtualPacerRelativeDistance
                altimetria3DView.update3DData('''
    
    content = content.replace(orig, new_code)
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

print("Data fields patched")
