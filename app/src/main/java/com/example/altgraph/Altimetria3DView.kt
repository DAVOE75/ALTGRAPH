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
import kotlin.math.cos
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
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }

    private val ribbonTopPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val lineStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 22f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#A1A1AA")
        textSize = 16f
    }

    private val beaconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        style = Paint.Style.FILL
    }

    private val beaconHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#3038BDF8")
        style = Paint.Style.FILL
    }

    private var nextBlocks: List<Float> = emptyList()
    private var currentElevation: Double = 0.0
    private var maxElevation: Double = 727.0
    private var currentGrade: Double = 0.0
    private var remainingDistance: Double = 0.0

    private val ribbonPath = Path()
    private val wallPath = Path()

    fun update3DData(
        blocks: List<Float>,
        elevation: Double,
        maxElev: Double,
        grade: Double,
        remainingDist: Double
    ) {
        this.nextBlocks = blocks
        this.currentElevation = elevation
        this.maxElevation = if (maxElev > 0) maxElev else 727.0
        this.currentGrade = grade
        this.remainingDistance = remainingDist
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0 || h <= 0) return

        // 1. Fondo Oscuro
        canvas.drawRect(0f, 0f, w, h, bgPaint)

        // 2. Encabezado Título
        val titleText = context.getString(R.string.data_type_altimetria_3d_title)
        textPaint.textSize = (h * 0.055f).coerceIn(16f, 24f)
        subTextPaint.textSize = (h * 0.04f).coerceIn(12f, 18f)

        canvas.drawText(titleText, 24f, h * 0.09f, textPaint)

        val subText = "${currentElevation.toInt()} m | Max: ${maxElevation.toInt()} m | ${"%.1f".format(currentGrade)}%"
        canvas.drawText(subText, 24f, h * 0.15f, subTextPaint)

        // 3. Rejilla Isometrica 3D de Suelo
        drawIsometricGrid(canvas, w, h)

        // 4. Perfil y Cinta de Ruta en Relieve 3D
        draw3DRibbonAndWalls(canvas, w, h)
    }

    private fun drawIsometricGrid(canvas: Canvas, w: Float, h: Float) {
        val groundY = h * 0.85f
        val gridLines = 5

        for (i in 0..gridLines) {
            val ratio = i.toFloat() / gridLines
            val x1 = w * 0.08f + ratio * (w * 0.2f)
            val y1 = groundY - ratio * (h * 0.15f)

            val x2 = w * 0.72f + ratio * (w * 0.2f)
            val y2 = groundY - ratio * (h * 0.15f)

            canvas.drawLine(x1, y1, x2, y2, gridPaint)
        }
    }

    private fun draw3DRibbonAndWalls(canvas: Canvas, w: Float, h: Float) {
        val samples = if (nextBlocks.isNotEmpty()) nextBlocks.size else 10
        val startX = w * 0.1f
        val endX = w * 0.9f
        val baseGroundY = h * 0.82f
        val maxPeakHeight = h * 0.42f

        val stepX = (endX - startX) / (samples - 1).coerceAtLeast(1)

        // Simulación de relieve y curva 3D (S-curve offset)
        val pointsX = FloatArray(samples)
        val pointsYTop = FloatArray(samples)
        val pointsYBase = FloatArray(samples)

        for (i in 0 until samples) {
            val grade = if (i < nextBlocks.size) nextBlocks[i].toDouble() else ((i % 4) * 2.5 + 2.0)

            // Proyección oblicua 3D
            val progress = i.toFloat() / (samples - 1)
            val curveOffset = sin(progress * Math.PI * 1.5).toFloat() * (w * 0.08f)

            val px = startX + i * stepX + curveOffset
            val normalizedHeight = (grade.coerceIn(-5.0, 20.0) + 5.0) / 25.0
            val pYTop = baseGroundY - (progress * (h * 0.12f)) - (normalizedHeight * maxPeakHeight).toFloat()
            val pYBase = baseGroundY - (progress * (h * 0.12f))

            pointsX[i] = px
            pointsYTop[i] = pYTop
            pointsYBase[i] = pYBase
        }

        // DIBUJAR PAREDES DE EXTROSIÓN 3D (Relieve)
        for (i in 0 until samples - 1) {
            val grade = if (i < nextBlocks.size) nextBlocks[i] else 4.0f

            // Color según pendiente
            val colorHex = getGradeColor(grade.toDouble())
            val fillColor = Color.parseColor(colorHex)

            // Pared lateral 3D con degradado a sombra
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

        // DIBUJAR CINTA SUPERIOR 3D
        ribbonPath.reset()
        ribbonPath.moveTo(pointsX[0], pointsYTop[0])
        for (i in 1 until samples) {
            ribbonPath.lineTo(pointsX[i], pointsYTop[i])
        }
        canvas.drawPath(ribbonPath, lineStrokePaint)

        // DIBUJAR MARCADOR BEACON 3D DEL CICLISTA
        val riderIdx = 0
        val rx = pointsX[riderIdx]
        val ry = pointsYTop[riderIdx]

        canvas.drawCircle(rx, ry, 18f, beaconHaloPaint)
        canvas.drawCircle(rx, ry, 8f, beaconPaint)

        // Etiqueta flotante 3D sobre el corredor
        val tagText = "📍 ${currentElevation.toInt()}m"
        textPaint.textSize = (h * 0.045f).coerceIn(12f, 18f)
        canvas.drawText(tagText, rx + 14f, ry - 10f, textPaint)
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