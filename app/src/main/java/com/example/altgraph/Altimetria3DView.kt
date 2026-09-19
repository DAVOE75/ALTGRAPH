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
import kotlin.math.roundToInt
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
        color = Color.parseColor("#1E293B")
        strokeWidth = 1.2f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
    }

    private val cotaLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#334155")
        strokeWidth = 1.6f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
    }

    private val cotaTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#94A3B8")
        typeface = Typeface.DEFAULT_BOLD
    }

    // --- PAINTS DE LA CINTA 3D ESTILO LA FLAMME ROUGE ---
    private val ribbonSurfacePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val ribbonFrontLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 2.8f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val ribbonBackLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#94A3B8")
        strokeWidth = 1.4f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val sliceSeparatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#334155")
        strokeWidth = 1.2f
        style = Paint.Style.STROKE
    }

    private val pctBadgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CC070C18") // Fondo cápsula oscuro para máximo contraste sobre cualquier color
        style = Paint.Style.FILL
    }

    private val pctBadgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#55FFFFFF") // Borde sutil nítido
        strokeWidth = 1.2f
        style = Paint.Style.STROKE
    }

    // --- PAINTS Y RUTAS PARA LOS 5 MODELOS DE ALTIMETRÍA REVOLUCIONARIOS ---
    // 1. Horizonte Isométrico (Cockpit 3D)
    private val headlightPath = Path()
    private val headlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val isoRidgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0F2FE")
        strokeWidth = 3.0f
        style = Paint.Style.STROKE
    }

    // 2. Oasis y Crisoles Tácticos
    private val oasisGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val oasisBadgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E00284C7")
        style = Paint.Style.FILL
    }
    private val oasisBadgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0F2FE")
        strokeWidth = 1.2f
        style = Paint.Style.STROKE
    }
    private val crucibleBadgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0991B1B")
        style = Paint.Style.FILL
    }
    private val crucibleBadgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FECACA")
        strokeWidth = 1.2f
        style = Paint.Style.STROKE
    }
    private val shelfRidgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        strokeWidth = 3.5f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val crucibleRidgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EF4444")
        strokeWidth = 3.5f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val hypoxiaHazePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    // 3. Campo de Fuerza y Fatiga
    private val inertiaWavePath = Path()
    private val inertiaWavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2.2f
        style = Paint.Style.STROKE
    }
    private val tensionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#35FFFFFF")
        strokeWidth = 1.0f
        style = Paint.Style.STROKE
    }
    private val pacingLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#EAB308")
        strokeWidth = 1.8f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(6f, 6f), 0f)
    }

    // 4. Monolito Obsidiana y Plasma
    private val obsidianFacetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1E293B")
        strokeWidth = 1.0f
        style = Paint.Style.STROKE
    }
    private val plasmaCorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val neonGlowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#5038BDF8")
        strokeWidth = 6.0f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val neonCorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 2.2f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val holoBadgeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#D0090D16")
        style = Paint.Style.FILL
    }
    private val holoBadgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8038BDF8")
        strokeWidth = 1.2f
        style = Paint.Style.STROKE
    }

    private val subBlockPctTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 0f, 0f, Color.BLACK)
    }

    // Pared frontal y lateral 3D
    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val leftEndcapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0F172A")
        style = Paint.Style.FILL
    }

    private val leftEndcapBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#334155")
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }

    // Barra de distancia inferior 3D
    private val baseBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#050811")
        style = Paint.Style.FILL
    }

    private val baseBarBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1F2937")
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }

    private val baseDistTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    // Encabezados y telemetría
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
        color = Color.parseColor("#94A3B8")
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.LEFT
    }

    private val percentTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        setShadowLayer(4f, 0f, 0f, Color.BLACK)
    }

    // Baliza del ciclista
    private val beaconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        style = Paint.Style.FILL
    }

    private val beaconHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4538BDF8")
        style = Paint.Style.FILL
    }

    private val beaconCorePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    // POIs estilo insignias circulares La Flamme Rouge
    private val poiLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E2E8F0")
        strokeWidth = 1.8f
        style = Paint.Style.STROKE
    }

    private val poiBadgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#DC2626")
        style = Paint.Style.FILL
    }

    private val poiBadgeBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }

    private val poiBadgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    private val poiLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }

    // Rampas duras
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

    // Zoom Overlay
    private val zoomBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E018181B")
        style = Paint.Style.FILL
    }

    private val zoomBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        strokeWidth = 2f
        style = Paint.Style.STROKE
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
    private var showZoomControls: Boolean = true
    private var showBlockPercentages: Boolean = true
    private var curvatureOffsets: List<Float> = emptyList()
    private var riderProgress: Float = 0.0f
    private var windowStartMeters: Double = 0.0
    private var subBlocks: List<Float> = emptyList()
    private var subBlockSizeMeters: Double = 50.0
    private var majorBlockSizeMeters: Double = 100.0
    private var profileElevations: List<Float> = emptyList()
    private var activeClimbs: List<RouteClimb> = emptyList()
    private var altimetriaStyle: AltimetriaStyle = AltimetriaStyle.CLASSIC
    private var targetVam: Int = 900

    // Interactive Zoom & Pan
    private var zoomScale: Float = 1.0f
    private var panOffsetX: Float = 0.0f
    private var isDragging: Boolean = false
    private var lastTouchX: Float = 0.0f

    private val ribbonQuadPath = Path()
    private val wallPath = Path()
    private val leftEndcapPath = Path()
    private val arrowPath = Path()
    private val baseBarRect = RectF()
    private val leftBaseEndcapPath = Path()
    private val oasisShelfPath = Path()
    private val cruciblePath = Path()
    private val pacingPath = Path()
    private val plasmaCorePath = Path()

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
        pois: List<Poi> = emptyList(),
        showZoomControls: Boolean = true,
        curvatureOffsets: List<Float> = emptyList(),
        riderProgress: Float = 0.0f,
        windowStartMeters: Double = 0.0,
        subBlocks: List<Float> = emptyList(),
        subBlockSizeMeters: Double = 50.0,
        majorBlockSizeMeters: Double = 100.0,
        profileElevations: List<Float> = emptyList(),
        showBlockPercentages: Boolean = true,
        altimetriaStyle: AltimetriaStyle = AltimetriaStyle.CLASSIC,
        targetVam: Int = 900,
        activeClimbs: List<RouteClimb> = emptyList()
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
        this.showZoomControls = showZoomControls
        this.showBlockPercentages = showBlockPercentages
        this.curvatureOffsets = curvatureOffsets
        this.riderProgress = riderProgress
        this.windowStartMeters = windowStartMeters
        this.altimetriaStyle = altimetriaStyle
        this.targetVam = targetVam
        this.activeClimbs = activeClimbs
        if (subBlocks.isNotEmpty()) {
            this.subBlocks = subBlocks
        }
        this.subBlockSizeMeters = subBlockSizeMeters
        this.majorBlockSizeMeters = majorBlockSizeMeters
        if (profileElevations.isNotEmpty()) {
            this.profileElevations = profileElevations
        }

        FontHelper.applyFontToPaint(titlePaint, fontFamilyKey)
        FontHelper.applyFontToPaint(subTitleLabelPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(liveGradePaint, fontFamilyKey)
        FontHelper.applyFontToPaint(maxGradePaint, fontFamilyKey)
        FontHelper.applyFontToPaint(percentTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(subBlockPctTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(rampTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(cotaTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(axisTextPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(poiLabelPaint, fontFamilyKey)
        FontHelper.applyFontToPaint(baseDistTextPaint, fontFamilyKey)

        postInvalidate()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!showZoomControls) return super.onTouchEvent(event)

        val touchX = event.x
        val touchY = event.y
        val w = width.toFloat()
        val h = height.toFloat()

        val pillW = (w * 0.22f).coerceIn(80f, 130f)
        val pillH = (h * 0.15f).coerceIn(28f, 44f)
        val right = w - 16f
        val left = right - pillW
        val top = 16f
        val bottom = top + pillH
        val centerX = left + (pillW / 2f)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (touchY in top..bottom) {
                    if (touchX >= left && touchX < centerX) {
                        performClick()
                        zoomOut()
                        return true
                    }
                    if (touchX >= centerX && touchX <= right) {
                        performClick()
                        zoomIn()
                        return true
                    }
                }
                isDragging = true
                lastTouchX = touchX
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    val dx = touchX - lastTouchX
                    lastTouchX = touchX

                    val totalWidth = w * zoomScale
                    val maxPanX = (totalWidth - w).coerceAtLeast(0f)

                    panOffsetX = (panOffsetX - dx).coerceIn(0f, maxPanX)
                    postInvalidate()
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun zoomIn() {
        val curr = lookaheadMeters
        val nextVal = when {
            curr > 50000 -> 50000
            curr > 20000 -> 20000
            curr > 10000 -> 10000
            curr > 5000 -> 5000
            curr > 2000 -> 2000
            curr > 1000 -> 1000
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
            curr < 2000 -> 2000
            curr < 5000 -> 5000
            curr < 10000 -> 10000
            curr < 20000 -> 20000
            curr < 50000 -> 50000
            curr < 100000 -> 100000
            else -> 100000
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
        titlePaint.textSize = ((h * 0.080f) * fontScale).coerceIn(16f, 28f)
        canvas.drawText(titleText, 18f, h * 0.07f, titlePaint)

        val col1X = 18f
        val col2X = w * 0.35f
        val col3X = w * 0.68f

        subTitleLabelPaint.textSize = ((h * 0.040f) * fontScale).coerceIn(9f, 14f)

        // COLUMNA 1: PENDIENTE ACTUAL
        val labelCurrentGrade = context.getString(R.string.label_current_gradient)
        canvas.drawText(labelCurrentGrade, col1X, h * 0.13f, subTitleLabelPaint)

        val liveGradeText = "%.1f%%".format(currentGrade)
        liveGradePaint.textSize = ((h * 0.15f) * fontScale).coerceIn(26f, 48f) // Aumentado 2 puntos extra
        liveGradePaint.color = GradeColorScale.getTelemetryColor(currentGrade)
        canvas.drawText(liveGradeText, col1X, h * 0.24f, liveGradePaint)

        // COLUMNA 2: PENDIENTE MEDIA DEL TRAMO VISIBLE
        val tramoAvgGrade = if (subBlocks.isNotEmpty()) subBlocks.average() else (if (nextBlocks.isNotEmpty()) nextBlocks.average() else currentGrade)
        val labelAvgGrade = context.getString(R.string.label_avg_gradient_tramo)
        canvas.drawText(labelAvgGrade, col2X, h * 0.13f, subTitleLabelPaint)

        val avgGradeText = "%.1f%%".format(tramoAvgGrade)
        liveGradePaint.textSize = ((h * 0.11f) * fontScale).coerceIn(18f, 36f)
        liveGradePaint.color = GradeColorScale.getTelemetryColor(tramoAvgGrade)
        canvas.drawText(avgGradeText, col2X, h * 0.23f, liveGradePaint)

        // COLUMNA 3: PENDIENTE MÁXIMA DEL TRAMO VISIBLE
        if (showMaxGrade) {
            val tramoMaxGrade = if (subBlocks.isNotEmpty()) subBlocks.maxOrNull()?.toDouble() ?: 12.8 else (if (nextBlocks.isNotEmpty()) nextBlocks.maxOrNull()?.toDouble() ?: 12.8 else 12.8)
            val labelMaxGrade = context.getString(R.string.label_max_gradient_tramo)
            canvas.drawText(labelMaxGrade, col3X, h * 0.13f, subTitleLabelPaint)

            val maxGradeText = "%.1f%%".format(tramoMaxGrade)
            maxGradePaint.textSize = ((h * 0.11f) * fontScale).coerceIn(18f, 36f)
            maxGradePaint.color = GradeColorScale.getTelemetryColor(tramoMaxGrade)
            canvas.drawText(maxGradeText, col3X, h * 0.23f, maxGradePaint)
        }

        // 3. Botones Minimalistas de Zoom [ - | + ]
        if (showZoomControls) {
            drawZoomOverlay(canvas, w, h)
        }

        // 4. Perfil Altimétrico 3D Recto y Limpio (Estilo La Flamme Rouge)
        draw3DStraightRibbon(canvas, w, h)
    }

    private fun drawZoomOverlay(canvas: Canvas, w: Float, h: Float) {
        val pillW = (w * 0.24f).coerceIn(86f, 135f)
        val pillH = (h * 0.15f).coerceIn(26f, 40f)

        val marginR = 16f
        val marginT = 16f

        val right = w - marginR
        val left = right - pillW
        val top = marginT
        val bottom = top + pillH

        val rect = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rect, pillH / 2f, pillH / 2f, zoomBgPaint)
        canvas.drawRoundRect(rect, pillH / 2f, pillH / 2f, zoomBorderPaint)

        val textSize = (pillH * 0.58f).coerceIn(11f, 16f)
        zoomBtnPaint.textSize = textSize

        val centerY = top + (pillH / 2f) + (textSize * 0.35f)
        val centerX = left + (pillW / 2f)

        val zoomLabel = when {
            lookaheadMeters < 1000 -> "🔍 ${lookaheadMeters}m"
            lookaheadMeters % 1000 == 0 -> "🔍 ${lookaheadMeters / 1000}km"
            else -> "🔍 ${lookaheadMeters / 1000}.${(lookaheadMeters % 1000) / 100}km"
        }
        canvas.drawText(zoomLabel, centerX, centerY, zoomBtnPaint)
    }

    private fun drawMountainGates(canvas: Canvas, xFront: FloatArray, yFront: FloatArray, yBase: FloatArray, totalMicroSamples: Int) {
        if (activeClimbs.isEmpty()) return

        val windowEndMeters = windowStartMeters + lookaheadMeters

        for (climb in activeClimbs) {
            // Draw Mountain Gate at Start
            if (climb.startDistance in windowStartMeters..windowEndMeters) {
                val startFrac = ((climb.startDistance - windowStartMeters) / lookaheadMeters).toFloat().coerceIn(0f, 1f)
                val idx = (startFrac * (totalMicroSamples - 1)).roundToInt().coerceIn(0, totalMicroSamples - 1)
                val px = xFront[idx]
                val pyTop = yFront[idx]
                
                // Draw Glowing Gate / Arch
                val gatePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#EF4444")
                    strokeWidth = 4f
                    style = Paint.Style.STROKE
                    setShadowLayer(10f, 0f, 0f, Color.parseColor("#EF4444"))
                }
                
                // Draw a vertical line shooting up from the road as a gate
                val archHeight = 120f
                canvas.drawLine(px, pyTop, px, pyTop - archHeight, gatePaint)
                canvas.drawLine(px - 30f, pyTop - archHeight, px + 30f, pyTop - archHeight, gatePaint) // Top crossbar
                
                // Label for Start
                val gateTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 20f
                    typeface = Typeface.DEFAULT_BOLD
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("START CLIMB", px, pyTop - archHeight - 10f, gateTextPaint)
            }

            // Draw Summit Beacon at End
            if (climb.endDistance in windowStartMeters..windowEndMeters) {
                val endFrac = ((climb.endDistance - windowStartMeters) / lookaheadMeters).toFloat().coerceIn(0f, 1f)
                val idx = (endFrac * (totalMicroSamples - 1)).roundToInt().coerceIn(0, totalMicroSamples - 1)
                val px = xFront[idx]
                val pyTop = yFront[idx]
                
                val beaconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.parseColor("#EAB308")
                    style = Paint.Style.FILL
                    setShadowLayer(15f, 0f, 0f, Color.parseColor("#FDE047"))
                }
                
                canvas.drawCircle(px, pyTop - 40f, 15f, beaconPaint)
                
                val summitTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.WHITE
                    textSize = 22f
                    typeface = Typeface.DEFAULT_BOLD
                    textAlign = Paint.Align.CENTER
                }
                canvas.drawText("SUMMIT", px, pyTop - 65f, summitTextPaint)
            }
        }
    }

    private fun draw3DStraightRibbon(canvas: Canvas, w: Float, h: Float) {
        val totalMetersAhead = lookaheadMeters.toDouble().coerceIn(100.0, 200000.0)

        val totalMicroSamples: Int
        val microElevations: FloatArray
        val microGrades: FloatArray
        val microSubDivisions: Int
        val majorBlocksCount: Int

        if (profileElevations.size >= 2 && subBlocks.isNotEmpty()) {
            totalMicroSamples = profileElevations.size
            microElevations = profileElevations.toFloatArray()
            microGrades = subBlocks.toFloatArray()
            val ratio = (majorBlockSizeMeters / subBlockSizeMeters.coerceAtLeast(1.0)).roundToInt().coerceAtLeast(1)
            microSubDivisions = ratio
            majorBlocksCount = ((totalMicroSamples - 1) / microSubDivisions).coerceAtLeast(1)
        } else {
            majorBlocksCount = if (totalMetersAhead > 1000.0) {
                (totalMetersAhead / 1000.0).toInt().coerceIn(2, 10)
            } else {
                (totalMetersAhead / blockSizeMeters.coerceAtLeast(10.0)).toInt().coerceIn(2, 10)
            }
            microSubDivisions = if (totalMetersAhead >= 1000.0) 20 else 5
            totalMicroSamples = (majorBlocksCount * microSubDivisions) + 1

            val microDistMeters = totalMetersAhead / (totalMicroSamples - 1)
            microElevations = FloatArray(totalMicroSamples)
            microGrades = FloatArray(totalMicroSamples - 1)
            var accumulatedElev = currentElevation
            microElevations[0] = accumulatedElev.toFloat()

            for (i in 0 until totalMicroSamples - 1) {
                val majorBlockIdx = (i / microSubDivisions).coerceAtMost(nextBlocks.size - 1)
                val baseGrade = if (majorBlockIdx < nextBlocks.size) nextBlocks[majorBlockIdx].toDouble() else 4.0
                val microGrade = baseGrade.coerceIn(-15.0, 30.0).toFloat()
                microGrades[i] = microGrade
                accumulatedElev += (microDistMeters * (microGrade / 100.0))
                microElevations[i + 1] = accumulatedElev.toFloat()
            }
        }

        val majorSamples = majorBlocksCount + 1

        val startX = (w * 0.10f)
        val baseEndX = (w * 0.98f)
        val baseGroundY = (h * 0.84f)
        val maxPeakHeight = (h * 0.48f)

        // Desplazamiento oblicuo 3D sutil y elegante (profundidad reducida a petición del usuario)
        val depth3dX = (w * 0.012f).coerceIn(4f, 8f)
        val depth3dY = (h * 0.015f).coerceIn(5f, 9f)

        val totalSpanX = (baseEndX - startX) * zoomScale
        val microStepX = totalSpanX / (totalMicroSamples - 1).coerceAtLeast(1)

        val xFront = FloatArray(totalMicroSamples)
        val yFront = FloatArray(totalMicroSamples)
        val xBack = FloatArray(totalMicroSamples)
        val yBack = FloatArray(totalMicroSamples)
        val yBase = FloatArray(totalMicroSamples)

        val minElev = microElevations.minOrNull() ?: currentElevation.toFloat()
        val maxElev = microElevations.maxOrNull() ?: (minElev + 50f)
        val elevRange = (maxElev - minElev).coerceAtLeast(15f)

        // GRÁFICA RECTA: xFront avanza de forma estrictamente lineal, sin zigzagueo de carretera
        for (i in 0 until totalMicroSamples) {
            val px = startX + (i * microStepX) - panOffsetX
            val normalizedHeight = ((microElevations[i] - minElev) / elevRange).coerceIn(0f, 1f)

            val pYFront = baseGroundY - (normalizedHeight * maxPeakHeight)

            xFront[i] = px
            yFront[i] = pYFront

            // Cara trasera extruida en 3D sutil hacia arriba y atrás
            xBack[i] = px - depth3dX
            yBack[i] = pYFront - depth3dY

            yBase[i] = baseGroundY
        }

        val majorX = FloatArray(majorSamples)
        val majorYBase = FloatArray(majorSamples)
        val majorElevations = FloatArray(majorSamples)

        for (k in 0 until majorSamples) {
            val microIdx = (k * microSubDivisions).coerceAtMost(totalMicroSamples - 1)
            majorX[k] = xFront[microIdx]
            majorYBase[k] = yBase[microIdx]
            majorElevations[k] = microElevations[microIdx]
        }

        // 1. Isolíneas de altitud de fondo
        val steps = 4
        axisTextPaint.textSize = (h * 0.038f).coerceIn(9f, 13f)
        axisTextPaint.textAlign = Paint.Align.LEFT
        for (k in 0..steps) {
            val ratio = k.toFloat() / steps
            val elevMark = (minElev + (ratio * elevRange)).toInt()
            val yIso = baseGroundY - (ratio * maxPeakHeight)

            canvas.drawLine(startX - depth3dX, yIso, baseEndX, yIso, gridPaint)
            canvas.drawText("${elevMark}m", 6f, yIso + 4f, axisTextPaint)
        }

        // 2. RENDERIZADO DEL PERFIL SEGÚN EL MODELO SELECCIONADO (5 VISTAS REVOLUCIONARIAS)
        when (altimetriaStyle) {
            AltimetriaStyle.CLASSIC -> {
                drawClassicRibbon(canvas, totalMicroSamples, microGrades, xFront, yFront, xBack, yBack, yBase, depth3dY)
            }
            AltimetriaStyle.HORIZON_ISOMETRIC -> {
                drawHorizonIsometricRibbon(canvas, w, h, totalMicroSamples, microGrades, xFront, yFront, xBack, yBack, yBase)
            }
            AltimetriaStyle.TACTICAL_OASES -> {
                drawTacticalOasesRibbon(canvas, w, h, totalMicroSamples, microGrades, xFront, yFront, xBack, yBack, yBase, depth3dY)
            }
            AltimetriaStyle.DYNAMIC_FORCE_FIELD -> {
                drawDynamicForceFieldRibbon(canvas, w, h, totalMicroSamples, microGrades, xFront, yFront, xBack, yBack, yBase, depth3dY, startX, baseEndX, baseGroundY)
            }
            AltimetriaStyle.MONOLITHIC_OBSIDIAN -> {
                drawMonolithicObsidianRibbon(canvas, w, h, totalMicroSamples, microGrades, xFront, yFront, xBack, yBack, yBase, startX, baseEndX, baseGroundY, maxPeakHeight)
            }
        }

        // 5. DIVISORES DE BLOQUE MAYOR VERTICALES CON COTAS DE ALTITUD
        cotaTextPaint.textSize = ((h * 0.048f) * fontScale).coerceIn(10f, 16f)
        for (k in 0 until majorSamples) {
            val microIdx = (k * microSubDivisions).coerceAtMost(totalMicroSamples - 1)
            val px = xFront[microIdx]
            val pyTop = yFront[microIdx]
            val pyBase = yBase[microIdx]

            canvas.drawLine(px, pyTop, px, pyBase, cotaLinePaint)

            if (showCotas) {
                val cotaText = "${majorElevations[k].toInt()} m"
                canvas.save()
                val textOffsetX = if (k == 0) px + 8f else px - 5f
                val textOffsetY = pyBase - 36f
                canvas.translate(textOffsetX, textOffsetY)
                canvas.rotate(-90f)
                canvas.drawText(cotaText, 0f, 0f, cotaTextPaint)
                canvas.restore()
            }
        }

        // 6. PORCENTAJES DE PENDIENTE DENTRO DE LOS BLOQUES (GRANDES, HORIZONTALES Y DE ALTO CONTRASTE)
        if (showBlockPercentages && totalMicroSamples > 1) {
            val isObsidian = altimetriaStyle == AltimetriaStyle.MONOLITHIC_OBSIDIAN
            val badgeBg = if (isObsidian) holoBadgeBgPaint else pctBadgeBgPaint
            val badgeBorder = if (isObsidian) holoBadgeBorderPaint else pctBadgeBorderPaint

            val blockW = abs(xFront[1] - xFront[0])
            val canDrawSubBlocks = blockW >= 28f

            if (canDrawSubBlocks) {
                // Dibujar porcentaje grande y horizontal dentro de cada columna/bloque individual
                for (i in 0 until totalMicroSamples - 1) {
                    val g = if (i < microGrades.size) microGrades[i] else 4.0f
                    val colMidX = (xFront[i] + xFront[i + 1]) / 2f
                    val colTopY = (yFront[i] + yFront[i + 1]) / 2f
                    val colH = baseGroundY - colTopY

                    val pctStr = if (blockW >= 52f) {
                        if (abs(g - g.roundToInt()) < 0.15f) "%.0f%%".format(g) else "%.1f%%".format(g)
                    } else {
                        "%.0f%%".format(g)
                    }

                    val calcFontSize = (blockW * 0.40f * fontScale).coerceIn(14f, 24f)
                    percentTextPaint.textSize = calcFontSize

                    val isInsideBlock = colH >= (calcFontSize * 2.2f)
                    val centerY = if (isInsideBlock) {
                        colTopY + (colH * 0.50f)
                    } else {
                        colTopY - (calcFontSize * 0.85f)
                    }

                    val textW = percentTextPaint.measureText(pctStr)
                    val padX = (calcFontSize * 0.26f).coerceIn(4f, 8f)
                    val padY = (calcFontSize * 0.18f).coerceIn(3f, 6f)
                    val pillRect = RectF(
                        colMidX - (textW / 2f) - padX,
                        centerY - (calcFontSize * 0.50f) - padY,
                        colMidX + (textW / 2f) + padX,
                        centerY + (calcFontSize * 0.50f) + padY
                    )

                    canvas.drawRoundRect(pillRect, 6f, 6f, badgeBg)
                    canvas.drawRoundRect(pillRect, 6f, 6f, badgeBorder)
                    canvas.drawText(pctStr, colMidX, centerY + (calcFontSize * 0.35f), percentTextPaint)
                }
            } else {
                // Si la escala es muy amplia (>5km con muchas micro-columnas), dibujar porcentaje en cada bloque mayor
                val fontBaseSize = ((h * 0.085f) * fontScale).coerceIn(16f, 26f)
                for (k in 0 until majorBlocksCount) {
                    val microIdx1 = k * microSubDivisions
                    val microIdx2 = ((k + 1) * microSubDivisions).coerceAtMost(totalMicroSamples - 1)

                    var sumG = 0.0
                    var countG = 0
                    for (si in microIdx1 until microIdx2) {
                        if (si < microGrades.size) {
                            sumG += microGrades[si]
                            countG++
                        }
                    }
                    val avgGrade = if (countG > 0) (sumG / countG).toFloat() else (if (k < nextBlocks.size) nextBlocks[k] else 4.0f)
                    val pctStr = if (abs(avgGrade - avgGrade.roundToInt()) < 0.15f) "%.0f%%".format(avgGrade) else "%.1f%%".format(avgGrade)

                    val midX = (xFront[microIdx1] + xFront[microIdx2]) / 2f
                    val midTopY = (yFront[microIdx1] + yFront[microIdx2]) / 2f
                    val bW = abs(xFront[microIdx2] - xFront[microIdx1])
                    val blockH = baseGroundY - midTopY

                    val calcFontSize = (bW * 0.32f * fontScale).coerceIn(16f, fontBaseSize)
                    percentTextPaint.textSize = calcFontSize

                    val isInsideBlock = blockH >= (calcFontSize * 2.2f)
                    val centerY = if (isInsideBlock) {
                        midTopY + (blockH * 0.50f)
                    } else {
                        midTopY - (calcFontSize * 0.85f)
                    }

                    val textW = percentTextPaint.measureText(pctStr)
                    val padX = (calcFontSize * 0.28f).coerceIn(5f, 9f)
                    val padY = (calcFontSize * 0.18f).coerceIn(3f, 6f)
                    val pillRect = RectF(
                        midX - (textW / 2f) - padX,
                        centerY - (calcFontSize * 0.50f) - padY,
                        midX + (textW / 2f) + padX,
                        centerY + (calcFontSize * 0.50f) + padY
                    )

                    canvas.drawRoundRect(pillRect, 6f, 6f, badgeBg)
                    canvas.drawRoundRect(pillRect, 6f, 6f, badgeBorder)
                    canvas.drawText(pctStr, midX, centerY + (calcFontSize * 0.35f), percentTextPaint)
                }
            }
        }

        // 6.b. MOUNTAIN GATES & SUMMIT BEACONS (Puertos Oficiales SDK)
        drawMountainGates(canvas, xFront, yFront, yBase, totalMicroSamples)

        // 7. Flechas con porcentaje para rampas duras (>= 10%)
        if (showRamps) {
            for (k in 0 until majorBlocksCount) {
                val startIdx = k * microSubDivisions
                val endIdx = ((k + 1) * microSubDivisions).coerceAtMost(totalMicroSamples - 1)

                var maxIdx = -1
                var maxG = 0f
                for (i in startIdx until endIdx) {
                    if (i < microGrades.size) {
                        val g = microGrades[i]
                        if (g >= rampMinSlope.toFloat() && g > maxG) {
                            maxG = g
                            maxIdx = i
                        }
                    }
                }

                if (maxIdx >= 0) {
                    val mx = xFront[maxIdx]
                    val my = yBack[maxIdx]

                    val arrowTopY = my - (h * 0.11f)
                    val arrowBottomY = my - 4f

                    canvas.drawLine(mx, arrowTopY, mx, arrowBottomY, rampArrowPaint)

                    arrowPath.reset()
                    arrowPath.moveTo(mx, arrowBottomY)
                    arrowPath.lineTo(mx - 4.5f, arrowBottomY - 7f)
                    arrowPath.lineTo(mx + 4.5f, arrowBottomY - 7f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, rampArrowHeadPaint)

                    rampTextPaint.textSize = ((h * 0.052f) * fontScale).coerceIn(10f, 17f)
                    val rampLabel = "%.0f%%".format(maxG)
                    canvas.drawText(rampLabel, mx, arrowTopY - 4f, rampTextPaint)
                }
            }
        }

        // 8. Puntos de Interés (Insignias circulares)
        if (showPois) {
            for (poi in pois) {
                val relDist = poi.relativeDistance
                if (relDist !in 0.0..totalMetersAhead) continue

                val progress = (relDist / totalMetersAhead).toFloat()
                val exactIndexF = progress * (totalMicroSamples - 1)
                val idx = exactIndexF.toInt().coerceIn(0, totalMicroSamples - 2)
                val rem = exactIndexF - idx

                val px = xFront[idx] + rem * (xFront[idx + 1] - xFront[idx])
                val pyTop = yBack[idx] + rem * (yBack[idx + 1] - yBack[idx])

                val badgeRadius = 14f
                val badgeCenterY = pyTop - (h * 0.14f)

                canvas.drawLine(px, pyTop, px, badgeCenterY + badgeRadius, poiLinePaint)
                canvas.drawCircle(px, badgeCenterY, badgeRadius, poiBadgePaint)
                canvas.drawCircle(px, badgeCenterY, badgeRadius, poiBadgeBorderPaint)

                poiBadgeTextPaint.textSize = 12f
                val badgeText = when (poi.type) {
                    PoiType.SUMMIT -> "1"
                    PoiType.VIEWPOINT -> "2"
                    else -> poi.icon
                }
                canvas.drawText(badgeText, px, badgeCenterY + 4f, poiBadgeTextPaint)

                poiLabelPaint.textSize = ((h * 0.038f) * fontScale).coerceIn(9f, 13f)
                canvas.drawText(poi.name, px, badgeCenterY - badgeRadius - 5f, poiLabelPaint)
            }
        }

        // 9. Barra inferior de distancia
        val barH = (h * 0.09f).coerceIn(20f, 32f)
        baseBarRect.set(startX, baseGroundY, baseEndX, baseGroundY + barH)
        canvas.drawRect(baseBarRect, baseBarPaint)
        canvas.drawRect(baseBarRect, baseBarBorderPaint)

        // Corte 3D izquierdo de la barra base
        leftBaseEndcapPath.reset()
        leftBaseEndcapPath.moveTo(startX, baseGroundY)
        leftBaseEndcapPath.lineTo(startX - depth3dX, baseGroundY - depth3dY)
        leftBaseEndcapPath.lineTo(startX - depth3dX, baseGroundY + barH - depth3dY)
        leftBaseEndcapPath.lineTo(startX, baseGroundY + barH)
        leftBaseEndcapPath.close()
        canvas.drawPath(leftBaseEndcapPath, baseBarPaint)
        canvas.drawPath(leftBaseEndcapPath, baseBarBorderPaint)

        // Rótulos de distancia en la barra inferior (Ventana rodante de 50 metros)
        baseDistTextPaint.textSize = (barH * 0.58f).coerceIn(10f, 15f)
        val majorDistMeters = if (profileElevations.isNotEmpty()) majorBlockSizeMeters else (totalMetersAhead / majorBlocksCount)

        for (k in 0 until majorSamples) {
            val m = (windowStartMeters + (k * majorDistMeters)).toLong()
            val distLabel = if (m < 1000) {
                "${m}m"
            } else {
                if (m % 1000L == 0L) "${m / 1000}km" else "${m / 1000}.${(m % 1000L) / 100}km"
            }
            val px = majorX[k]
            canvas.drawText(distLabel, px, baseGroundY + (barH * 0.70f), baseDistTextPaint)
        }

        // 10. Baliza del ciclista en 3D: avanza limpiamente por la cresta frontal de la pendiente
        val progressClamped = riderProgress.coerceIn(0f, 1f)
        val exactIndexF = progressClamped * (totalMicroSamples - 1)
        val idx = exactIndexF.toInt().coerceIn(0, totalMicroSamples - 2)
        val rem = exactIndexF - idx
        val rx = xFront[idx] + rem * (xFront[idx + 1] - xFront[idx])
        val ry = yFront[idx] + rem * (yFront[idx + 1] - yFront[idx])

        // Haz de luz frontal en Horizonte Isométrico (orientado según la pendiente que sube el ciclista)
        if (altimetriaStyle == AltimetriaStyle.HORIZON_ISOMETRIC) {
            val coneLen = (w * 0.26f)
            val slopeDy = if (idx + 1 < totalMicroSamples) yFront[idx + 1] - yFront[idx] else 0f
            val slopeDx = if (idx + 1 < totalMicroSamples) (xFront[idx + 1] - xFront[idx]).coerceAtLeast(1f) else 1f
            val slopeRatio = (slopeDy / slopeDx).coerceIn(-0.8f, 0.8f)
            val targetMidY = ry + (coneLen * slopeRatio)

            val coneEndTopY = targetMidY - (h * 0.08f)
            val coneEndBottomY = targetMidY + (h * 0.08f)
            headlightPath.reset()
            headlightPath.moveTo(rx, ry)
            headlightPath.lineTo(rx + coneLen, coneEndTopY)
            headlightPath.lineTo(rx + coneLen, coneEndBottomY)
            headlightPath.close()

            val headlightShader = LinearGradient(
                rx, ry, rx + coneLen, targetMidY,
                Color.parseColor("#6038BDF8"),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            headlightPaint.shader = headlightShader
            canvas.drawPath(headlightPath, headlightPaint)
        }

        canvas.drawCircle(rx, ry, 22f, beaconHaloPaint)
        canvas.drawCircle(rx, ry, 10f, beaconPaint)
        canvas.drawCircle(rx, ry, 4.5f, beaconCorePaint)
    }

    // --- MÉTODOS DE RENDERIZADO PARA LOS 5 MODELOS DE ALTIMETRÍA REVOLUCIONARIOS ---

    private fun drawClassicRibbon(
        canvas: Canvas,
        totalMicroSamples: Int,
        microGrades: FloatArray,
        xFront: FloatArray,
        yFront: FloatArray,
        xBack: FloatArray,
        yBack: FloatArray,
        yBase: FloatArray,
        depth3dY: Float
    ) {
        for (i in 0 until totalMicroSamples - 1) {
            val grade = if (i < microGrades.size) microGrades[i] else 4.0f
            val baseColor = Color.parseColor(getGradeColor(grade.toDouble()))
            val darkColor = getGradeColorDarkShaded(grade.toDouble())

            wallPath.reset()
            wallPath.moveTo(xFront[i], yFront[i])
            wallPath.lineTo(xFront[i + 1], yFront[i + 1])
            wallPath.lineTo(xFront[i + 1], yBase[i + 1])
            wallPath.lineTo(xFront[i], yBase[i])
            wallPath.close()

            val wallShader = LinearGradient(
                xFront[i], yFront[i],
                xFront[i], yBase[i],
                baseColor, darkColor,
                Shader.TileMode.CLAMP
            )
            wallPaint.shader = wallShader
            canvas.drawPath(wallPath, wallPaint)
            canvas.drawLine(xFront[i], yFront[i], xFront[i], yBase[i], sliceSeparatorPaint)
        }
        val lastIdx = totalMicroSamples - 1
        canvas.drawLine(xFront[lastIdx], yFront[lastIdx], xFront[lastIdx], yBase[lastIdx], sliceSeparatorPaint)

        val startGrade = if (microGrades.isNotEmpty()) microGrades[0].toDouble() else 4.0
        leftEndcapPath.reset()
        leftEndcapPath.moveTo(xBack[0], yBack[0])
        leftEndcapPath.lineTo(xFront[0], yFront[0])
        leftEndcapPath.lineTo(xFront[0], yBase[0])
        leftEndcapPath.lineTo(xBack[0], yBase[0] - depth3dY)
        leftEndcapPath.close()
        leftEndcapPaint.color = getGradeColorDarkShaded(startGrade)
        canvas.drawPath(leftEndcapPath, leftEndcapPaint)
        canvas.drawPath(leftEndcapPath, leftEndcapBorderPaint)

        for (i in 0 until totalMicroSamples - 1) {
            val grade = if (i < microGrades.size) microGrades[i] else 4.0f
            val topColor = getGradeColorTopFacet(grade.toDouble())

            ribbonQuadPath.reset()
            ribbonQuadPath.moveTo(xFront[i], yFront[i])
            ribbonQuadPath.lineTo(xBack[i], yBack[i])
            ribbonQuadPath.lineTo(xBack[i + 1], yBack[i + 1])
            ribbonQuadPath.lineTo(xFront[i + 1], yFront[i + 1])
            ribbonQuadPath.close()

            ribbonSurfacePaint.color = topColor
            canvas.drawPath(ribbonQuadPath, ribbonSurfacePaint)
            canvas.drawLine(xBack[i], yBack[i], xBack[i + 1], yBack[i + 1], ribbonBackLinePaint)
            canvas.drawLine(xFront[i], yFront[i], xFront[i + 1], yFront[i + 1], ribbonFrontLinePaint)
        }
    }

    private fun drawHorizonIsometricRibbon(
        canvas: Canvas,
        w: Float,
        h: Float,
        totalMicroSamples: Int,
        microGrades: FloatArray,
        xFront: FloatArray,
        yFront: FloatArray,
        xBack: FloatArray,
        yBack: FloatArray,
        yBase: FloatArray
    ) {
        // Pared frontal con perspectiva isométrica que converge al horizonte
        for (i in 0 until totalMicroSamples - 1) {
            val grade = if (i < microGrades.size) microGrades[i] else 4.0f
            val baseColor = Color.parseColor(getGradeColor(grade.toDouble()))
            val darkColor = getGradeColorDarkShaded(grade.toDouble())

            wallPath.reset()
            wallPath.moveTo(xFront[i], yFront[i])
            wallPath.lineTo(xFront[i + 1], yFront[i + 1])
            wallPath.lineTo(xFront[i + 1], yBase[i + 1])
            wallPath.lineTo(xFront[i], yBase[i])
            wallPath.close()

            val wallShader = LinearGradient(
                xFront[i], yFront[i],
                xFront[i], yBase[i],
                baseColor, darkColor,
                Shader.TileMode.CLAMP
            )
            wallPaint.shader = wallShader
            canvas.drawPath(wallPath, wallPaint)
            canvas.drawLine(xFront[i], yFront[i], xFront[i], yBase[i], sliceSeparatorPaint)
        }
        val lastIdx = totalMicroSamples - 1
        canvas.drawLine(xFront[lastIdx], yFront[lastIdx], xFront[lastIdx], yBase[lastIdx], sliceSeparatorPaint)

        // Corte 3D izquierdo isométrico
        val pFactor0 = 1.0f
        val isoX0 = xFront[0] - (w * 0.035f * pFactor0)
        val isoY0 = yFront[0] - (h * 0.030f * pFactor0)
        val startGrade = if (microGrades.isNotEmpty()) microGrades[0].toDouble() else 4.0

        leftEndcapPath.reset()
        leftEndcapPath.moveTo(isoX0, isoY0)
        leftEndcapPath.lineTo(xFront[0], yFront[0])
        leftEndcapPath.lineTo(xFront[0], yBase[0])
        leftEndcapPath.lineTo(isoX0, yBase[0] - (h * 0.030f * pFactor0))
        leftEndcapPath.close()
        leftEndcapPaint.color = getGradeColorDarkShaded(startGrade)
        canvas.drawPath(leftEndcapPath, leftEndcapPaint)
        canvas.drawPath(leftEndcapPath, leftEndcapBorderPaint)

        // Calzada superior isométrica estilizada tipo pista de despegue hacia el cielo
        for (i in 0 until totalMicroSamples - 1) {
            val grade = if (i < microGrades.size) microGrades[i] else 4.0f
            val topColor = getGradeColorTopFacet(grade.toDouble())

            val pFactor = 1.0f - ((i.toFloat() / (totalMicroSamples - 1).coerceAtLeast(1)) * 0.45f)
            val nextPFactor = 1.0f - (((i + 1).toFloat() / (totalMicroSamples - 1).coerceAtLeast(1)) * 0.45f)
            val isoX1 = xFront[i] - (w * 0.035f * pFactor)
            val isoY1 = yFront[i] - (h * 0.030f * pFactor)
            val isoX2 = xFront[i + 1] - (w * 0.035f * nextPFactor)
            val isoY2 = yFront[i + 1] - (h * 0.030f * nextPFactor)

            ribbonQuadPath.reset()
            ribbonQuadPath.moveTo(xFront[i], yFront[i])
            ribbonQuadPath.lineTo(isoX1, isoY1)
            ribbonQuadPath.lineTo(isoX2, isoY2)
            ribbonQuadPath.lineTo(xFront[i + 1], yFront[i + 1])
            ribbonQuadPath.close()

            ribbonSurfacePaint.color = topColor
            canvas.drawPath(ribbonQuadPath, ribbonSurfacePaint)
            canvas.drawLine(isoX1, isoY1, isoX2, isoY2, ribbonBackLinePaint)

            // Cresta de horizonte azul hielo nítida
            canvas.drawLine(xFront[i], yFront[i], xFront[i + 1], yFront[i + 1], isoRidgePaint)

            // Línea de carril central tipo pista
            val midFrontX = (xFront[i] + xFront[i + 1]) / 2f
            val midFrontY = (yFront[i] + yFront[i + 1]) / 2f
            val midIsoX = (isoX1 + isoX2) / 2f
            val midIsoY = (isoY1 + isoY2) / 2f
            val laneMidX = (midFrontX + midIsoX) / 2f
            val laneMidY = (midFrontY + midIsoY) / 2f
            if (i % 2 == 0) {
                canvas.drawCircle(laneMidX, laneMidY, 2.0f, beaconCorePaint)
            }
        }
    }

    private fun drawTacticalOasesRibbon(
        canvas: Canvas,
        w: Float,
        h: Float,
        totalMicroSamples: Int,
        microGrades: FloatArray,
        xFront: FloatArray,
        yFront: FloatArray,
        xBack: FloatArray,
        yBack: FloatArray,
        yBase: FloatArray,
        depth3dY: Float
    ) {
        // Dibuja el perfil base clásico
        drawClassicRibbon(canvas, totalMicroSamples, microGrades, xFront, yFront, xBack, yBack, yBase, depth3dY)

        // 1. Resaltado de Oasis de Oxígeno (tramos <= 3% para recuperación)
        var oasisStart = -1
        for (i in 0 until totalMicroSamples - 1) {
            val g = if (i < microGrades.size) microGrades[i] else 4.0f
            if (g <= 3.0f) {
                if (oasisStart == -1) oasisStart = i
            } else {
                if (oasisStart != -1) {
                    drawOasisShelf(canvas, oasisStart, i, xFront, yFront, yBase, subBlockSizeMeters)
                    oasisStart = -1
                }
            }
        }
        if (oasisStart != -1) {
            drawOasisShelf(canvas, oasisStart, totalMicroSamples - 1, xFront, yFront, yBase, subBlockSizeMeters)
        }

        // 2. Resaltado de Crisoles de Fuego agrupados (rampas duras >= 12%)
        var crucibleStart = -1
        for (i in 0 until totalMicroSamples - 1) {
            val g = if (i < microGrades.size) microGrades[i] else 4.0f
            if (g >= 12.0f) {
                if (crucibleStart == -1) crucibleStart = i
            } else {
                if (crucibleStart != -1) {
                    drawCrucibleFire(canvas, crucibleStart, i, microGrades, xFront, yFront, yBase, subBlockSizeMeters)
                    crucibleStart = -1
                }
            }
        }
        if (crucibleStart != -1) {
            drawCrucibleFire(canvas, crucibleStart, totalMicroSamples - 1, microGrades, xFront, yFront, yBase, subBlockSizeMeters)
        }

        // 3. Bruma de hipoxia si altitud >= 1400m
        if (currentElevation >= 1400.0) {
            val hazeShader = LinearGradient(
                0f, 0f, 0f, h * 0.30f,
                Color.parseColor("#4594A3B8"),
                Color.TRANSPARENT,
                Shader.TileMode.CLAMP
            )
            hypoxiaHazePaint.shader = hazeShader
            canvas.drawRect(0f, 0f, w, h * 0.30f, hypoxiaHazePaint)
        }
    }

    private fun drawOasisShelf(
        canvas: Canvas,
        startIdx: Int,
        endIdx: Int,
        xFront: FloatArray,
        yFront: FloatArray,
        yBase: FloatArray,
        subBlockSize: Double
    ) {
        val x1 = xFront[startIdx]
        val x2 = xFront[endIdx]
        val midX = (x1 + x2) / 2f
        val topY = (yFront[startIdx] + yFront[endIdx]) / 2f

        oasisShelfPath.reset()
        oasisShelfPath.moveTo(xFront[startIdx], yFront[startIdx])
        for (k in startIdx + 1..endIdx) {
            oasisShelfPath.lineTo(xFront[k], yFront[k])
        }
        oasisShelfPath.lineTo(xFront[endIdx], yBase[endIdx])
        oasisShelfPath.lineTo(xFront[startIdx], yBase[startIdx])
        oasisShelfPath.close()

        val glowShader = LinearGradient(
            midX, topY,
            midX, yBase[startIdx],
            Color.parseColor("#750284C7"),
            Color.parseColor("#100284C7"),
            Shader.TileMode.CLAMP
        )
        oasisGlowPaint.shader = glowShader
        canvas.drawPath(oasisShelfPath, oasisGlowPaint)

        // Cresta iluminada en cian hielo siguiendo todos los vértices reales
        for (k in startIdx until endIdx) {
            canvas.drawLine(xFront[k], yFront[k], xFront[k + 1], yFront[k + 1], shelfRidgePaint)
        }

        // Rótulo táctico del descansillo si hay anchura
        val shelfW = abs(x2 - x1)
        if (shelfW >= 36f) {
            val distMeters = ((endIdx - startIdx) * subBlockSize).toInt()
            val label = if (shelfW >= 60f) "❄️ OASIS ${distMeters}m" else "❄️ OASIS"
            percentTextPaint.textSize = (shelfW * 0.22f * fontScale).coerceIn(11f, 15f)
            val textW = percentTextPaint.measureText(label)
            val badgeRect = RectF(
                midX - (textW / 2f) - 6f,
                topY - 20f,
                midX + (textW / 2f) + 6f,
                topY - 2f
            )
            canvas.drawRoundRect(badgeRect, 6f, 6f, oasisBadgeBgPaint)
            canvas.drawRoundRect(badgeRect, 6f, 6f, oasisBadgeBorderPaint)
            canvas.drawText(label, midX, topY - 7f, percentTextPaint)
        }
    }

    private fun drawCrucibleFire(
        canvas: Canvas,
        startIdx: Int,
        endIdx: Int,
        microGrades: FloatArray,
        xFront: FloatArray,
        yFront: FloatArray,
        yBase: FloatArray,
        subBlockSize: Double
    ) {
        val x1 = xFront[startIdx]
        val x2 = xFront[endIdx]
        val midX = (x1 + x2) / 2f
        val topY = (yFront[startIdx] + yFront[endIdx]) / 2f

        cruciblePath.reset()
        cruciblePath.moveTo(xFront[startIdx], yFront[startIdx])
        for (k in startIdx + 1..endIdx) {
            cruciblePath.lineTo(xFront[k], yFront[k])
        }
        cruciblePath.lineTo(xFront[endIdx], yBase[endIdx])
        cruciblePath.lineTo(xFront[startIdx], yBase[startIdx])
        cruciblePath.close()

        val fireShader = LinearGradient(
            midX, topY,
            midX, yBase[startIdx],
            Color.parseColor("#90DC2626"),
            Color.parseColor("#15DC2626"),
            Shader.TileMode.CLAMP
        )
        oasisGlowPaint.shader = fireShader
        canvas.drawPath(cruciblePath, oasisGlowPaint)

        // Cresta de fuego carmesí siguiendo el contorno exacto
        for (k in startIdx until endIdx) {
            canvas.drawLine(xFront[k], yFront[k], xFront[k + 1], yFront[k + 1], crucibleRidgePaint)
        }

        // Insignia táctica de Muro
        val crucibleW = abs(x2 - x1)
        if (crucibleW >= 36f) {
            val distMeters = ((endIdx - startIdx) * subBlockSize).toInt()
            var maxG = 0f
            for (k in startIdx until endIdx) {
                if (k < microGrades.size && microGrades[k] > maxG) maxG = microGrades[k]
            }
            val label = if (crucibleW >= 60f) "🔥 MURO ${distMeters}m" else "🔥 ${maxG.roundToInt()}%"
            percentTextPaint.textSize = (crucibleW * 0.22f * fontScale).coerceIn(11f, 15f)
            val textW = percentTextPaint.measureText(label)
            val badgeRect = RectF(
                midX - (textW / 2f) - 6f,
                topY - 20f,
                midX + (textW / 2f) + 6f,
                topY - 2f
            )
            canvas.drawRoundRect(badgeRect, 6f, 6f, crucibleBadgeBgPaint)
            canvas.drawRoundRect(badgeRect, 6f, 6f, crucibleBadgeBorderPaint)
            canvas.drawText(label, midX, topY - 7f, percentTextPaint)
        }
    }

    private fun drawDynamicForceFieldRibbon(
        canvas: Canvas,
        w: Float,
        h: Float,
        totalMicroSamples: Int,
        microGrades: FloatArray,
        xFront: FloatArray,
        yFront: FloatArray,
        xBack: FloatArray,
        yBack: FloatArray,
        yBase: FloatArray,
        depth3dY: Float,
        startX: Float,
        baseEndX: Float,
        baseGroundY: Float
    ) {
        // Dibuja el perfil base clásico
        drawClassicRibbon(canvas, totalMicroSamples, microGrades, xFront, yFront, xBack, yBack, yBase, depth3dY)

        // Líneas de tensión gravitatoria ancladas exactamente a la superficie inclinada
        for (i in 0 until totalMicroSamples - 1) {
            val g = if (i < microGrades.size) microGrades[i] else 4.0f
            if (g >= 8.0f) {
                val stepCount = 3
                val colW = xFront[i + 1] - xFront[i]
                for (s in 1..stepCount) {
                    val ratio = s.toFloat() / (stepCount + 1)
                    val tx = xFront[i] + (ratio * colW)
                    val ty = yFront[i] + (ratio * (yFront[i + 1] - yFront[i])) + 2f
                    canvas.drawLine(tx, ty, tx, yBase[i] - 2f, tensionPaint)
                }
            }
        }

        // Onda cinética de inercia a lo largo de la base (baseGroundY)
        inertiaWavePath.reset()
        val waveAmp = (h * 0.022f)
        val isHighResistance = currentGrade >= 9.0
        inertiaWavePaint.color = if (isHighResistance) Color.parseColor("#EF4444") else Color.parseColor("#38BDF8")

        val waveFrequency = if (isHighResistance) 22f else 14f
        for (step in 0..50) {
            val ratio = step / 50f
            val px = startX + (ratio * (baseEndX - startX))
            val py = baseGroundY + (sin((ratio * waveFrequency) + (riderProgress * 14f)) * waveAmp)
            if (step == 0) inertiaWavePath.moveTo(px, py) else inertiaWavePath.lineTo(px, py)
        }
        canvas.drawPath(inertiaWavePath, inertiaWavePaint)

        // Línea guía de ritmo ascensional (VAM) flotando suavemente sobre el perfil
        pacingPath.reset()
        for (i in 0 until totalMicroSamples) {
            val px = xFront[i]
            val py = yFront[i] - 12f
            if (i == 0) pacingPath.moveTo(px, py) else pacingPath.lineTo(px, py)
        }
        canvas.drawPath(pacingPath, pacingLinePaint)
    }

    private fun drawMonolithicObsidianRibbon(
        canvas: Canvas,
        w: Float,
        h: Float,
        totalMicroSamples: Int,
        microGrades: FloatArray,
        xFront: FloatArray,
        yFront: FloatArray,
        xBack: FloatArray,
        yBack: FloatArray,
        yBase: FloatArray,
        startX: Float,
        baseEndX: Float,
        baseGroundY: Float,
        maxPeakHeight: Float
    ) {
        // 1. Cuerpo de montaña en cristal de obsidiana ahumada profunda
        val obsidianShader = LinearGradient(
            startX, baseGroundY - maxPeakHeight,
            startX, baseGroundY,
            Color.parseColor("#151D2C"),
            Color.parseColor("#04070D"),
            Shader.TileMode.CLAMP
        )
        wallPaint.shader = obsidianShader

        for (i in 0 until totalMicroSamples - 1) {
            wallPath.reset()
            wallPath.moveTo(xFront[i], yFront[i])
            wallPath.lineTo(xFront[i + 1], yFront[i + 1])
            wallPath.lineTo(xFront[i + 1], yBase[i + 1])
            wallPath.lineTo(xFront[i], yBase[i])
            wallPath.close()
            canvas.drawPath(wallPath, wallPaint)

            // Facetas de corte de diamante obsidian
            if (i % 2 == 0) {
                canvas.drawLine(xFront[i], yFront[i], xFront[i] + (w * 0.04f), yBase[i], obsidianFacetPaint)
            }
            canvas.drawLine(xFront[i], yFront[i], xFront[i], yBase[i], sliceSeparatorPaint)
        }
        val lastIdx = totalMicroSamples - 1
        canvas.drawLine(xFront[lastIdx], yFront[lastIdx], xFront[lastIdx], yBase[lastIdx], sliceSeparatorPaint)

        // 2. Faceta superior 3D en cristal pulido oscuro con resplandor
        for (i in 0 until totalMicroSamples - 1) {
            ribbonQuadPath.reset()
            ribbonQuadPath.moveTo(xFront[i], yFront[i])
            ribbonQuadPath.lineTo(xBack[i], yBack[i])
            ribbonQuadPath.lineTo(xBack[i + 1], yBack[i + 1])
            ribbonQuadPath.lineTo(xFront[i + 1], yFront[i + 1])
            ribbonQuadPath.close()

            ribbonSurfacePaint.color = Color.parseColor("#0A1120")
            canvas.drawPath(ribbonQuadPath, ribbonSurfacePaint)
            canvas.drawLine(xBack[i], yBack[i], xBack[i + 1], yBack[i + 1], obsidianFacetPaint)
        }

        // 3. Núcleo interior de plasma radiante con el degradado de color oficial (estrictamente acotado)
        for (i in 0 until totalMicroSamples - 1) {
            val g = if (i < microGrades.size) microGrades[i] else 4.0f
            val baseColor = Color.parseColor(getGradeColor(g.toDouble()))
            val plasmaColor = Color.argb(165, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))

            val coreTopY1 = (yFront[i] + 8f).coerceAtMost(yBase[i] - 4f)
            val coreBottomY1 = (yFront[i] + 24f).coerceAtMost(yBase[i] - 1f)
            val coreTopY2 = (yFront[i + 1] + 8f).coerceAtMost(yBase[i + 1] - 4f)
            val coreBottomY2 = (yFront[i + 1] + 24f).coerceAtMost(yBase[i + 1] - 1f)

            if (coreBottomY1 > coreTopY1 && coreBottomY2 > coreTopY2) {
                plasmaCorePath.reset()
                plasmaCorePath.moveTo(xFront[i], coreTopY1)
                plasmaCorePath.lineTo(xFront[i + 1], coreTopY2)
                plasmaCorePath.lineTo(xFront[i + 1], coreBottomY2)
                plasmaCorePath.lineTo(xFront[i], coreBottomY1)
                plasmaCorePath.close()

                plasmaCorePaint.color = plasmaColor
                canvas.drawPath(plasmaCorePath, plasmaCorePaint)
            }
        }

        // 4. Cresta superior en haz láser blanco nítido con resplandor neón
        for (i in 0 until totalMicroSamples - 1) {
            canvas.drawLine(xFront[i], yFront[i], xFront[i + 1], yFront[i + 1], neonGlowPaint)
            canvas.drawLine(xFront[i], yFront[i], xFront[i + 1], yFront[i + 1], neonCorePaint)
        }
    }

    private fun getGradeColor(grade: Double): String {
        return GradeColorScale.getColorHex(grade)
    }

    private fun getGradeColorDarkShaded(grade: Double): Int {
        if (grade > 20.0) return Color.parseColor("#05070D")
        val baseColor = Color.parseColor(getGradeColor(grade))
        val r = (Color.red(baseColor) * 0.65f).toInt()
        val g = (Color.green(baseColor) * 0.65f).toInt()
        val b = (Color.blue(baseColor) * 0.65f).toInt()
        return Color.rgb(r, g, b)
    }

    private fun getGradeColorTopFacet(grade: Double): Int {
        if (grade > 20.0) return Color.parseColor("#1E293B")
        val baseColor = Color.parseColor(getGradeColor(grade))
        val r = (Color.red(baseColor) * 0.82f).toInt()
        val g = (Color.green(baseColor) * 0.82f).toInt()
        val b = (Color.blue(baseColor) * 0.82f).toInt()
        return Color.rgb(r, g, b)
    }
}