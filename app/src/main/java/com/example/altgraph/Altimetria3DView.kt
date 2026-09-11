package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.sin

class Altimetria3DView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bgPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1F2937")
        strokeWidth = 1.2f
        style = Paint.Style.STROKE
    }

    private val cotaLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E4E4E7")
        strokeWidth = 2.5f
        style = Paint.Style.STROKE
    }

    private val cotaTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E4E4E7")
        typeface = Typeface.DEFAULT_BOLD
    }

    private val rampArrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EF4444")
        strokeWidth = 3.5f
        style = Paint.Style.STROKE
    }

    private val rampArrowHeadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EF4444")
        style = Paint.Style.FILL
    }

    private val rampTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val lineStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
    }

    private val subTitleLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        typeface = Typeface.DEFAULT_BOLD
    }

    private val liveGradePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        typeface = Typeface.DEFAULT_BOLD
    }

    private val maxGradePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        typeface = Typeface.DEFAULT_BOLD
    }

    private val axisTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9CA3AF")
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.LEFT
    }

    private val percentTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 0f, 0f, Color.BLACK)
    }

    private val beaconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        style = Paint.Style.FILL
    }

    private val beaconHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4038BDF8")
        style = Paint.Style.FILL
    }

    private val hairpinLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#111827")
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private val poiLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#9CA3AF")
        strokeWidth = 2.5f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(8f, 8f), 0f)
    }
    
    private val poiBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#A0000000")
        style = Paint.Style.FILL
    }

    private val poiTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
    }

    // Zoom Controls Overlay Paints
    private val zoomBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E018181B")
        style = Paint.Style.FILL
    }

    private val zoomBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val zoomTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val zoomBtnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private var nextBlocks: List<Float> = emptyList()
    private var currentElevation: Double = 350.0
    private var maxElevation: Double = 727.0
    private var currentGrade: Double = 7.2
    private var remainingDistance: Double = 8500.0
    private var blockSizeMeters: Double = 100.0
    private var lookaheadMeters: Int = 350
    private var fontScale: Float = 1.0f
    private var showCotas: Boolean = true
    private var showRamps: Boolean = true
    private var showMaxGrade: Boolean = true
    private var rotate90: Boolean = false
    private var rampMinSlope: Double = 10.0
    private var rampMaxSlope: Double = 15.0
    private var showHairpins: Boolean = true
    private var showPois: Boolean = true
    private var hairpins: List<Double> = emptyList()
    private var pois: List<Poi> = emptyList()

    private val wallPath = Path()
    private val arrowPath = Path()
    private val tagRect = RectF()

    fun update3DData(
        blocks: List<Float>,
        elevation: Double,
        maxElev: Double,
        grade: Double,
        remainingDist: Double,
        blockSizeMeters: Double = 100.0,
        fontScale: Float = 1.0f,
        lookaheadMeters: Int = 350,
        showCotas: Boolean = true,
        showRamps: Boolean = true,
        showMaxGrade: Boolean = true,
        fontFamilyKey: String = "sans-serif-condensed",
        rotate90: Boolean = false,
        rampMinSlope: Double = 10.0,
        rampMaxSlope: Double = 15.0,
        showHairpins: Boolean = true,
        showPois: Boolean = true,
        hairpins: List<Double> = emptyList(),
        pois: List<Poi> = emptyList()
    ) {
        if (blocks.isNotEmpty()) {
            this.nextBlocks = blocks
        }
        if (elevation > 0) {
            this.currentElevation = elevation
        }
        this.maxElevation = if (maxElev > 0) maxElev else 727.0
        this.currentGrade = grade
        this.remainingDistance = remainingDist
        this.blockSizeMeters = blockSizeMeters
        this.fontScale = fontScale
        this.lookaheadMeters = lookaheadMeters
        this.showCotas = showCotas
        this.showRamps = showRamps
        this.showMaxGrade = showMaxGrade
        this.rotate90 = rotate90
        this.rampMinSlope = rampMinSlope
        this.rampMaxSlope = rampMaxSlope
        this.showHairpins = showHairpins
        this.showPois = showPois
        this.hairpins = hairpins
        this.pois = pois

        FontHelper.applyFontToPaint(titlePaint, fontFamilyKey)
        FontHelper.applyFontToPaint(subTitleLabelPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(liveGradePaint, fontFamilyKey)
        FontHelper.applyFontToPaint(maxGradePaint, fontFamilyKey)
        FontHelper.applyFontToPaint(percentTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(rampTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(cotaTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(axisTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(poiTextPaint, fontFamilyKey)

        postInvalidate()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val touchX = event.x
            val touchY = event.y
            val w = width.toFloat()
            val h = height.toFloat()

            val pillW = (w * 0.38f).coerceIn(120f, 210f)
            val pillH = (h * 0.15f).coerceIn(28f, 44f)
            val right = w - 16f
            val left = right - pillW
            val top = 16f
            val bottom = top + pillH
            val btnW = pillW * 0.28f

            if (touchY in top..bottom) {
                // Tapped [-] button (Alejar / Zoom Out)
                if (touchX >= left && touchX <= left + btnW + 10f) {
                    performClick()
                    zoomOut()
                    return true
                }
                // Tapped [+] button (Acercar / Zoom In)
                if (touchX >= right - btnW - 10f && touchX <= right) {
                    performClick()
                    zoomIn()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun zoomIn() {
        val curr = lookaheadMeters
        val nextVal = when {
            curr > 1000 -> curr - 1000
            curr == 1000 -> 500
            curr > 200 -> curr - 50
            else -> 200
        }
        lookaheadMeters = nextVal
        AppPreferences.getInstance(context).lookaheadMeters3d = nextVal
        postInvalidate()
    }

    private fun zoomOut() {
        val curr = lookaheadMeters
        val nextVal = when {
            curr < 500 -> curr + 50
            curr == 500 -> 1000
            curr < 10000 -> curr + 1000
            else -> 10000
        }
        lookaheadMeters = nextVal
        AppPreferences.getInstance(context).lookaheadMeters3d = nextVal
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0 || h <= 0) return

        if (rotate90) {
            canvas.save()
            canvas.translate(w, 0f)
            canvas.rotate(90f)
            drawRenderContent(canvas, h, w)
            canvas.restore()
        } else {
            drawRenderContent(canvas, w, h)
        }
    }

    private fun drawRenderContent(canvas: Canvas, w: Float, h: Float) {
        // 1. Fondo Oscuro
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // 2. Encabezado Título ("Altimetría 3D")
        val titleText = context.getString(R.string.data_type_altimetria_3d_title)
        titlePaint.textSize = ((h * 0.085f) * fontScale).coerceIn(18f, 32f)
        canvas.drawText(titleText, 18f, h * 0.08f, titlePaint)

        // Etiqueta PENDIENTE ACTUAL
        val labelCurrentGrade = context.getString(R.string.label_current_gradient)
        subTitleLabelPaint.textSize = ((h * 0.045f) * fontScale).coerceIn(10f, 16f)
        canvas.drawText(labelCurrentGrade, 18f, h * 0.14f, subTitleLabelPaint)

        // Número PENDIENTE ACTUAL
        val liveGradeText = "%.1f%%".format(currentGrade)
        liveGradePaint.textSize = ((h * 0.13f) * fontScale).coerceIn(20f, 42f)
        liveGradePaint.color = Color.parseColor(getGradeColor(currentGrade))
        canvas.drawText(liveGradeText, 18f, h * 0.25f, liveGradePaint)

        // PENDIENTE MÁXIMA DEL TRAMO VISIBLE
        if (showMaxGrade) {
            val tramoMaxGrade = if (nextBlocks.isNotEmpty()) nextBlocks.maxOrNull()?.toDouble() ?: 12.8 else 12.8
            val labelMaxGrade = context.getString(R.string.label_max_gradient_tramo)
            subTitleLabelPaint.textSize = ((h * 0.045f) * fontScale).coerceIn(10f, 15f)
            canvas.drawText(labelMaxGrade, 18f, h * 0.32f, subTitleLabelPaint)

            val maxGradeText = "%.1f%%".format(tramoMaxGrade)
            maxGradePaint.textSize = ((h * 0.12f) * fontScale).coerceIn(18f, 36f)
            maxGradePaint.color = Color.parseColor(getGradeColor(tramoMaxGrade))
            canvas.drawText(maxGradeText, 18f, h * 0.43f, maxGradePaint)
        }

        // 3. Controles de Zoom Táctil en Esquina Superior Derecha [–] 🔍 500m [+]
        drawZoomOverlay(canvas, w, h)

        // 4. Perfil 3D con Hitos y Herraduras
        draw3DRibbonAndWalls(canvas, w, h)
    }

    private fun drawZoomOverlay(canvas: Canvas, w: Float, h: Float) {
        val zoomText = if (lookaheadMeters < 1000) "${lookaheadMeters}m" else "${lookaheadMeters / 1000}km"

        val pillW = (w * 0.38f).coerceIn(120f, 210f)
        val pillH = (h * 0.15f).coerceIn(28f, 44f)

        val marginR = 16f
        val marginT = 16f

        val right = w - marginR
        val left = right - pillW
        val top = marginT
        val bottom = top + pillH

        val rect = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rect, pillH / 2f, pillH / 2f, zoomBgPaint)
        canvas.drawRoundRect(rect, pillH / 2f, pillH / 2f, zoomBorderPaint)

        val btnW = pillW * 0.28f
        val textSize = (pillH * 0.55f)

        // Botón [-] (Zoom Out / Alejar)
        zoomBtnPaint.textSize = textSize
        val minusX = left + (btnW / 2f)
        val centerY = top + (pillH / 2f) + (textSize * 0.32f)
        canvas.drawText("–", minusX, centerY, zoomBtnPaint)

        // Línea divisoria izquierda
        canvas.drawLine(left + btnW, top + 4f, left + btnW, bottom - 4f, zoomBorderPaint)

        // Texto central (ej. 🔍 500m / 🔍 1km)
        zoomTextPaint.textSize = (pillH * 0.46f)
        val labelX = left + (pillW / 2f)
        canvas.drawText("🔍 $zoomText", labelX, centerY, zoomTextPaint)

        // Línea divisoria derecha
        canvas.drawLine(right - btnW, top + 4f, right - btnW, bottom - 4f, zoomBorderPaint)

        // Botón [+] (Zoom In / Acercar)
        val plusX = right - (btnW / 2f)
        canvas.drawText("+", plusX, centerY, zoomBtnPaint)
    }

    private fun draw3DBackwallGrid(
        canvas: Canvas,
        w: Float,
        h: Float,
        pointsX: FloatArray,
        pointsYBase: FloatArray,
        samples: Int,
        maxPeakHeight: Float,
        minElev: Float,
        maxElev: Float
    ) {
        val steps = 4
        axisTextPaint.textSize = (h * 0.038f).coerceIn(9f, 13f)
        axisTextPaint.textAlign = Paint.Align.LEFT

        val elevRange = (maxElev - minElev).coerceAtLeast(10f)

        for (k in 0..steps) {
            val ratio = k.toFloat() / steps
            val elevMark = (minElev + (ratio * elevRange)).toInt()

            for (j in 0 until samples - 1) {
                val x1 = pointsX[j]
                val y1 = pointsYBase[j] - (ratio * maxPeakHeight)

                val x2 = pointsX[j + 1]
                val y2 = pointsYBase[j + 1] - (ratio * maxPeakHeight)

                canvas.drawLine(x1, y1, x2, y2, gridPaint)
            }

            val labelX = (pointsX[0] - 28f).coerceAtLeast(6f)
            val labelY = pointsYBase[0] - (ratio * maxPeakHeight) + 4f
            canvas.drawText("${elevMark}m", labelX, labelY, axisTextPaint)
        }

        for (j in 0 until samples) {
            val x = pointsX[j]
            val yBottom = pointsYBase[j]
            val yTop = pointsYBase[j] - maxPeakHeight
            canvas.drawLine(x, yTop, x, yBottom, gridPaint)
        }
    }

    private fun draw3DRibbonAndWalls(canvas: Canvas, w: Float, h: Float) {
        val totalMetersAhead = lookaheadMeters.toDouble().coerceIn(200.0, 10000.0)

        val majorBlocksCount = if (totalMetersAhead > 1000.0) {
            (totalMetersAhead / 1000.0).toInt().coerceIn(2, 10)
        } else {
            (totalMetersAhead / blockSizeMeters.coerceAtLeast(10.0)).toInt().coerceIn(2, 10)
        }
        val majorSamples = majorBlocksCount + 1

        val microSubDivisions = if (totalMetersAhead >= 1000.0) 20 else 2
        val totalMicroSamples = (majorBlocksCount * microSubDivisions) + 1

        val startX = w * 0.08f
        val endX = w * 0.99f
        val baseGroundY = h * 0.88f
        val maxPeakHeight = h * 0.52f

        val microStepX = (endX - startX) / (totalMicroSamples - 1).coerceAtLeast(1)
        val microDistMeters = totalMetersAhead / (totalMicroSamples - 1)

        val microX = FloatArray(totalMicroSamples)
        val microYTop = FloatArray(totalMicroSamples)
        val microYBase = FloatArray(totalMicroSamples)
        val microElevations = FloatArray(totalMicroSamples)
        val microGrades = FloatArray(totalMicroSamples - 1)

        var accumulatedElev = currentElevation
        microElevations[0] = accumulatedElev.toFloat()

        for (i in 0 until totalMicroSamples - 1) {
            val majorBlockIdx = (i / microSubDivisions).coerceAtMost(nextBlocks.size - 1)
            val baseGrade = if (majorBlockIdx < nextBlocks.size) nextBlocks[majorBlockIdx].toDouble() else 4.0

            val distMeters = i * microDistMeters
            val microVariation = (sin(distMeters / 60.0) * 3.5) + (sin(distMeters / 140.0) * 4.5)
            val microGrade = (baseGrade + microVariation).coerceIn(-6.0, 22.0).toFloat()

            microGrades[i] = microGrade
            accumulatedElev += (microDistMeters * (microGrade / 100.0))
            microElevations[i + 1] = accumulatedElev.toFloat()
        }

        val minElev = microElevations.minOrNull() ?: currentElevation.toFloat()
        val maxElev = microElevations.maxOrNull() ?: (minElev + 50f)
        val elevRange = (maxElev - minElev).coerceAtLeast(15f)

        for (i in 0 until totalMicroSamples) {
            val progress = i.toFloat() / (totalMicroSamples - 1)
            val curveOffset = sin(progress * Math.PI * 1.5).toFloat() * (w * 0.08f)

            val px = startX + i * microStepX + curveOffset
            val normalizedHeight = ((microElevations[i] - minElev) / elevRange).coerceIn(0f, 1f)

            val pYTop = baseGroundY - (progress * (h * 0.05f)) - (normalizedHeight * maxPeakHeight)
            val pYBase = baseGroundY - (progress * (h * 0.05f))

            microX[i] = px
            microYTop[i] = pYTop
            microYBase[i] = pYBase
        }

        val majorX = FloatArray(majorSamples)
        val majorYTop = FloatArray(majorSamples)
        val majorYBase = FloatArray(majorSamples)
        val majorElevations = FloatArray(majorSamples)

        for (k in 0 until majorSamples) {
            val microIdx = (k * microSubDivisions).coerceAtMost(totalMicroSamples - 1)
            majorX[k] = microX[microIdx]
            majorYTop[k] = microYTop[microIdx]
            majorYBase[k] = microYBase[microIdx]
            majorElevations[k] = microElevations[microIdx]
        }

        draw3DBackwallGrid(canvas, w, h, majorX, majorYBase, majorSamples, maxPeakHeight, minElev, maxElev)

        for (i in 0 until totalMicroSamples - 1) {
            val grade = microGrades[i]

            val fillColor = if (grade < 0.0f) {
                Color.parseColor("#1E3A8A")
            } else {
                Color.parseColor(getGradeColor(grade.toDouble()))
            }

            wallPath.reset()
            wallPath.moveTo(microX[i], microYTop[i])
            wallPath.lineTo(microX[i + 1], microYTop[i + 1])
            wallPath.lineTo(microX[i + 1], microYBase[i + 1])
            wallPath.lineTo(microX[i], microYBase[i])
            wallPath.close()

            val bottomDarkHex = if (grade < 0.0f) "#020617" else "#0F172A"
            val wallShader = LinearGradient(
                microX[i], microYTop[i],
                microX[i], microYBase[i],
                fillColor, Color.parseColor(bottomDarkHex),
                Shader.TileMode.CLAMP
            )
            wallPaint.shader = wallShader
            canvas.drawPath(wallPath, wallPaint)
        }

        if (showHairpins) {
            for (hpRelDist in hairpins) {
                if (hpRelDist > totalMetersAhead) continue
                
                val progress = (hpRelDist / totalMetersAhead).toFloat()
                val curveOffset = sin(progress * Math.PI * 1.5).toFloat() * (w * 0.08f)
                val px = startX + (endX - startX) * progress + curveOffset

                val exactIndexF = progress * (totalMicroSamples - 1)
                val idx = exactIndexF.toInt().coerceIn(0, totalMicroSamples - 2)
                val rem = exactIndexF - idx
                val pyTop = microYTop[idx] + rem * (microYTop[idx + 1] - microYTop[idx])
                val pyBase = microYBase[idx] + rem * (microYBase[idx + 1] - microYBase[idx])

                canvas.drawLine(px, pyTop, px, pyBase, hairpinLinePaint)
            }
        }

        cotaTextPaint.textSize = ((h * 0.050f) * fontScale).coerceIn(10f, 18f)

        for (k in 0 until majorSamples) {
            val px = majorX[k]
            val pyTop = majorYTop[k]
            val pyBase = majorYBase[k]

            canvas.drawLine(px, pyTop, px, pyBase, cotaLinePaint)

            if (showCotas) {
                val cotaText = "${majorElevations[k].toInt()} m"
                canvas.save()
                val textOffsetX = if (k == 0) px + 8f else px - 5f
                val textOffsetY = pyBase - 12f
                canvas.translate(textOffsetX, textOffsetY)
                canvas.rotate(-90f)
                canvas.drawText(cotaText, 0f, 0f, cotaTextPaint)
                canvas.restore()
            }
        }

        for (i in 0 until totalMicroSamples - 1) {
            val grade = microGrades[i]
            val darkStrokeColor = getDarkGradeColor(grade.toDouble())

            lineStrokePaint.color = Color.parseColor(darkStrokeColor)
            canvas.drawLine(microX[i], microYTop[i], microX[i + 1], microYTop[i + 1], lineStrokePaint)
        }

        if (showPois) {
            poiTextPaint.textSize = ((h * 0.040f) * fontScale).coerceIn(10f, 16f)

            for (poi in pois) {
                val relDist = poi.relativeDistance
                if (relDist > totalMetersAhead) continue

                val progress = (relDist / totalMetersAhead).toFloat()
                val curveOffset = sin(progress * Math.PI * 1.5).toFloat() * (w * 0.08f)
                val px = startX + (endX - startX) * progress + curveOffset

                val exactIndexF = progress * (totalMicroSamples - 1)
                val idx = exactIndexF.toInt().coerceIn(0, totalMicroSamples - 2)
                val rem = exactIndexF - idx
                val pyTop = microYTop[idx] + rem * (microYTop[idx + 1] - microYTop[idx])

                val labelY = pyTop - (h * 0.16f)
                
                canvas.drawLine(px, pyTop, px, labelY + 12f, poiLinePaint)

                val text = "${poi.icon} ${poi.name}"
                val textWidth = poiTextPaint.measureText(text)
                
                val rectL = px - (textWidth / 2f) - 12f
                val rectT = labelY - poiTextPaint.textSize - 12f
                val rectR = px + (textWidth / 2f) + 12f
                val rectB = labelY + 12f
                
                tagRect.set(rectL, rectT, rectR, rectB)
                
                canvas.drawRoundRect(tagRect, 10f, 10f, poiBgPaint)
                canvas.drawText(text, px, labelY, poiTextPaint)
            }
        }

        if (showRamps) {
            for (k in 0 until majorBlocksCount) {
                val startIdx = k * microSubDivisions
                val endIdx = ((k + 1) * microSubDivisions).coerceAtMost(totalMicroSamples - 1)

                val steepRampsInKm = mutableListOf<Pair<Int, Float>>()
                for (i in startIdx until endIdx) {
                    val g = microGrades[i]
                    if (g >= rampMinSlope.toFloat() && g <= rampMaxSlope.toFloat()) {
                        steepRampsInKm.add(Pair(i, g))
                    }
                }

                val topSteepRamps = steepRampsInKm.sortedByDescending { it.second }.take(2)

                for ((i, grade) in topSteepRamps) {
                    val midX = (microX[i] + microX[i + 1]) / 2f
                    val midY = (microYTop[i] + microYTop[i + 1]) / 2f

                    val arrowTopY = midY - (h * 0.12f)
                    val arrowBottomY = midY - 6f

                    canvas.drawLine(midX, arrowTopY, midX, arrowBottomY, rampArrowPaint)

                    arrowPath.reset()
                    arrowPath.moveTo(midX, arrowBottomY)
                    arrowPath.lineTo(midX - 5f, arrowBottomY - 8f)
                    arrowPath.lineTo(midX + 5f, arrowBottomY - 8f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, rampArrowHeadPaint)

                    rampTextPaint.textSize = ((h * 0.055f) * fontScale).coerceIn(10f, 18f)
                    val rampLabel = "%.1f%%".format(grade)
                    canvas.drawText(rampLabel, midX, arrowTopY - 4f, rampTextPaint)
                }
            }
        }

        axisTextPaint.textSize = (h * 0.040f).coerceIn(9f, 13f)
        axisTextPaint.textAlign = Paint.Align.CENTER
        val majorDistMeters = (totalMetersAhead / majorBlocksCount).toInt()

        for (k in 0 until majorSamples) {
            val m = k * majorDistMeters
            val distLabel = if (totalMetersAhead >= 1000.0) {
                if (m < 1000) "${m}m" else "${m / 1000}km"
            } else {
                "${m}m"
            }
            val px = majorX[k]
            val pyBase = majorYBase[k]

            canvas.drawText(distLabel, px, pyBase + 12f, axisTextPaint)
        }

        val fontBaseSize = (h * 0.045f) * fontScale

        for (k in 0 until majorBlocksCount) {
            val avgGrade = if (k < nextBlocks.size) nextBlocks[k] else 4.0f
            val pctStr = "%.1f%%".format(avgGrade)

            val midX = (majorX[k] + majorX[k + 1]) / 2f
            val midYTop = (majorYTop[k] + majorYTop[k + 1]) / 2f
            val midYBase = (majorYBase[k] + majorYBase[k + 1]) / 2f
            val centerY = (midYTop + midYBase) / 2f

            val blockW = abs(majorX[k + 1] - majorX[k])
            val calcFontSize = (blockW * 0.28f * fontScale).coerceIn(10f, fontBaseSize.coerceAtLeast(18f))
            percentTextPaint.textSize = calcFontSize

            val textY = centerY + (calcFontSize * 0.35f)
            canvas.drawText(pctStr, midX, textY, percentTextPaint)
        }

        val riderIdx = 0
        val rx = microX[riderIdx]
        val ry = microYTop[riderIdx]

        canvas.drawCircle(rx, ry, 22f, beaconHaloPaint)
        canvas.drawCircle(rx, ry, 10f, beaconPaint)
    }

    private fun getGradeColor(grade: Double): String {
        return when {
            grade < 0.0 -> "#3B82F6"
            grade < 4.0 -> "#10B981"
            grade < 8.0 -> "#0EA5E9"
            grade < 10.0 -> "#F59E0B"
            grade < 15.0 -> "#EF4444"
            else -> "#A855F7"
        }
    }

    private fun getDarkGradeColor(grade: Double): String {
        return when {
            grade < 0.0 -> "#1E40AF"
            grade < 4.0 -> "#15803D"
            grade < 8.0 -> "#0284C7"
            grade < 10.0 -> "#B45309"
            grade < 15.0 -> "#B91C1C"
            else -> "#6B21A8"
        }
    }
}