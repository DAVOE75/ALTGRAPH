package com.example.altgraph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class AltgraphMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val bgPaint = Paint().apply {
        color = Color.parseColor("#09090B")
        style = Paint.Style.FILL
    }

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1F2937")
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }

    private val contourLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#374151")
        strokeWidth = 2.0f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(12f, 8f), 0f)
    }

    private val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        strokeWidth = 10f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val riderBeaconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        style = Paint.Style.FILL
    }

    private val riderHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#4038BDF8")
        style = Paint.Style.FILL
    }

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val titleTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        typeface = Typeface.DEFAULT_BOLD
    }

    private val infoTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#38BDF8")
        typeface = Typeface.DEFAULT_BOLD
    }

    private var currentLat = 38.7831
    private var currentLng = -0.2114
    private var currentElevation = 350.0
    private var currentSpeedKmh = 18.5
    private var routePolylinePoints = listOf(
        Pair(38.7831, -0.2114),
        Pair(38.7842, -0.2105),
        Pair(38.7855, -0.2092),
        Pair(38.7868, -0.2081),
        Pair(38.7880, -0.2065),
        Pair(38.7895, -0.2048)
    )

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0 || h <= 0) return

        // 1. Dibujar Teselas Vectoriales Reales .mbtiles si están instaladas en /sdcard/Maps/
        val tileBitmap = MbtilesTileReader.getTileBitmapForLocation(currentLat, currentLng)
        if (tileBitmap != null) {
            val srcRect = Rect(0, 0, tileBitmap.width, tileBitmap.height)
            val dstRect = RectF(0f, 0f, w, h)
            canvas.drawBitmap(tileBitmap, srcRect, dstRect, null)
        } else {
            // Fondo Topográfico Vectorial de Respaldo
            canvas.drawRect(0f, 0f, w, h, bgPaint)

            // Curvas de Nivel Topográficas HD a Pantalla Completa
            val numContours = if (h > 400f) 12 else 6
            for (i in 1..numContours) {
                val cy = h * (i.toFloat() / (numContours + 1))
                roadPath.reset()
                roadPath.moveTo(0f, cy + sin(i * 1.5).toFloat() * 25f)
                roadPath.quadTo(w / 2f, cy - 35f, w, cy + cos(i * 1.5).toFloat() * 25f)
                canvas.drawPath(roadPath, contourLinePaint)
            }
        }

        // 2. Trazado Vectorial de la Carretera / Ruta GPS
        if (routePolylinePoints.size >= 2) {
            roadPath.reset()
            val startPt = routePolylinePoints.first()
            val startX = w * 0.15f
            val startY = h * 0.85f
            roadPath.moveTo(startX, startY)

            for (idx in 1 until routePolylinePoints.size) {
                val pt = routePolylinePoints[idx]
                val px = startX + ((pt.second - startPt.second) * 14000.0).toFloat().coerceIn(0f, w * 0.85f)
                val py = startY - ((pt.first - startPt.first) * 14000.0).toFloat().coerceIn(0f, h * 0.8f)
                roadPath.lineTo(px, py)
            }
            canvas.drawPath(roadPath, roadPaint)
        }

        // 3. Faro del Ciclista en Posición GPS Activa
        val rx = w * 0.50f
        val ry = h * 0.50f
        canvas.drawCircle(rx, ry, 32f, riderHaloPaint)
        canvas.drawCircle(rx, ry, 14f, riderBeaconPaint)

        // Flecha de Dirección de Marcha
        arrowPath.reset()
        arrowPath.moveTo(rx, ry - 16f)
        arrowPath.lineTo(rx - 8f, ry + 8f)
        arrowPath.lineTo(rx, ry + 3f)
        arrowPath.lineTo(rx + 8f, ry + 8f)
        arrowPath.close()
        canvas.drawPath(arrowPath, arrowPaint)

        // 4. Encabezado e Insignia Topográfica Superior
        titleTextPaint.textSize = (h * 0.055f).coerceIn(16f, 26f)
        canvas.drawText("🗺️ MAPA TOPO ALTGRAPH", 20f, h * 0.08f, titleTextPaint)

        val speedText = "%.1f km/h".format(currentSpeedKmh)
        val elevText = "${currentElevation.toInt()} m"
        infoTextPaint.textSize = (h * 0.045f).coerceIn(14f, 22f)
        canvas.drawText("$speedText  •  $elevText", 20f, h * 0.14f, infoTextPaint)

        // 5. Escala Gráfica de Distancia (200m)
        val scaleW = w * 0.25f
        val scaleX = w - scaleW - 20f
        val scaleY = h - 25f
        canvas.drawLine(scaleX, scaleY, scaleX + scaleW, scaleY, gridPaint)
        canvas.drawLine(scaleX, scaleY - 6f, scaleX, scaleY + 6f, gridPaint)
        canvas.drawLine(scaleX + scaleW, scaleY - 6f, scaleX + scaleW, scaleY + 6f, gridPaint)

        infoTextPaint.textSize = (h * 0.035f).coerceIn(10f, 14f)
        canvas.drawText("200 m", scaleX + (scaleW / 4f), scaleY - 6f, infoTextPaint)
    }
}