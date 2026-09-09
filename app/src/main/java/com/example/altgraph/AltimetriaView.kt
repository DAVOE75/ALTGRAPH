package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class AltimetriaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var remainingDistance: Double = 0.0
    private var timeToSummit: Long = 0
    private var avgGradeRemaining: Double = 0.0
    private var zoneColor: String = "VERDE"
    private var nextBlocks: List<Float> = emptyList()
    private var attackAlert: Boolean = false
    private var blockSizeMeters: Double = 100.0 // NUEVO

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 60f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        textSize = 30f
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val blockPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val alertPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun updateStrategyData(
        remainingDistance: Double,
        timeToSummit: Long,
        avgGrade: Double,
        currentZoneColor: String,
        nextBlocks: List<Float>,
        attackAlert: Boolean,
        blockSizeMeters: Double // NUEVO
    ) {
        this.remainingDistance = remainingDistance
        this.timeToSummit = timeToSummit
        this.avgGradeRemaining = avgGrade
        this.zoneColor = currentZoneColor
        this.nextBlocks = nextBlocks
        this.attackAlert = attackAlert
        this.blockSizeMeters = blockSizeMeters

        invalidate()
    }

    private fun getColorForGrade(grade: Float): Int {
        return when {
            grade > 12f -> Color.parseColor("#9C27B0")
            grade > 8f -> Color.parseColor("#F44336")
            grade > 5f -> Color.parseColor("#FF9800")
            grade > 3f -> Color.parseColor("#FFEB3B")
            else -> Color.parseColor("#4CAF50")
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        canvas.drawColor(Color.DKGRAY)

        android.graphics.Paint().apply { color = when (zoneColor) {
            "AMARILLO" -> Color.parseColor("#FFEB3B")
            "NARANJA" -> Color.parseColor("#FF9800")
            "ROJO" -> Color.parseColor("#F44336")
            "VIOLETA" -> Color.parseColor("#9C27B0")
            else -> Color.parseColor("#4CAF50")
        }}.let { paint ->
            canvas.drawRect(0f, 0f, width, height * 0.5f, paint)
        }

        val textColor = if (zoneColor == "AMARILLO") Color.BLACK else Color.WHITE
        textPaint.color = textColor
        labelPaint.color = if (zoneColor == "AMARILLO") Color.DKGRAY else Color.LTGRAY

        val centerX = width / 2

        val distanceText = String.format("%.1f km", remainingDistance / 1000)
        val minutes = timeToSummit / 60
        val seconds = timeToSummit % 60
        val timeText = String.format("%02d:%02d", minutes, seconds)
        val gradeText = String.format("%.1f %%", avgGradeRemaining)

        labelPaint.textSize = 30f
        canvas.drawText("ESTRATEGA DE PUERTO", centerX, 40f, labelPaint)

        textPaint.textSize = 55f
        canvas.drawText(distanceText, centerX, height * 0.35f, textPaint)
        canvas.drawText(timeText, centerX, height * 0.5f - 10f, textPaint)

        labelPaint.textSize = 25f
        canvas.drawText("DIST. RESTANTE", centerX, height * 0.45f - 10f, labelPaint)
        canvas.drawText("TIEMPO A CIMA", centerX, height * 0.6f - 10f, labelPaint)

        // Barra de bloques dinámica
        val barY = height * 0.75f
        val barHeight = 40f
        val totalMeters = nextBlocks.size * blockSizeMeters // Ej: 10 bloques * 100m = 1000m

        // Título dinámico
        labelPaint.textSize = 25f
        canvas.drawText("PRÓXIMOS ${totalMeters.toInt()} m (Bloques de ${blockSizeMeters.toInt()} m)", centerX, barY - 20f, labelPaint)

        val blockWidth = (width - 40f) / 10f

        for (i in 0 until min(10, nextBlocks.size)) {
            val left = 20f + i * blockWidth
            val top = barY
            val right = left + blockWidth - 8f
            val bottom = barY + barHeight

            blockPaint.color = getColorForGrade(nextBlocks[i])
            canvas.drawRoundRect(RectF(left, top, right, bottom), 10f, 10f, blockPaint)
        }

        if (attackAlert) {
            val alertRect = RectF(width * 0.1f, height * 0.25f, width * 0.9f, height * 0.65f)
            alertPaint.color = Color.RED
            alertPaint.style = Paint.Style.FILL
            canvas.drawRoundRect(alertRect, 30f, 30f, alertPaint)

            alertPaint.style = Paint.Style.STROKE
            alertPaint.strokeWidth = 8f
            alertPaint.color = Color.WHITE
            canvas.drawRoundRect(alertRect, 30f, 30f, alertPaint)

            textPaint.color = Color.WHITE
            textPaint.textSize = 45f
            canvas.drawText("¡ATACA!", width / 2, height * 0.45f, textPaint)

            textPaint.textSize = 25f
            canvas.drawText("Rampa dura en el próximo tramo", width / 2, height * 0.55f, textPaint)
        }
    }
}