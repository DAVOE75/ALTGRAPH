package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
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

    private val mountainBodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private val ribbonTopPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val ribbonStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private val verticalLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#71717A")
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }

    private val cotaTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E4E4E7")
        typeface = Typeface.DEFAULT_BOLD
    }

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
    }

    private val tableBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F4F4F5")
        style = Paint.Style.FILL
    }

    private val tableBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#27272A")
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }

    private val tableTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val tableNegativeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EF4444")
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val tableSubTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#52525B")
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.CENTER
    }

    private val rampArrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EF4444")
        strokeWidth = 2.5f
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

    private val beaconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        style = Paint.Style.FILL
    }

    private val beaconHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4038BDF8")
        style = Paint.Style.FILL
    }

    private val legendTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#A1A1AA")
        textSize = 10f
        textAlign = Paint.Align.CENTER
    }

    private val legendSwatchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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

    private val mountainPath = Path()
    private val ribbonPath = Path()
    private val subSegmentPath = Path()
    private val arrowPath = Path()
    private val tableRect = RectF()
    private val swatchRect = RectF()

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

        // 2. Título "Altimetría 3D"
        val titleText = context.getString(R.string.data_type_altimetria_3d_title)
        titlePaint.textSize = (h * 0.10f).coerceIn(20f, 36f)
        canvas.drawText(titleText, 16f, h * 0.12f, titlePaint)

        // 3. Renderizado Principal de la Montaña Montnegre 3D
        drawProMontnegreProfile(canvas, w, h)
    }

    private fun drawProMontnegreProfile(canvas: Canvas, w: Float, h: Float) {
        val totalMetersAhead = lookaheadMeters.toDouble().coerceIn(200.0, 500.0)
        val blocksCount = (totalMetersAhead / blockSizeMeters.coerceAtLeast(10.0)).toInt().coerceIn(2, 10)
        val samples = blocksCount + 1

        val startX = w * 0.05f
        val endX = w * 0.95f
        val tableTopY = h * 0.74f  // Inicio de la tabla inferior
        val baseGroundY = tableTopY - 4f
        val maxPeakHeight = h * 0.44f

        val stepX = (endX - startX) / (samples - 1).coerceAtLeast(1)

        val pointsX = FloatArray(samples)
        val pointsYTop = FloatArray(samples)
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

            pointsX[i] = px
            pointsYTop[i] = pYTop
        }

        // 1. CUERPO DE LA MONTAÑA (Relleno Negro Macizo #000000)
        mountainPath.reset()
        mountainPath.moveTo(pointsX[0], pointsYTop[0])
        for (i in 1 until samples) {
            mountainPath.lineTo(pointsX[i], pointsYTop[i])
        }
        mountainPath.lineTo(pointsX[samples - 1], baseGroundY)
        mountainPath.lineTo(pointsX[0], baseGroundY)
        mountainPath.close()

        canvas.drawPath(mountainPath, mountainBodyPaint)

        // 2. CINTA DE PERFIL 3D CON FRAJAS DE COLORES Y SUBDIVISIÓN
        val ribbonThickness = h * 0.035f

        for (i in 0 until samples - 1) {
            val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f
            val colorHex = getGradeColorPro(grade.toDouble())

            subSegmentPath.reset()
            subSegmentPath.moveTo(pointsX[i], pointsYTop[i])
            subSegmentPath.lineTo(pointsX[i + 1], pointsYTop[i + 1])
            subSegmentPath.lineTo(pointsX[i + 1], pointsYTop[i + 1] + ribbonThickness)
            subSegmentPath.lineTo(pointsX[i], pointsYTop[i] + ribbonThickness)
            subSegmentPath.close()

            ribbonTopPaint.color = Color.parseColor(colorHex)
            canvas.drawPath(subSegmentPath, ribbonTopPaint)
        }

        // Borde superior de la cinta de carretera
        ribbonPath.reset()
        ribbonPath.moveTo(pointsX[0], pointsYTop[0])
        for (i in 1 until samples) {
            ribbonPath.lineTo(pointsX[i], pointsYTop[i])
        }
        canvas.drawPath(ribbonPath, ribbonStrokePaint)

        // 3. LÍNEAS VERTICALES Y COTAS ROTADAS A 90º DENTRO DE LA MONTAÑA (Estilo Montnegre)
        cotaTextPaint.textSize = ((h * 0.045f) * fontScale).coerceIn(10f, 16f)

        for (i in 0 until samples) {
            // Línea vertical desde la cinta hasta la base
            canvas.drawLine(pointsX[i], pointsYTop[i] + ribbonThickness, pointsX[i], baseGroundY, verticalLinePaint)

            // Cota rotada 90 grados pegada a la línea dentro del cuerpo negro
            if (showCotas) {
                val cotaText = "${pointElevations[i].toInt()} m"
                canvas.save()
                canvas.translate(pointsX[i] - 4f, baseGroundY - 10f)
                canvas.rotate(-90f)
                canvas.drawText(cotaText, 0f, 0f, cotaTextPaint)
                canvas.restore()
            }
        }

        // 4. INDICADORES DE RAMPAS DURAS (≥10%) CON FLECHA ROJA Y PORCENTAJE FLOTANTE
        if (showRamps) {
            for (i in 0 until samples - 1) {
                val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f
                if (grade >= 10.0f) {
                    val midX = (pointsX[i] + pointsX[i + 1]) / 2f
                    val midY = (pointsYTop[i] + pointsYTop[i + 1]) / 2f

                    val arrowTopY = midY - (h * 0.14f)
                    val arrowBottomY = midY - 6f

                    canvas.drawLine(midX, arrowTopY, midX, arrowBottomY, rampArrowPaint)

                    arrowPath.reset()
                    arrowPath.moveTo(midX, arrowBottomY)
                    arrowPath.lineTo(midX - 5f, arrowBottomY - 8f)
                    arrowPath.lineTo(midX + 5f, arrowBottomY - 8f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, rampArrowHeadPaint)

                    rampTextPaint.textSize = ((h * 0.055f) * fontScale).coerceIn(11f, 20f)
                    val rampLabel = "%.0f%%".format(grade)
                    canvas.drawText(rampLabel, midX, arrowTopY - 4f, rampTextPaint)
                }
            }
        }

        // 5. TABLA INFERIOR DE SUBDIVISIÓN DE % Y KILÓMETROS (Como en la foto de Montnegre)
        drawSubdivisionTableBar(canvas, w, h, pointsX, samples, totalMetersAhead)

        // 6. MARCADOR 3D BEACON DEL CICLISTA
        val rx = pointsX[0]
        val ry = pointsYTop[0]
        canvas.drawCircle(rx, ry, 18f, beaconHaloPaint)
        canvas.drawCircle(rx, ry, 8f, beaconPaint)
    }

    private fun drawSubdivisionTableBar(
        canvas: Canvas,
        w: Float,
        h: Float,
        pointsX: FloatArray,
        samples: Int,
        totalMetersAhead: Double
    ) {
        val tableTop = h * 0.74f
        val tableBottom = h * 0.90f
        val tableHeight = tableBottom - tableTop

        tableRect.set(10f, tableTop, w - 10f, tableBottom)
        canvas.drawRoundRect(tableRect, 8f, 8f, tableBgPaint)
        canvas.drawRoundRect(tableRect, 8f, 8f, tableBorderPaint)

        // Línea divisoria horizontal entre fila de % y fila de km
        val midLineY = tableTop + (tableHeight * 0.5f)
        canvas.drawLine(10f, midLineY, w - 10f, midLineY, tableBorderPaint)

        val fontPct = (tableHeight * 0.38f).coerceIn(10f, 16f)
        tableTextPaint.textSize = fontPct
        tableNegativeTextPaint.textSize = fontPct
        tableSubTextPaint.textSize = (tableHeight * 0.34f).coerceIn(9f, 14f)

        val stepMeters = (totalMetersAhead / (samples - 1)).toInt()

        for (i in 0 until samples - 1) {
            val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f
            val midX = (pointsX[i] + pointsX[i + 1]) / 2f

            // Fila 1: Porcentaje de Pendiente del tramo
            val pctStr = "%.1f%%".format(grade)
            val textPaintToUse = if (grade < 0f) tableNegativeTextPaint else tableTextPaint
            canvas.drawText(pctStr, midX, tableTop + (tableHeight * 0.38f), textPaintToUse)

            // Fila 2: Distancia en metros / km
            val distLabel = "${(i + 1) * stepMeters}m"
            canvas.drawText(distLabel, midX, tableBottom - (tableHeight * 0.12f), tableSubTextPaint)

            // Líneas divisorias de columna verticales
            if (i < samples - 2) {
                val lx = pointsX[i + 1]
                canvas.drawLine(lx, tableTop, lx, tableBottom, tableBorderPaint)
            }
        }

        // Leyenda de colores rápida al pie
        drawMiniLegend(canvas, w, h)
    }

    private fun drawMiniLegend(canvas: Canvas, w: Float, h: Float) {
        val legendY = h * 0.94f
        val swatchW = 20f
        val swatchH = 8f
        val categories = listOf(
            "0-4%" to "#10B981",
            "4-8%" to "#0EA5E9",
            "8-10%" to "#F59E0B",
            "10-15%" to "#EF4444",
            "15%+" to "#1F2937"
        )

        val totalLegendW = categories.size * (swatchW + 36f)
        var startX = (w - totalLegendW) / 2f

        categories.forEach { (label, hexColor) ->
            swatchRect.set(startX, legendY, startX + swatchW, legendY + swatchH)
            legendSwatchPaint.color = Color.parseColor(hexColor)
            canvas.drawRoundRect(swatchRect, 3f, 3f, legendSwatchPaint)

            canvas.drawText(label, startX + swatchW + 18f, legendY + swatchH - 1f, legendTextPaint)
            startX += (swatchW + 36f)
        }
    }

    private fun getGradeColorPro(grade: Double): String {
        return when {
            grade < 4.0 -> "#10B981"  // Verde Esmeralda (0% - 4%)
            grade < 8.0 -> "#0EA5E9"  // Azul Celeste (4% - 8%)
            grade < 10.0 -> "#F59E0B" // Amarillo Ocre (8% - 10%)
            grade < 15.0 -> "#EF4444" // Rojo Carmesí (10% - 15%)
            else -> "#1F2937"         // Antracita / Negro rampa muy dura (> 15%)
        }
    }
}