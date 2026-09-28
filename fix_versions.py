import glob
import os

for filepath in glob.glob("c:/ALTGRAPH/*.md"):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Replace the top heading
    content = content.replace("# ALTGRAPH (v1.0.1 ELITE)", "# ALTGRAPH (v1.0.2 ELITE)")
    content = content.replace("# ALTGRAPH (v1.0.0 ELITE)", "# ALTGRAPH (v1.0.2 ELITE)")
    
    # Replace the bugfix heading
    content = content.replace("1.0.1: C", "1.0.2: C")
    content = content.replace("1.0.1: K", "1.0.2: K")
    
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Fixed {filepath}")
