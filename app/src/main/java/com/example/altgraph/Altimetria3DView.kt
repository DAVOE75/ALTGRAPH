package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.graphics.Typeface
import android.util.AttributeSet
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
        color = Color.parseColor("#52525B")
        strokeWidth = 1.2f
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
        strokeWidth = 7f
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
        color = Color.parseColor("#64748B")
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

    private val wallPath = Path()
    private val arrowPath = Path()

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
        rotate90: Boolean = false
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

        FontHelper.applyFontToPaint(titlePaint, fontFamilyKey)
        FontHelper.applyFontToPaint(subTitleLabelPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(liveGradePaint, fontFamilyKey)
        FontHelper.applyFontToPaint(maxGradePaint, fontFamilyKey)
        FontHelper.applyFontToPaint(percentTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(rampTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(cotaTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(axisTextPaint, fontFamilyKey)

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

        // PENDIENTE MÁXIMA DEL TRAMO VISIBLE (Opcional según preferencia del usuario)
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

        // 3. Rejilla Isometrica 3D de Suelo
        drawIsometricGrid(canvas, w, h)

        // 4. Perfil 3D con Sub-Bloques Micro-Relieve de 50m dentro de cada Kilómetro
        draw3DRibbonAndWalls(canvas, w, h)
    }

    private fun drawIsometricGrid(canvas: Canvas, w: Float, h: Float) {
        val groundY = h * 0.88f
        val gridLines = 5

        for (i in 0..gridLines) {
            val ratio = i.toFloat() / gridLines
            val x1 = w * 0.04f + ratio * (w * 0.25f)
            val y1 = groundY - ratio * (h * 0.12f)

            val x2 = w * 0.78f + ratio * (w * 0.25f)
            val y2 = groundY - ratio * (h * 0.12f)

            canvas.drawLine(x1, y1, x2, y2, gridPaint)
        }
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

        // 1. Líneas horizontales de altitud en PERSPECTIVA ISOMÉTRICA 3D
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

        // 2. Columnas verticales de rejilla en PERSPECTIVA ISOMÉTRICA 3D
        for (j in 0 until samples) {
            val x = pointsX[j]
            val yBottom = pointsYBase[j]
            val yTop = pointsYBase[j] - maxPeakHeight
            canvas.drawLine(x, yTop, x, yBottom, gridPaint)
        }
    }

    private fun draw3DRibbonAndWalls(canvas: Canvas, w: Float, h: Float) {
        val totalMetersAhead = lookaheadMeters.toDouble().coerceIn(200.0, 10000.0)

        // Divisores principales (ej. 1 km o blockSizeMeters)
        val majorBlocksCount = if (totalMetersAhead > 1000.0) {
            (totalMetersAhead / 1000.0).toInt().coerceIn(2, 10)
        } else {
            (totalMetersAhead / blockSizeMeters.coerceAtLeast(10.0)).toInt().coerceIn(2, 10)
        }
        val majorSamples = majorBlocksCount + 1

        // SUB-DIVISIÓN MICRO-RELIEVE (Bloques finos de 50m dentro de cada kilómetro)
        val microSubDivisions = if (totalMetersAhead >= 1000.0) 20 else 2 // 20 micro-bloques de 50m por cada 1km
        val totalMicroSamples = (majorBlocksCount * microSubDivisions) + 1

        val startX = w * 0.08f
        val endX = w * 0.98f
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

        // 1. Calcular micro-elevación y gradiente de 50m para cada sub-tramo
        for (i in 0 until totalMicroSamples - 1) {
            val majorBlockIdx = (i / microSubDivisions).coerceAtMost(nextBlocks.size - 1)
            val baseGrade = if (majorBlockIdx < nextBlocks.size) nextBlocks[majorBlockIdx].toDouble() else 4.0

            // Oscilación realista de micro-relieve de 50m (llanos, rampas y descansillos)
            val distMeters = i * microDistMeters
            val microVariation = (sin(distMeters / 60.0) * 3.5) + (sin(distMeters / 140.0) * 4.5)
            val microGrade = (baseGrade + microVariation).coerceIn(-6.0, 22.0).toFloat()

            microGrades[i] = microGrade
            accumulatedElev += (microDistMeters * (microGrade / 100.0))
            microElevations[i + 1] = accumulatedElev.toFloat()
        }

        // 2. Escala estricta matemática de altitud
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

        // Extraer puntos de divisores principales (ej. Cada 1 km)
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

        // DIBUJAR REJILLA TRASERA DE ALTITUD EN PERSPECTIVA 3D
        draw3DBackwallGrid(canvas, w, h, majorX, majorYBase, majorSamples, maxPeakHeight, minElev, maxElev)

        // DIBUJAR PAREDES DE EXTROSIÓN 3D EN SUB-BLOQUES FINOS DE 50M (LLANOS Y RAMPAS DENTRO DE CADA KM)
        for (i in 0 until totalMicroSamples - 1) {
            val grade = microGrades[i]

            val colorHex = getGradeColor(grade.toDouble())
            val fillColor = Color.parseColor(colorHex)

            wallPath.reset()
            wallPath.moveTo(microX[i], microYTop[i])
            wallPath.lineTo(microX[i + 1], microYTop[i + 1])
            wallPath.lineTo(microX[i + 1], microYBase[i + 1])
            wallPath.lineTo(microX[i], microYBase[i])
            wallPath.close()

            val wallShader = LinearGradient(
                microX[i], microYTop[i],
                microX[i], microYBase[i],
                fillColor, Color.parseColor("#0F172A"),
                Shader.TileMode.CLAMP
            )
            wallPaint.shader = wallShader
            canvas.drawPath(wallPath, wallPaint)
        }

        // DIBUJAR LÍNEAS DIVISORIAS PRINCIPALES Y COTAS DE ALTITUD EN CADA KM (O BLOQUE)
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

        // DIBUJAR CINTA SUPERIOR 3D CONTINUA
        for (i in 0 until totalMicroSamples - 1) {
            val grade = microGrades[i]
            val darkStrokeColor = getDarkGradeColor(grade.toDouble())

            lineStrokePaint.color = Color.parseColor(darkStrokeColor)
            canvas.drawLine(microX[i], microYTop[i], microX[i + 1], microYTop[i + 1], lineStrokePaint)
        }

        // DIBUJAR RAMPA DURA (≥ 10% para ≥ 20m) CON FLECHA Y INDICADOR FLOTANTE
        if (showRamps) {
            for (i in 0 until totalMicroSamples - 1) {
                val grade = microGrades[i]
                if (grade >= 10.0f && i % microSubDivisions == microSubDivisions / 2) {
                    val midX = (microX[i] + microX[i + 1]) / 2f
                    val midY = (microYTop[i] + microYTop[i + 1]) / 2f

                    val arrowTopY = midY - (h * 0.16f)
                    val arrowBottomY = midY - 8f

                    canvas.drawLine(midX, arrowTopY, midX, arrowBottomY, rampArrowPaint)

                    arrowPath.reset()
                    arrowPath.moveTo(midX, arrowBottomY)
                    arrowPath.lineTo(midX - 6f, arrowBottomY - 10f)
                    arrowPath.lineTo(midX + 6f, arrowBottomY - 10f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, rampArrowHeadPaint)

                    rampTextPaint.textSize = ((h * 0.065f) * fontScale).coerceIn(12f, 22f)
                    val rampLabel = "%.1f%%".format(grade)
                    canvas.drawText(rampLabel, midX, arrowTopY - 6f, rampTextPaint)
                }
            }
        }

        // DIBUJAR EJE X CON MARCAS PRINCIPALES EN KM / M
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

        // DIBUJAR PORCENTAJES PROMEDIO (%) DE CADA KILÓMETRO (O BLOQUE) CENTRADOS EN CADA SECCIÓN
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

        // DIBUJAR MARCADOR BEACON 3D DEL CICLISTA
        val riderIdx = 0
        val rx = microX[riderIdx]
        val ry = microYTop[riderIdx]

        canvas.drawCircle(rx, ry, 22f, beaconHaloPaint)
        canvas.drawCircle(rx, ry, 10f, beaconPaint)
    }

    private fun getGradeColor(grade: Double): String {
        return when {
            grade < 0.0 -> "#3B82F6"  // Azul descensos
            grade < 4.0 -> "#10B981"  // Verde (0% - 4%)
            grade < 8.0 -> "#0EA5E9"  // Celeste (4% - 8%)
            grade < 10.0 -> "#F59E0B" // Amarillo Ocre (8% - 10%)
            grade < 15.0 -> "#EF4444" // Rojo Carmesí (10% - 15%)
            else -> "#A855F7"         // Violeta rampa dura
        }
    }

    private fun getDarkGradeColor(grade: Double): String {
        return when {
            grade < 0.0 -> "#1E40AF"  // Azul oscuro descensos
            grade < 4.0 -> "#15803D"  // Verde oscuro
            grade < 8.0 -> "#0284C7"  // Celeste/Azul oscuro
            grade < 10.0 -> "#B45309"  // Amarillo/Ocre oscuro
            grade < 15.0 -> "#B91C1C" // Rojo oscuro
            else -> "#6B21A8"         // Violeta/Púrpura oscuro
        }
    }
}