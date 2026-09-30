import re
with open('c:/ALTGRAPH/docs/index.html', 'r', encoding='utf-8') as f:
    html = f.read()

match = re.search(r'<script>(.*?)</script>', html, re.DOTALL)
js = match.group(1)

ids_in_js = re.findall(r"document\.getElementById\('([^']+)'\)", js)
ids_in_js = set(ids_in_js)

ids_in_html = re.findall(r'id="([^"]+)"', html)
ids_in_html = set(ids_in_html)

missing = ids_in_js - ids_in_html
print('Missing IDs:', missing)
