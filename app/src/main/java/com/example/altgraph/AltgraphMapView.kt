package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan

class AltgraphMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mapZoomFactor = 1.0f
    private var dragOffsetX = 0f
    private var dragOffsetY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false

    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mapZoomFactor *= detector.scaleFactor
            mapZoomFactor = mapZoomFactor.coerceIn(0.2f, 8.0f)
            postInvalidate()
            return true
        }
    })

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#F8FAFC")
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#64748B")
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }

    private val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0284C7")
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val riderBeaconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0284C7")
        style = Paint.Style.FILL
    }

    private val riderHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#400284C7")
        style = Paint.Style.FILL
    }

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val titleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0F172A")
        typeface = Typeface.DEFAULT_BOLD
    }

    private val infoTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0369A1")
        typeface = Typeface.DEFAULT_BOLD
    }

    private val zoomBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E0F1F5F9")
        style = Paint.Style.FILL
    }

    private val zoomBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0284C7")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private var currentLat = 0.0
    private var currentLng = 0.0
    private var currentElevation = 350.0
    private var currentSpeedKmh = 0.0
    private var routePolylinePoints = emptyList<Pair<Double, Double>>()

    private val roadPath = Path()
    private val arrowPath = Path()

    fun updateMapData(
        lat: Double,
        lng: Double,
        elevation: Double,
        speedMps: Double,
        routePoints: List<Pair<Double, Double>>
    ) {
        if (lat != 0.0 && lng != 0.0) {
            this.currentLat = lat
            this.currentLng = lng
        }
        if (elevation > 0) {
            this.currentElevation = elevation
        }
        this.currentSpeedKmh = speedMps * 3.6
        if (routePoints.isNotEmpty()) {
            this.routePolylinePoints = routePoints
        }

        postInvalidate()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        val x = event.x
        val y = event.y
        val w = width.toFloat()

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = x
                lastTouchY = y
                isDragging = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging && !scaleDetector.isInProgress) {
                    dragOffsetX += (x - lastTouchX)
                    dragOffsetY += (y - lastTouchY)
                    lastTouchX = x
                    lastTouchY = y
                    postInvalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                performClick()
                
                val zoomPillRect = RectF(w - 180f, 20f, w - 20f, 90f)
                val resetPillRect = RectF(w - 280f, 20f, w - 200f, 90f)

                if (zoomPillRect.contains(x, y)) {
                    if (x > w - 100f) {
                        mapZoomFactor = (mapZoomFactor * 1.5f).coerceAtMost(8.0f)
                    } else {
                        mapZoomFactor = (mapZoomFactor / 1.5f).coerceAtLeast(0.2f)
                    }
                    postInvalidate()
                } else if (resetPillRect.contains(x, y)) {
                    dragOffsetX = 0f
                    dragOffsetY = 0f
                    postInvalidate()
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0 || h <= 0) return

        var mapLat = currentLat
        var mapLng = currentLng
        if ((mapLat == 0.0 || mapLng == 0.0) && routePolylinePoints.isNotEmpty()) {
            mapLat = routePolylinePoints.first().first
            mapLng = routePolylinePoints.first().second
        }

        if (mapLat == 0.0 && mapLng == 0.0) {
            mapLat = 40.4168
            mapLng = -3.7038
        }

        canvas.drawRect(0f, 0f, w, h, bgPaint)

        MbtilesTileReader.initDb(context)

        var baseZoom = 14
        var scaleMultiplier = mapZoomFactor

        while (scaleMultiplier > 1.8f && baseZoom < MbtilesTileReader.getMaxZoom()) {
            baseZoom++
            scaleMultiplier /= 2.0f
        }
        while (scaleMultiplier < 0.6f && baseZoom > MbtilesTileReader.getMinZoom()) {
            baseZoom--
            scaleMultiplier *= 2.0f
        }

        val tileSize = 256f
        val n = (1 shl baseZoom).toDouble()
        val centerX = (mapLng + 180.0) / 360.0 * n
        val centerLatRad = Math.toRadians(mapLat)
        val centerY = (1.0 - ln(tan(centerLatRad) + 1.0 / cos(centerLatRad)) / PI) / 2.0 * n

        val scaledTileSize = tileSize * scaleMultiplier

        val cx = (w / 2f) + dragOffsetX
        val cy = (h / 2f) + dragOffsetY

        val startCol = floor(centerX - (cx / scaledTileSize)).toInt() - 1
        val endCol = floor(centerX + ((w - cx) / scaledTileSize)).toInt() + 1
        val startRow = floor(centerY - (cy / scaledTileSize)).toInt() - 1
        val endRow = floor(centerY + ((h - cy) / scaledTileSize)).toInt() + 1

        for (col in startCol..endCol) {
            for (row in startRow..endRow) {
                val tileBmp = MbtilesTileReader.getTile(baseZoom, col, row)
                if (tileBmp != null) {
                    val pixelX = cx + (col - centerX).toFloat() * scaledTileSize
                    val pixelY = cy + (row - centerY).toFloat() * scaledTileSize
                    val dstRect = RectF(pixelX, pixelY, pixelX + scaledTileSize + 1f, pixelY + scaledTileSize + 1f)
                    canvas.drawBitmap(tileBmp, null, dstRect, null)
                }
            }
        }

        // Trazado de ruta GPS Exacto
        if (routePolylinePoints.isNotEmpty()) {
            roadPath.reset()
            var isFirst = true
            for (pt in routePolylinePoints) {
                val ptX = (pt.second + 180.0) / 360.0 * n
                val ptLatRad = Math.toRadians(pt.first)
                val ptY = (1.0 - ln(tan(ptLatRad) + 1.0 / cos(ptLatRad)) / PI) / 2.0 * n

                val px = cx + (ptX - centerX).toFloat() * scaledTileSize
                val py = cy + (ptY - centerY).toFloat() * scaledTileSize

                if (isFirst) {
                    roadPath.moveTo(px, py)
                    isFirst = false
                } else {
                    roadPath.lineTo(px, py)
                }
            }
            canvas.drawPath(roadPath, roadPaint)
        }

        // Faro del Ciclista
        val rx = (w / 2f) + dragOffsetX
        val ry = (h / 2f) + dragOffsetY
        canvas.drawCircle(rx, ry, 32f, riderHaloPaint)
        canvas.drawCircle(rx, ry, 14f, riderBeaconPaint)

        arrowPath.reset()
        arrowPath.moveTo(rx, ry - 16f)
        arrowPath.lineTo(rx - 8f, ry + 8f)
        arrowPath.lineTo(rx, ry + 3f)
        arrowPath.lineTo(rx + 8f, ry + 8f)
        arrowPath.close()
        canvas.drawPath(arrowPath, arrowPaint)

        // Textos y Controles
        titleTextPaint.textSize = (h * 0.055f).coerceIn(16f, 26f)
        canvas.drawText("🗺️ MAPA TOPO ALTGRAPH", 20f, h * 0.08f, titleTextPaint)

        val speedText = "%.1f km/h".format(currentSpeedKmh)
        val elevText = "${currentElevation.toInt()} m"
        infoTextPaint.textSize = (h * 0.045f).coerceIn(14f, 22f)
        canvas.drawText("$speedText  •  $elevText", 20f, h * 0.14f, infoTextPaint)

        val zoomPill = RectF(w - 180f, 20f, w - 20f, 90f)
        val resetPill = RectF(w - 280f, 20f, w - 200f, 90f)
        
        canvas.drawRoundRect(zoomPill, 35f, 35f, zoomBgPaint)
        canvas.drawRoundRect(zoomPill, 35f, 35f, zoomBorderPaint)
        
        canvas.drawRoundRect(resetPill, 35f, 35f, zoomBgPaint)
        canvas.drawRoundRect(resetPill, 35f, 35f, zoomBorderPaint)

        titleTextPaint.textSize = 24f
        canvas.drawText("–", w - 145f, 62f, titleTextPaint)
        canvas.drawLine(w - 100f, 30f, w - 100f, 80f, zoomBorderPaint)
        canvas.drawText("+", w - 65f, 62f, titleTextPaint)
        canvas.drawText("🎯", w - 260f, 60f, titleTextPaint)

        // Escala Gráfica
        val scaleW = w * 0.25f
        val scaleX = w - scaleW - 20f
        val scaleY = h - 25f
        canvas.drawLine(scaleX, scaleY, scaleX + scaleW, scaleY, gridPaint)
        canvas.drawLine(scaleX, scaleY - 6f, scaleX, scaleY + 6f, gridPaint)
        canvas.drawLine(scaleX + scaleW, scaleY - 6f, scaleX + scaleW, scaleY + 6f, gridPaint)

        infoTextPaint.textSize = (h * 0.035f).coerceIn(10f, 14f)
        
        // Calcular los metros aproximados del ancho de la escala
        val metersPerPixel = (156543.03 * cos(centerLatRad) / (1 shl baseZoom)) / scaleMultiplier
        val scaleMeters = (scaleW * metersPerPixel).toInt()
        
        canvas.drawText("$scaleMeters m", scaleX + (scaleW / 4f), scaleY - 6f, infoTextPaint)
    }
}