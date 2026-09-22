import os

climb_path = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/ClimbViewerDataField.kt'
with open(climb_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if '// Draw Header Overlay' in line:
        new_lines.append('''
                    // Draw Header Overlay
                    val isRotated = prefs.climbRotate90Clockwise
                    var drawW = w.toFloat()
                    var drawH = h.toFloat()

                    if (isRotated) {
                        currentCanvas.save()
                        currentCanvas.translate(w.toFloat(), 0f)
                        currentCanvas.rotate(90f)
                        drawW = h.toFloat()
                        drawH = w.toFloat()
                    }
''')
        continue
    
    if 'overlayPaint.textSize = (h * 0.12f).coerceIn(26f, 42f)' in line:
        line = line.replace('h *', 'drawH *')
    elif 'subPaint.textSize = (h * 0.06f).coerceIn(16f, 24f)' in line:
        line = line.replace('h *', 'drawH *')
    elif 'currentCanvas.drawText(climbTitle, w / 2f, h * 0.10f, overlayPaint)' in line:
        line = line.replace('w / 2f', 'drawW / 2f').replace('h *', 'drawH *')
    elif 'val statsY1 = h * 0.18f' in line:
        line = line.replace('h *', 'drawH *')
    elif 'currentCanvas.drawText(line1, w / 2f, statsY1, subPaint)' in line:
        line = line.replace('w / 2f', 'drawW / 2f')
    elif 'var currentX = (w / 2f) - (line2TotalW / 2f)' in line:
        line = line.replace('w / 2f', 'drawW / 2f')

    if 'val remoteViews = RemoteViews(context.packageName, R.layout.view_remote_climb)' in line:
        new_lines.append('''
                    if (isRotated) {
                        currentCanvas.restore()
                    }

                    val layoutRes = if (isRotated) R.layout.view_remote_climb_land else R.layout.view_remote_climb
                    val remoteViews = RemoteViews(context.packageName, layoutRes)
''')
        continue
        
    new_lines.append(line)

with open(climb_path, 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print("Done patching ClimbViewerDataField!")
