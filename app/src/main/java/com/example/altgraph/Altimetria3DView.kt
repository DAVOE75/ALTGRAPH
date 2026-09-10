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
        color = Color.parseColor("#09090B")
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#27272A")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val cotaLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#52525B")
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }

    private val cotaTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E4E4E7")
        typeface = Typeface.DEFAULT_BOLD
    }

    private val rampArrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EF4444")
        strokeWidth = 3f
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
        color = Color.parseColor("#38BDF8")
        strokeWidth = 5f
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
        color = Color.parseColor("#71717A")
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val percentTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        setShadowLayer(6f, 0f, 0f, Color.BLACK)
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

    private val ribbonPath = Path()
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
        showRamps: Boolean = true
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
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0 || h <= 0) return

        // 1. Fondo Oscuro
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // 2. Encabezado Título ("Altimetría 3D")
        val titleText = context.getString(R.string.data_type_altimetria_3d_title)
        titlePaint.textSize = ((h * 0.065f) * fontScale).coerceIn(14f, 22f)
        canvas.drawText(titleText, 20f, h * 0.07f, titlePaint)

        // Etiqueta PENDIENTE ACTUAL
        val labelCurrentGrade = context.getString(R.string.label_current_gradient)
        subTitleLabelPaint.textSize = ((h * 0.045f) * fontScale).coerceIn(10f, 15f)
        canvas.drawText(labelCurrentGrade, 20f, h * 0.13f, subTitleLabelPaint)

        // Número PENDIENTE ACTUAL (Grande)
        val liveGradeText = "%.1f%%".format(currentGrade)
        liveGradePaint.textSize = ((h * 0.14f) * fontScale).coerceIn(20f, 44f)
        liveGradePaint.color = Color.parseColor(getGradeColor(currentGrade))
        canvas.drawText(liveGradeText, 20f, h * 0.25f, liveGradePaint)

        // Etiqueta PENDIENTE MÁX. TRAMO
        val tramoMaxGrade = if (nextBlocks.isNotEmpty()) nextBlocks.maxOrNull()?.toDouble() ?: 12.8 else 12.8
        val labelMaxGrade = context.getString(R.string.label_max_gradient_tramo)
        subTitleLabelPaint.textSize = ((h * 0.045f) * fontScale).coerceIn(10f, 15f)
        canvas.drawText(labelMaxGrade, 20f, h * 0.32f, subTitleLabelPaint)

        // Número PENDIENTE MÁX. TRAMO (Grande justo debajo)
        val maxGradeText = "%.1f%%".format(tramoMaxGrade)
        maxGradePaint.textSize = ((h * 0.13f) * fontScale).coerceIn(18f, 38f)
        maxGradePaint.color = Color.parseColor(getGradeColor(tramoMaxGrade))
        canvas.drawText(maxGradeText, 20f, h * 0.43f, maxGradePaint)

        // 3. Rejilla Isometrica 3D de Suelo
        drawIsometricGrid(canvas, w, h)

        // 4. Perfil 3D con Cotas Verticales y Marcadores de Rampas Duras
        draw3DRibbonAndWalls(canvas, w, h)
    }

    private fun drawIsometricGrid(canvas: Canvas, w: Float, h: Float) {
        val groundY = h * 0.88f
        val gridLines = 5

        for (i in 0..gridLines) {
            val ratio = i.toFloat() / gridLines
            val x1 = w * 0.04f + ratio * (w * 0.22f)
            val y1 = groundY - ratio * (h * 0.14f)

            val x2 = w * 0.78f + ratio * (w * 0.22f)
            val y2 = groundY - ratio * (h * 0.14f)

            canvas.drawLine(x1, y1, x2, y2, gridPaint)
        }
    }

    private fun draw3DRibbonAndWalls(canvas: Canvas, w: Float, h: Float) {
        val totalMetersAhead = lookaheadMeters.toDouble().coerceIn(200.0, 500.0)
        val blocksCount = (totalMetersAhead / blockSizeMeters.coerceAtLeast(10.0)).toInt().coerceIn(2, 10)
        val samples = blocksCount + 1

        val startX = w * 0.05f
        val endX = w * 0.96f
        val baseGroundY = h * 0.86f
        val maxPeakHeight = h * 0.44f

        val stepX = (endX - startX) / (samples - 1).coerceAtLeast(1)

        val pointsX = FloatArray(samples)
        val pointsYTop = FloatArray(samples)
        val pointsYBase = FloatArray(samples)
        val pointElevations = FloatArray(samples)

        var accumulatedElev = currentElevation

        for (i in 0 until samples) {
            val grade = if (i < nextBlocks.size) nextBlocks[i].toDouble() else ((i % 4) * 2.5 + 2.0)

            if (i > 0) {
                val segmentMeters = blockSizeMeters.coerceAtLeast(20.0)
                accumulatedElev += (segmentMeters * (grade / 100.0))
            }
            pointElevations[i] = accumulatedElev.toFloat()

            val progress = i.toFloat() / (samples - 1)
            val curveOffset = sin(progress * Math.PI * 1.5).toFloat() * (w * 0.06f)

            val px = startX + i * stepX + curveOffset
            val normalizedHeight = (grade.coerceIn(-5.0, 20.0) + 5.0) / 25.0
            val pYTop = baseGroundY - (progress * (h * 0.08f)) - (normalizedHeight * maxPeakHeight).toFloat()
            val pYBase = baseGroundY - (progress * (h * 0.08f))

            pointsX[i] = px
            pointsYTop[i] = pYTop
            pointsYBase[i] = pYBase
        }

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
        cotaTextPaint.textSize = ((h * 0.052f) * fontScale).coerceIn(10f, 18f)

        for (i in 0 until samples) {
            // Línea vertical que baja desde el perfil
            canvas.drawLine(pointsX[i], pointsYTop[i], pointsX[i], pointsYBase[i] + 12f, cotaLinePaint)

            // Cota de altitud rotada a 90 grados pegada a la línea de separación si está activada
            if (showCotas) {
                val cotaText = "${pointElevations[i].toInt()} m"
                canvas.save()
                canvas.translate(pointsX[i] - 5f, pointsYBase[i] - 8f)
                canvas.rotate(-90f)
                canvas.drawText(cotaText, 0f, 0f, cotaTextPaint)
                canvas.restore()
            }
        }

        // DIBUJAR CINTA SUPERIOR 3D
        ribbonPath.reset()
        ribbonPath.moveTo(pointsX[0], pointsYTop[0])
        for (i in 1 until samples) {
            ribbonPath.lineTo(pointsX[i], pointsYTop[i])
        }
        canvas.drawPath(ribbonPath, lineStrokePaint)

        // DIBUJAR RAMPA DURA (≥ 10% para ≥ 20m) CON FLECHA Y INDICADOR FLOTANTE si está activado
        if (showRamps) {
            for (i in 0 until samples - 1) {
                val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f
                if (grade >= 10.0f) {
                    val midX = (pointsX[i] + pointsX[i + 1]) / 2f
                    val midY = (pointsYTop[i] + pointsYTop[i + 1]) / 2f

                    val arrowTopY = midY - (h * 0.18f)
                    val arrowBottomY = midY - 8f

                    // Flecha apuntando a la rampa
                    canvas.drawLine(midX, arrowTopY, midX, arrowBottomY, rampArrowPaint)

                    arrowPath.reset()
                    arrowPath.moveTo(midX, arrowBottomY)
                    arrowPath.lineTo(midX - 6f, arrowBottomY - 10f)
                    arrowPath.lineTo(midX + 6f, arrowBottomY - 10f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, rampArrowHeadPaint)

                    // Texto del % sobre la flecha (ej. 10%, 12%, 15%)
                    rampTextPaint.textSize = ((h * 0.065f) * fontScale).coerceIn(12f, 22f)
                    val rampLabel = "%.0f%%".format(grade)
                    canvas.drawText(rampLabel, midX, arrowTopY - 6f, rampTextPaint)
                }
            }
        }

        // DIBUJAR EJE X CON MARCAS DE DISTANCIA EN METROS SEGÚN ANTICIPACIÓN
        axisTextPaint.textSize = (h * 0.045f).coerceIn(10f, 15f)
        val stepDistMeters = (totalMetersAhead / blocksCount).toInt()

        for (i in 0 until samples) {
            val distLabel = "${i * stepDistMeters}m"
            val lx = pointsX[i]
            val ly = pointsYBase[i] + 16f
            canvas.drawText(distLabel, lx, ly, axisTextPaint)
            canvas.drawLine(pointsX[i], pointsYBase[i], pointsX[i], pointsYBase[i] + 6f, gridPaint)
        }

        // DIBUJAR PORCENTAJES (%) DIRECTAMENTE SOBRE CADA BLOQUE 3D (SIN CUADRO GRIS, MÁS GRANDE Y ADAPTADO AL ANCHO)
        val fontBaseSize = (h * 0.090f) * fontScale

        for (i in 0 until samples - 1) {
            val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f
            val pctStr = "%.0f%%".format(grade)

            val midX = (pointsX[i] + pointsX[i + 1]) / 2f
            val midYTop = (pointsYTop[i] + pointsYTop[i + 1]) / 2f
            val midYBase = (pointsYBase[i] + pointsYBase[i + 1]) / 2f
            val centerY = (midYTop + midYBase) / 2f

            val blockW = abs(pointsX[i + 1] - pointsX[i])
            val calcFontSize = (blockW * 0.58f * fontScale).coerceIn(18f, fontBaseSize.coerceAtLeast(38f))
            percentTextPaint.textSize = calcFontSize

            val textY = centerY + (calcFontSize * 0.35f)
            canvas.drawText(pctStr, midX, textY, percentTextPaint)
        }

        // DIBUJAR MARCADOR BEACON 3D DEL CICLISTA
        val riderIdx = 0
        val rx = pointsX[riderIdx]
        val ry = pointsYTop[riderIdx]

        canvas.drawCircle(rx, ry, 22f, beaconHaloPaint)
        canvas.drawCircle(rx, ry, 10f, beaconPaint)
    }

    private fun getGradeColor(grade: Double): String {
        return when {
            grade < 3.0 -> "#22C55E" // Verde
            grade < 6.0 -> "#EAB308" // Amarillo
            grade < 9.0 -> "#F97316" // Naranja
            grade < 12.0 -> "#EF4444" // Rojo
            else -> "#A855F7"         // Violeta rampa dura
        }
    }
}