import re

calc_file = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\AltimetriaStrategyCalculator.kt'
view_file = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\Altimetria3DView.kt'
data_field_1 = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\Altimetria3DGraphDataField.kt'
data_field_2 = r'c:\ALTGRAPH\app\src\main\java\com\example\altgraph\ClimbViewerDataField.kt'

# 1. Update Calculator
with open(calc_file, 'r', encoding='utf-8') as f:
    calc_content = f.read()

energy_logic = '''            virtualPacerDistance += vx * 1.0 // Assume ~1s tick
        }

        // ENERGY MANAGEMENT LOGIC
        if (energyManagementEnabled) {
            val vam = currentSpeed * (instantBarometricGrade / 100.0) * 3600.0
            val targetVam = (prefs?.targetVam ?: 900).toDouble()
            
            if (vam > targetVam * 1.1) {
                energyBatteryLevel -= 0.5
            } else if (vam < targetVam * 0.6 && instantBarometricGrade < 4.0) {
                energyBatteryLevel += 0.3
            }
            energyBatteryLevel = energyBatteryLevel.coerceIn(0.0, 100.0)
        }'''
calc_content = calc_content.replace('            virtualPacerDistance += vx * 1.0 // Assume ~1s tick\n        }', energy_logic)

calc_content = calc_content.replace(
    'virtualPacerRelativeDistance = if (virtualPacerEnabled) virtualPacerDistance - liveDistanceAccumulated else null',
    'virtualPacerRelativeDistance = if (virtualPacerEnabled) virtualPacerDistance - liveDistanceAccumulated else null,\n                energyBatteryLevel = energyBatteryLevel'
)
calc_content = calc_content.replace(
    'virtualPacerRelativeDistance = if (virtualPacerEnabled) virtualPacerDistance - currentRiderDistance else null',
    'virtualPacerRelativeDistance = if (virtualPacerEnabled) virtualPacerDistance - currentRiderDistance else null,\n            energyBatteryLevel = energyBatteryLevel'
)

with open(calc_file, 'w', encoding='utf-8') as f:
    f.write(calc_content)


# 2. Update 3D View
with open(view_file, 'r', encoding='utf-8') as f:
    view_content = f.read()

view_content = view_content.replace('    var virtualPacerRelativeDistance: Double? = null', '    var virtualPacerRelativeDistance: Double? = null\n    var energyBatteryLevel: Double = 100.0')

ribbon_orig = '''        for (i in 0 until totalMicroSamples) {
            val px = startX + (i * microStepX) - panOffsetX
            val normalizedHeight = ((microElevations[i] - displayMinElev) / finalElevRange).coerceIn(0f, 1f)

            val pYFront = baseGroundY - (normalizedHeight * maxPeakHeight)

            xFront[i] = px
            yFront[i] = pYFront

            // Cara trasera extruida en 3D sutil hacia arriba y atrás
            xBack[i] = px - depth3dX
            yBack[i] = pYFront - depth3dY'''

ribbon_new = '''        for (i in 0 until totalMicroSamples) {
            val px = startX + (i * microStepX) - panOffsetX
            val normalizedHeight = ((microElevations[i] - displayMinElev) / finalElevRange).coerceIn(0f, 1f)
            val pYFront = baseGroundY - (normalizedHeight * maxPeakHeight)

            var curveOffset = 0f
            if (showHairpins && hairpins.isNotEmpty()) {
                val dist = windowStartMeters + (i.toDouble() / totalMicroSamples.coerceAtLeast(1)) * lookaheadMeters
                for (hp in hairpins) {
                    val diff = dist - hp
                    if (abs(diff) < 25.0) {
                        val phase = (diff + 25.0) / 50.0
                        curveOffset = (Math.sin(phase * Math.PI * 2.0) * 20.0).toFloat()
                        break
                    }
                }
            }

            xFront[i] = px + curveOffset
            yFront[i] = pYFront

            // Cara trasera extruida en 3D sutil hacia arriba y atrás
            xBack[i] = (px + curveOffset) - depth3dX
            yBack[i] = pYFront - depth3dY'''
view_content = view_content.replace(ribbon_orig, ribbon_new)

battery_hud = '''        // PRO FEATURE: Oasis Tracking Hologram
        oasisDistanceToNextCrucible?.let { oasisDist ->
            val distStr = if (oasisDist >= 1000) String.format("%.1f km", oasisDist / 1000.0) else " m"
            val text = "❄️ OASIS: \ HASTA EL MURO"
            canvas.drawText(text, w / 2f, h * 0.15f, oasisHoloPaint)
        }
        
        // PRO FEATURE: Energy Battery HUD
        val batteryW = 80f
        val batteryH = 8f
        val bx = w - batteryW - 20f
        val by = 20f
        canvas.drawRect(bx, by, bx + batteryW, by + batteryH, baseBarPaint)
        val fillPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = if (energyBatteryLevel > 50) android.graphics.Color.parseColor("#22C55E") else if (energyBatteryLevel > 20) android.graphics.Color.parseColor("#EAB308") else android.graphics.Color.parseColor("#EF4444")
            style = android.graphics.Paint.Style.FILL
            setShadowLayer(6f, 0f, 0f, color)
        }
        canvas.drawRect(bx, by, bx + (batteryW * (energyBatteryLevel.toFloat() / 100f)), by + batteryH, fillPaint)
'''
view_content = view_content.replace('''        // PRO FEATURE: Oasis Tracking Hologram
        oasisDistanceToNextCrucible?.let { oasisDist ->
            val distStr = if (oasisDist >= 1000) String.format("%.1f km", oasisDist / 1000.0) else " m"
            val text = "❄️ OASIS: \ HASTA EL MURO"
            canvas.drawText(text, w / 2f, h * 0.15f, oasisHoloPaint)
        }''', battery_hud)

with open(view_file, 'w', encoding='utf-8') as f:
    f.write(view_content)


# 3. Update DataFields to pass battery
for df_path in [data_field_1, data_field_2]:
    with open(df_path, 'r', encoding='utf-8') as f:
        df_content = f.read()
    df_content = df_content.replace(
        'altimetria3DView.virtualPacerRelativeDistance = strategy.virtualPacerRelativeDistance',
        'altimetria3DView.virtualPacerRelativeDistance = strategy.virtualPacerRelativeDistance\n                altimetria3DView.energyBatteryLevel = strategy.energyBatteryLevel'
    )
    with open(df_path, 'w', encoding='utf-8') as f:
        f.write(df_content)

print("Phase 2 & 3 patched")
