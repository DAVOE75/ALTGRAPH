package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
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

    private val liveGradePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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

    private val tagBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#18181B")
        style = Paint.Style.FILL
    }

    private val tagBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        strokeWidth = 2.5f
        style = Paint.Style.STROKE
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
        FontHelper.applyFontToPaint(liveGradePaint, fontFamilyKey)
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

        // 2. Título "Altimetría 3D" discreto en la parte superior izquierda
        val titleText = context.getString(R.string.data_type_altimetria_3d_title)
        titlePaint.textSize = ((h * 0.050f) * fontScale).coerceIn(12f, 18f)
        canvas.drawText(titleText, 16f, h * 0.05f, titlePaint)

        // 3. Rejilla Isometrica 3D de Suelo
        drawIsometricGrid(canvas, w, h)

        // 4. Perfil 3D a Pantalla Completa 100% (Estirado de extremo a extremo)
        draw3DRibbonAndWalls(canvas, w, h)
    }

    private fun drawIsometricGrid(canvas: Canvas, w: Float, h: Float) {
        val groundY = h * 0.94f
        val gridLines = 5

        for (i in 0..gridLines) {
            val ratio = i.toFloat() / gridLines
            val x1 = w * 0.01f + ratio * (w * 0.25f)
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
        val totalMetersAhead = lookaheadMeters.toDouble().coerceIn(200.0, 500.0)
        val blocksCount = (totalMetersAhead / blockSizeMeters.coerceAtLeast(10.0)).toInt().coerceIn(2, 10)
        val samples = blocksCount + 1

        // HUELLA 3D ESTIRADA AL 100% DEL ANCHO Y ALTO DE PANTALLA
        val startX = w * 0.01f
        val endX = w * 0.98f
        val baseGroundY = h * 0.94f
        val maxPeakHeight = h * 0.82f // Expande el relieve 3D para ocupar el 82% de la altura total de pantalla

        val stepX = (endX - startX) / (samples - 1).coerceAtLeast(1)

        val pointsX = FloatArray(samples)
        val pointsYTop = FloatArray(samples)
        val pointsYBase = FloatArray(samples)
        val pointElevations = FloatArray(samples)

        var accumulatedElev = currentElevation

        // 1. Calcular altitud exacta en metros para cada punto del perfil
        pointElevations[0] = accumulatedElev.toFloat()
        for (i in 1 until samples) {
            val segmentGrade = if (i - 1 < nextBlocks.size) nextBlocks[i - 1].toDouble() else 4.0
            val segmentMeters = blockSizeMeters.coerceAtLeast(20.0)
            accumulatedElev += (segmentMeters * (segmentGrade / 100.0))
            pointElevations[i] = accumulatedElev.toFloat()
        }

        // 2. Escala estricta matemática de altitud
        val minElev = pointElevations.minOrNull() ?: currentElevation.toFloat()
        val maxElev = pointElevations.maxOrNull() ?: (minElev + 50f)
        val elevRange = (maxElev - minElev).coerceAtLeast(15f)

        for (i in 0 until samples) {
            val progress = i.toFloat() / (samples - 1)
            val curveOffset = sin(progress * Math.PI * 1.5).toFloat() * (w * 0.08f)

            val px = startX + i * stepX + curveOffset
            val normalizedHeight = ((pointElevations[i] - minElev) / elevRange).coerceIn(0f, 1f)

            val pYTop = baseGroundY - (progress * (h * 0.04f)) - (normalizedHeight * maxPeakHeight)
            val pYBase = baseGroundY - (progress * (h * 0.04f))

            pointsX[i] = px
            pointsYTop[i] = pYTop
            pointsYBase[i] = pYBase
        }

        // DIBUJAR REJILLA TRASERA DE ALTITUD EN PERSPECTIVA 3D
        draw3DBackwallGrid(canvas, w, h, pointsX, pointsYBase, samples, maxPeakHeight, minElev, maxElev)

        // DIBUJAR PAREDES DE EXTROSIÓN 3D (Relieve)
        for (i in 0 until samples - 1) {
            val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f

            val colorHex = getGradeColor(grade.toDouble())
            val fillColor = Color.parseColor(colorHex)

            wallPath.reset()
            wallPath.moveTo(pointsX[i], pointsYTop[i])
            wallPath.lineTo(pointsX[i + 1], pointsYTop[i + 1])
            wallPath.lineTo(pointsX[i + 1], pointsYBase[i + 1])
            wallPath.lineTo(pointsX[i], pointsYBase[i])
            wallPath.close()

            val wallShader = LinearGradient(
                pointsX[i], pointsYTop[i],
                pointsX[i], pointsYBase[i],
                fillColor, Color.parseColor("#0F172A"),
                Shader.TileMode.CLAMP
            )
            wallPaint.shader = wallShader
            canvas.drawPath(wallPath, wallPaint)
        }

        // DIBUJAR LÍNEAS VERTICALES DE LÍMITE Y COTAS DE ALTITUD EN CADA TRAMO ROTADAS A 90 GRADOS
        cotaTextPaint.textSize = ((h * 0.050f) * fontScale).coerceIn(10f, 18f)

        for (i in 0 until samples) {
            canvas.drawLine(pointsX[i], pointsYTop[i], pointsX[i], pointsYBase[i] + 10f, cotaLinePaint)

            if (showCotas) {
                val cotaText = "${pointElevations[i].toInt()} m"
                canvas.save()
                canvas.translate(pointsX[i] - 5f, pointsYBase[i] - 6f)
                canvas.rotate(-90f)
                canvas.drawText(cotaText, 0f, 0f, cotaTextPaint)
                canvas.restore()
            }
        }

        // DIBUJAR CINTA SUPERIOR 3D
        for (i in 0 until samples - 1) {
            val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f
            val darkStrokeColor = getDarkGradeColor(grade.toDouble())

            lineStrokePaint.color = Color.parseColor(darkStrokeColor)
            canvas.drawLine(pointsX[i], pointsYTop[i], pointsX[i + 1], pointsYTop[i + 1], lineStrokePaint)
        }

        // DIBUJAR RAMPA DURA (≥ 10% para ≥ 20m) CON FLECHA Y INDICADOR FLOTANTE
        if (showRamps) {
            for (i in 0 until samples - 1) {
                val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f
                if (grade >= 10.0f) {
                    val midX = (pointsX[i] + pointsX[i + 1]) / 2f
                    val midY = (pointsYTop[i] + pointsYTop[i + 1]) / 2f

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

        // DIBUJAR EJE X CON MARCAS DE DISTANCIA EN METROS SEGÚN ANTICIPACIÓN
        axisTextPaint.textSize = (h * 0.042f).coerceIn(9f, 14f)
        axisTextPaint.textAlign = Paint.Align.CENTER
        val stepDistMeters = (totalMetersAhead / blocksCount).toInt()

        for (i in 0 until samples) {
            val distLabel = "${i * stepDistMeters}m"
            val px = pointsX[i]
            val pyBase = pointsYBase[i]

            canvas.drawLine(px, pyBase, px, pyBase + 6f, cotaLinePaint)
            canvas.drawText(distLabel, px, pyBase + 18f, axisTextPaint)
        }

        // DIBUJAR PORCENTAJES (%) DIRECTAMENTE SOBRE CADA BLOQUE 3D
        val fontBaseSize = (h * 0.045f) * fontScale

        for (i in 0 until samples - 1) {
            val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f
            val pctStr = if (blockSizeMeters <= 50.0) {
                if (grade % 1.0f == 0.0f) "%.0f%%".format(grade) else "%.1f%%".format(grade)
            } else {
                "%.0f%%".format(grade)
            }

            val midX = (pointsX[i] + pointsX[i + 1]) / 2f
            val midYTop = (pointsYTop[i] + pointsYTop[i + 1]) / 2f
            val midYBase = (pointsYBase[i] + pointsYBase[i + 1]) / 2f
            val centerY = (midYTop + midYBase) / 2f

            val blockW = abs(pointsX[i + 1] - pointsX[i])
            val calcFontSize = (blockW * 0.30f * fontScale).coerceIn(10f, fontBaseSize.coerceAtLeast(18f))
            percentTextPaint.textSize = calcFontSize

            val textY = centerY + (calcFontSize * 0.35f)
            canvas.drawText(pctStr, midX, textY, percentTextPaint)
        }

        // DIBUJAR MARCADOR BEACON 3D DEL CICLISTA Y GLOBO FLOTANTE DE PENDIENTE ACTUAL (Conforme la bici avanza)
        val riderIdx = 0
        val rx = pointsX[riderIdx]
        val ry = pointsYTop[riderIdx]

        canvas.drawCircle(rx, ry, 22f, beaconHaloPaint)
        canvas.drawCircle(rx, ry, 10f, beaconPaint)

        // GLOBO FLOTANTE 3D CON PENDIENTE ACTUAL EN TIEMPO REAL SOBRE EL CICLISTA
        drawRiderLiveGradeBalloon(canvas, w, h, rx, ry)
    }

    private fun drawRiderLiveGradeBalloon(canvas: Canvas, w: Float, h: Float, rx: Float, ry: Float) {
        val gradeColorHex = getGradeColor(currentGrade)
        liveGradePaint.color = Color.parseColor(gradeColorHex)

        val fontFontSize = ((h * 0.10f) * fontScale).coerceIn(18f, 36f)
        liveGradePaint.textSize = fontFontSize

        val balloonText = "📍 %.1f%%".format(currentGrade)
        val textW = liveGradePaint.measureText(balloonText)
        val rectW = textW + 20f
        val rectH = fontFontSize + 14f

        val rectL = (rx + 10f).coerceAtMost(w - rectW - 10f)
        val rectT = (ry - rectH - 18f).coerceAtLeast(10f)
        val rectR = rectL + rectW
        val rectB = rectT + rectH

        tagRect.set(rectL, rectT, rectR, rectB)

        // Fondo del globo flotante en tono oscuro
        tagBgPaint.color = Color.parseColor("#18181B")
        canvas.drawRoundRect(tagRect, 12f, 12f, tagBgPaint)

        // Borde del globo en el color dinámico de la rampa actual
        tagBorderPaint.color = Color.parseColor(gradeColorHex)
        canvas.drawRoundRect(tagRect, 12f, 12f, tagBorderPaint)

        // Puntero conector desde la bici hacia el globo
        canvas.drawLine(rx, ry, rectL + (rectW / 2f), rectB, tagBorderPaint)

        // Texto de la pendiente actual dentro del globo
        val txtY = rectT + (rectH * 0.72f)
        canvas.drawText(balloonText, rectL + 10f, txtY, liveGradePaint)
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