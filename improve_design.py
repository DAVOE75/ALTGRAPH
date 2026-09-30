import re

with open('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/Altimetria3DView.kt', 'r', encoding='utf-8') as f:
    content = f.read()

def replace_paint(name, new_props):
    global content
    pattern = r'(private val ' + name + r' = Paint\(Paint\.ANTI_ALIAS_FLAG\)\.apply \{.*?^\s*\})'
    match = re.search(pattern, content, re.DOTALL | re.MULTILINE)
    if match:
        content = content.replace(match.group(1), f'private val {name} = Paint(Paint.ANTI_ALIAS_FLAG).apply {{\n{new_props}\n    }}')

replace_paint('cotaTextPaint', '        color = Color.parseColor("#CBD5E1")\n        typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)')
replace_paint('subBlockPctTextPaint', '        color = Color.WHITE\n        typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)\n        textAlign = Paint.Align.CENTER\n        setShadowLayer(6f, 0f, 3f, Color.BLACK)')
replace_paint('baseDistTextPaint', '        color = Color.WHITE\n        typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)\n        textAlign = Paint.Align.CENTER')
replace_paint('titlePaint', '        color = Color.WHITE\n        typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)')
replace_paint('subTitleLabelPaint', '        color = Color.parseColor("#38BDF8")\n        typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)')

replace_paint('pctBadgeBgPaint', '        color = Color.parseColor("#E6000000") // Very dark translucent\n        style = Paint.Style.FILL')
replace_paint('pctBadgeBorderPaint', '        color = Color.TRANSPARENT\n        strokeWidth = 0f\n        style = Paint.Style.STROKE')

replace_paint('sliceSeparatorPaint', '        color = Color.parseColor("#15FFFFFF")\n        strokeWidth = 1.0f\n        style = Paint.Style.STROKE')
replace_paint('majorSliceSeparatorPaint', '        color = Color.parseColor("#25FFFFFF")\n        strokeWidth = 1.5f\n        style = Paint.Style.STROKE')
replace_paint('obsidianFacetPaint', '        color = Color.parseColor("#0CFFFFFF")\n        strokeWidth = 1.0f\n        style = Paint.Style.STROKE')

replace_paint('neonGlowPaint', '        color = Color.parseColor("#8038BDF8")\n        strokeWidth = 8.0f\n        style = Paint.Style.STROKE\n        strokeCap = Paint.Cap.ROUND\n        maskFilter = android.graphics.BlurMaskFilter(16f, android.graphics.BlurMaskFilter.Blur.NORMAL)')

# Let's fix the pill rounding for pct badges
content = content.replace('canvas.drawRoundRect(pctRect, 4f, 4f, bgP)', 'canvas.drawRoundRect(pctRect, pctRect.height() / 2f, pctRect.height() / 2f, bgP)')
content = content.replace('canvas.drawRoundRect(pctRect, 4f, 4f, borderP)', 'canvas.drawRoundRect(pctRect, pctRect.height() / 2f, pctRect.height() / 2f, borderP)')

with open('c:/ALTGRAPH/app/src/main/java/com/example/altgraph/Altimetria3DView.kt', 'w', encoding='utf-8') as f:
    f.write(content)

print('Paints updated')
