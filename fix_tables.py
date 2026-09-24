import glob
import re

for filename in glob.glob("README*.md"):
    with open(filename, "r", encoding="utf-8") as f:
        content = f.read()
    
    # We want to find the pattern:
    # | Header | Header | Header |
    # - Bullet point 1
    # - Bullet point 2
    # | :---: | :---: | :---: |
    # And change it to:
    # - Bullet point 1
    # - Bullet point 2
    #
    # | Header | Header | Header |
    # | :---: | :---: | :---: |
    
    # regex to match this specific structure
    pattern = r"(\|.*?\|.*?\|.*?\|)\n(-.*?)\n(-.*?)\n(\| :---: \| :---: \| :---: \|)"
    
    match = re.search(pattern, content)
    if match:
        header = match.group(1)
        bullet1 = match.group(2)
        bullet2 = match.group(3)
        separator = match.group(4)
        
        replacement = f"{bullet1}\n{bullet2}\n\n{header}\n{separator}"
        content = content.replace(match.group(0), replacement)
        
        with open(filename, "w", encoding="utf-8") as f:
            f.write(content)
        print(f"Fixed table in {filename}")
    else:
        # Maybe it has 1 bullet point or 3? Let's write a more robust replacement just in case.
        # It's specifically those two bullet points. Let's do a regex that matches any number of bullet points between the header and separator.
        pattern2 = r"(\|.*?\|.*?\|.*?\|)\n((- .*\n)+)(\| :---: \| :---: \| :---: \|)"
        match2 = re.search(pattern2, content)
        if match2:
            header = match2.group(1)
            bullets = match2.group(2).strip()
            separator = match2.group(4)
            
            replacement = f"{bullets}\n\n{header}\n{separator}"
            content = content.replace(match2.group(0), replacement)
            
            with open(filename, "w", encoding="utf-8") as f:
                f.write(content)
            print(f"Fixed table (variable bullets) in {filename}")
