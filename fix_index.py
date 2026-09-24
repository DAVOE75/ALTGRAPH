import re

with open("docs/index.html", "r", encoding="utf-8") as f:
    html = f.read()

# Fix m3Tag missing comma
html = html.replace('m3Tag: "Tactical Recovery & Fire"\n', 'm3Tag: "Tactical Recovery & Fire",\n')
html = html.replace('m3Tag: "Tactical Recovery & Fire"\r\n', 'm3Tag: "Tactical Recovery & Fire",\r\n')

# Also if there are cases with spaces:
html = re.sub(r'(m3Tag: "Tactical Recovery & Fire")\s*(m3Desc:)', r'\1,\n                \2', html)

# Just to be safe, check if we need to replace m4Tag missing comma again
html = re.sub(r'(m4Tag: "Kinetic Gravitational Field")\s*(m4Desc:)', r'\1,\n                \2', html)


with open("docs/index.html", "w", encoding="utf-8") as f:
    f.write(html)
print("Fixed commas")
