import sys
path = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\MainActivity.kt'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

target = '''        val themeSpinner = Spinner(this).apply {
            val adapter = android.widget.ArrayAdapter(context, android.R.layout.simple_spinner_item, themes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            this.adapter = adapter
            val currentTheme = prefs.eliteRadarTheme
            val idx = themes.indexOf(currentTheme)
            if (idx >= 0) setSelection(idx)
            
            onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                    prefs.eliteRadarTheme = themes[position]
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
        }
        radar3dCard.addView(themeSpinner)'''

target_win = target.replace('\n', '\r\n')

replacement = '''        val themeRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 8, 0, 8) }
        }
        val themeButtons = mutableListOf<android.widget.Button>()
        themes.forEach { theme ->
            val btn = createSegmentButton(theme.take(5), prefs.eliteRadarTheme == theme) {
                prefs.eliteRadarTheme = theme
                updateSegmentActiveStates(themeButtons, themes.indexOf(theme))
            }
            themeButtons.add(btn)
            themeRow.addView(btn)
        }
        radar3dCard.addView(themeRow)'''

if target in c:
    c = c.replace(target, replacement)
elif target_win in c:
    c = c.replace(target_win, replacement)
else:
    print('Target not found')
    sys.exit(1)

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
print('Done')
