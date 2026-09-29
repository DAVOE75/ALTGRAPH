import base64, glob, os
html_path = 'c:/ALTGRAPH/promo.html'
with open(html_path, 'r', encoding='utf-8') as f:
    html = f.read()

files = sorted(glob.glob(r'C:\Users\davoe\.gemini\antigravity-ide\brain\*\.user_uploaded\*.*'), key=os.path.getmtime, reverse=True)
# files[0] = KarooTopoMaps
# files[1] = Radar
# files[2] = 3D profile
img_path = files[2]

with open(img_path, 'rb') as img_f:
    b64 = base64.b64encode(img_f.read()).decode('utf-8')
ext = img_path.split('.')[-1]
mime = 'image/png' if ext.lower() == 'png' else 'image/jpeg'
data_uri = f'data:{mime};base64,{b64}'
html = html.replace('src=\"\"', f'src=\"{data_uri}\"')

with open(html_path, 'w', encoding='utf-8') as f:
    f.write(html)
print('Injected background image')
