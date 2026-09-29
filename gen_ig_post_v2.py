import base64, glob, os, asyncio, re
from playwright.async_api import async_playwright

html_path = 'c:/ALTGRAPH/instagram_post.html'
with open(html_path, 'r', encoding='utf-8') as f:
    html = f.read()

files = sorted(glob.glob(r'C:\Users\davoe\.gemini\antigravity-ide\brain\*\.user_uploaded\*.*'), key=os.path.getmtime, reverse=True)
img_path = files[0] # The new image

with open(img_path, 'rb') as img_f:
    b64 = base64.b64encode(img_f.read()).decode('utf-8')
ext = img_path.split('.')[-1]
mime = 'image/png' if ext.lower() == 'png' else 'image/jpeg'
data_uri = f'data:{mime};base64,{b64}'

html = re.sub(r'src=\"data:image/[^\"]*\"', f'src=\"{data_uri}\"', html)

with open(html_path, 'w', encoding='utf-8') as f:
    f.write(html)

async def main():
    async with async_playwright() as p:
        browser = await p.chromium.launch()
        page = await browser.new_page(viewport={'width': 1080, 'height': 1080})
        await page.goto('file:///' + html_path)
        await page.screenshot(path='c:/ALTGRAPH/altgraph_instagram_post_1.png')
        await browser.close()

asyncio.run(main())
print('Generated new Instagram post')
