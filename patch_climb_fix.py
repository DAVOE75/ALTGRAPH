import os

path = 'c:/ALTGRAPH/app/src/main/java/com/example/altgraph/ClimbViewerDataField.kt'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

# We need to replace the block starting at `val climbs = calculator.routeClimbs` and ending at `remoteViews.setImageViewBitmap`

start_str = 'val climbs = calculator.routeClimbs'
end_str = 'remoteViews.setImageViewBitmap(R.id.img_graphic, currentBmp)'

start_idx = content.find(start_str)
end_idx = content.find(end_str) + len(end_str)

if start_idx != -1 and end_idx != -1:
    new_block = """val climbs = calculator.routeClimbs
                val isRotated = AppPreferences.getInstance(context).climbRotate90Clockwise
                var drawW = w.toFloat()
                var drawH = h.toFloat()

                if (climbs.isEmpty()) {
                    if (isRotated) {
                        currentCanvas.save()
                        currentCanvas.translate(w.toFloat(), 0f)
                        currentCanvas.rotate(90f)
                        drawW = h.toFloat()
                        drawH = w.toFloat()
                    }
                    currentCanvas.drawText("Sin puertos detectados / No climbs", drawW / 2f, drawH / 2f, overlayPaint)
                    if (isRotated) {
                        currentCanvas.restore()
                    }
                } else {
                    if (currentClimbIndex >= climbs.size) currentClimbIndex = 0
                    val climb = climbs[currentClimbIndex]
                    
                    val quarterLength = climb.length / 4.0
                    val customStartDist = if (currentZoomQuarter > 0) climb.startDistance + (currentZoomQuarter - 1) * quarterLength else climb.startDistance
                    val customLength = if (currentZoomQuarter > 0) quarterLength else climb.length
                    
                    val strategy = calculator.getStrategyDataForClimb(climb, customStartDist, customLength)
                    val prefs = AppPreferences.getInstance(context)
                    
                    val lookahead = customLength.toInt().coerceAtLeast(100)
                    
                    val riderProgress = if (calculator.currentRouteDistance >= customStartDist && calculator.currentRouteDistance <= customStartDist + customLength) {
                        ((calculator.currentRouteDistance - customStartDist) / customLength).toFloat()
                    } else {
                        -1f
                    }
                    
                    altimetria3DView.oasisDistanceToNextCrucible = strategy.oasisDistanceToNextCrucible
                    altimetria3DView.virtualPacerRelativeDistance = strategy.virtualPacerRelativeDistance
                    altimetria3DView.energyBatteryLevel = strategy.energyBatteryLevel
                
                    altimetria3DView.stravaSegmentDistance = strategy.stravaSegmentDistance
                    altimetria3DView.stravaPrGhostDistance = strategy.stravaPrGhostDistance
                    altimetria3DView.windEffectIntensity = strategy.windEffectIntensity
                    altimetria3DView.update3DData(
                        blocks = strategy.nextBlocks,
                        elevation = strategy.windowStartElevation,
                        maxElev = 0.0,
                        grade = strategy.avgGrade,
                        remainingDist = strategy.remainingDistance,
                        blockSizeMeters = strategy.blockSizeMeters,
                        fontScale = prefs.fontSize3dScale,
                        lookaheadMeters = lookahead,
                        showCotas = prefs.show3dCotas,
                        showRamps = prefs.show3dRamps,
                        showMaxGrade = prefs.showMaxGradient,
                        fontFamilyKey = prefs.fontFamilyKey,
                        rotate90 = prefs.climbRotate90Clockwise,
                        rotateMinus90 = false,
                        rampMinSlope = prefs.rampMinSlopePct,
                        rampMaxSlope = prefs.rampMaxSlopePct,
                        showHairpins = false,
                        showPois = false,
                        hairpins = emptyList(),
                        pois = emptyList(),
                        showZoomControls = false,
                        curvatureOffsets = emptyList(),
                        riderProgress = riderProgress,
                        windowStartMeters = strategy.windowStartMeters,
                        subBlocks = strategy.subBlocks,
                        subBlockSizeMeters = strategy.subBlockSizeMeters,
                        majorBlockSizeMeters = strategy.majorBlockSizeMeters,
                        profileElevations = strategy.profileElevations,
                        showBlockPercentages = prefs.showBlockPercentages,
                        altimetriaStyle = prefs.climbAltimetriaStyle,
                        targetVam = prefs.targetVam,
                        activeClimbs = emptyList(),
                        visibleAvgGrade = strategy.visibleAvgGrade,
                        visibleMaxGrade = strategy.visibleMaxGrade,
                        routeName = "",
                        customTitle = "",
                        showHeaderStats = false,
                        routeCoords = strategy.routeCoords
                    )
                    
                    altimetria3DView.draw(currentCanvas)
                    
                    if (isRotated) {
                        currentCanvas.save()
                        currentCanvas.translate(w.toFloat(), 0f)
                        currentCanvas.rotate(90f)
                        drawW = h.toFloat()
                        drawH = w.toFloat()
                    }
                    overlayPaint.textSize = (drawH * 0.12f).coerceIn(26f, 42f)
                    FontHelper.applyFontToPaint(overlayPaint, prefs.fontFamilyKey)
                    
                    subPaint.textSize = (drawH * 0.06f).coerceIn(16f, 24f)
                    subPaint.textAlign = Paint.Align.LEFT
                    FontHelper.applyFontToPaint(subPaint, prefs.fontFamilyKey)
                    
                    val maxGradePaint = Paint(subPaint).apply {
                        color = android.graphics.Color.parseColor("#EF4444")
                    }
                    FontHelper.applyFontToPaint(maxGradePaint, prefs.fontFamilyKey)
                    
                    val climbTitle = "◀   Puerto ${currentClimbIndex + 1}/${climbs.size} - ${climb.category}   ▶"
                    currentCanvas.drawText(climbTitle, drawW / 2f, drawH * 0.10f, overlayPaint)
                    
                    val distStr = if (calculator.isNavigatingRoute) {
                        val distanceToStart = climb.startDistance - calculator.currentRouteDistance
                        val km = (java.lang.Math.abs(distanceToStart) / 1000).toInt()
                        val m = (java.lang.Math.abs(distanceToStart) % 1000).toInt()
                        if (distanceToStart > 0) {
                            if (km > 0) " | Faltan ${km}km ${m}m" else " | Faltan ${m}m"
                        } else if (distanceToStart <= 0 && calculator.currentRouteDistance < climb.endDistance) {
                            " | En puerto"
                        } else {
                            " | Superado"
                        }
                    } else ""
                    
                    val line1 = "${String.format("%.1f", climb.length / 1000f)}km | +${climb.totalElevation.toInt()}m$distStr"
                    
                    val liveGrade = calculator.instantBarometricGrade
                    val maxG = if (strategy.visibleMaxGrade > 0) strategy.visibleMaxGrade else climb.avgGrade
                    val line2Part1 = "Pend. Actual: ${String.format("%.1f", liveGrade)}%  |  P.Med: ${String.format("%.1f", climb.avgGrade)}%  |  P.Max: "
                    val line2Part2 = "${String.format("%.1f", maxG)}%"
                    
                    val wLine2P1 = subPaint.measureText(line2Part1)
                    val wLine2P2 = maxGradePaint.measureText(line2Part2)
                    
                    val statsY1 = drawH * 0.18f
                    val statsY2 = statsY1 + 40f
                    
                    subPaint.textAlign = Paint.Align.CENTER
                    currentCanvas.drawText(line1, drawW / 2f, statsY1, subPaint)
                    
                    subPaint.textAlign = Paint.Align.LEFT
                    val line2TotalW = wLine2P1 + wLine2P2
                    var currentX = (drawW / 2f) - (line2TotalW / 2f)
                    currentCanvas.drawText(line2Part1, currentX, statsY2, subPaint)
                    currentX += wLine2P1
                    currentCanvas.drawText(line2Part2, currentX, statsY2, maxGradePaint)
                    
                    if (isRotated) {
                        currentCanvas.restore()
                    }
                }

                val layoutRes = if (isRotated) R.layout.view_remote_climb_land else R.layout.view_remote_climb
                val remoteViews = RemoteViews(context.packageName, layoutRes)
                remoteViews.setImageViewBitmap(R.id.img_graphic, currentBmp)"""

    content = content[:start_idx] + new_block + content[end_idx:]
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Patched successfully!")
else:
    print("Could not find block boundaries!")
