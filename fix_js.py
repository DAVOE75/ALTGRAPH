import re

with open('c:/ALTGRAPH/docs/index.html', 'r', encoding='utf-8') as f:
    content = f.read()

# I will replace all instances of:
# document.getElementById('...').innerHTML = 
# document.getElementById('...').innerText = 
# with optional chaining or an if statement, to prevent the script from crashing.

def safe_replace(match):
    full_line = match.group(0)
    # Don't modify if it already has 'if ('
    if 'if (' in full_line:
        return full_line
    
    # Extract the id
    id_match = re.search(r"document\.getElementById\('([^']+)'\)", full_line)
    if id_match:
        id_str = id_match.group(1)
        # return f"if (document.getElementById('{id_str}')) {full_line.lstrip()}"
        return full_line.replace(f"document.getElementById('{id_str}')", f"document.getElementById('{id_str}')?")
    return full_line


# Wait, JS syntax document.getElementById('foo')?.innerText is valid in modern browsers.
# Let's replace document.getElementById('...') with document.getElementById('...')? where it's not already safe.
# Actually, the simplest is to replace all `document.getElementById('x').inner` with `var el = document.getElementById('x'); if (el) el.inner` ...
# But optional chaining is easiest: document.getElementById('g3-label')?.innerText = t.g3Label;
# However, `?.` assignment might throw "Invalid left-hand side in assignment" in some older browsers/engines. 
# It is better to use `if (document.getElementById('id')) document.getElementById('id').innerText = ...`

new_content = ""
for line in content.split('\n'):
    if "document.getElementById(" in line and "if (document.getElementById" not in line:
        id_m = re.search(r"document\.getElementById\('([^']+)'\)", line)
        if id_m:
            the_id = id_m.group(1)
            # Add if wrapper
            leading_spaces = len(line) - len(line.lstrip())
            line = (" " * leading_spaces) + f"if (document.getElementById('{the_id}')) " + line.lstrip()
    new_content += line + "\n"

with open('c:/ALTGRAPH/docs/index.html', 'w', encoding='utf-8') as f:
    f.write(new_content)

print('Fixed JS language switcher crash')
