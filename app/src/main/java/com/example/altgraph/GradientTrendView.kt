package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class GradientTrendView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var currentGrade: Double = 7.5
    private var trend: GradientTrend = GradientTrend.STEEPENING
    private var maxRampPeak: Double = 12.8

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#09090B")
        style = Paint.Style.FILL
    }

    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        textSize = 18f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val gradeValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 48f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val subTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#A1A1AA")
        textSize = 18f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val trendIconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 40f
    }

    fun updateTrendData(grade: Double, trend: GradientTrend, maxRamp: Double) {
        this.currentGrade = grade
        this.trend = trend
        this.maxRampPeak = maxRamp
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        canvas.drawRect(0f, 0f, w, h, bgPaint)

        val padX = 20f

        // 1. Título
        titlePaint.textSize = (h * 0.08f).coerceIn(14f, 20f)
        canvas.drawText("TENDENCIA PENDIENTE 3D", padX, h * 0.16f, titlePaint)

        // 2. Pendiente Actual + Flecha de Tendencia
        gradeValuePaint.textSize = (h * 0.28f).coerceIn(28f, 56f)
        trendIconPaint.textSize = (h * 0.28f).coerceIn(28f, 56f)

        val (arrow, trendLabel, trendColor) = when (trend) {
            GradientTrend.STEEPENING -> Triple("↗️", "ENDURECIENDO", "#EF4444")
            GradientTrend.STEADY -> Triple("➔", "ESTABLE", "#22C55E")
            GradientTrend.EASING -> Triple("↘️", "SUAVIZANDO", "#38BDF8")
        }

        val gradeStr = "%.1f%%  %s".format(currentGrade, arrow)
        canvas.drawText(gradeStr, padX, h * 0.48f, gradeValuePaint)

        // 3. Etiqueta de Tendencia + Rampa Máxima
        subTextPaint.textSize = (h * 0.08f).coerceIn(13f, 20f)
        subTextPaint.color = Color.parseColor(trendColor)
        canvas.drawText(trendLabel, padX, h * 0.68f, subTextPaint)

        subTextPaint.color = Color.parseColor("#A1A1AA")
        val maxRampStr = "Rampa Máx: %.1f%%".format(maxRampPeak)
        canvas.drawText(maxRampStr, padX, h * 0.88f, subTextPaint)
    }
}