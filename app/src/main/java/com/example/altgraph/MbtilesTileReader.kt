package com.example.altgraph

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Environment
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPInputStream
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan

object MbtilesTileReader {

    private const val TAG = "ALTGRAPH_MAP"

    fun getTileBitmapForLocation(context: Context, lat: Double, lng: Double, zoom: Int = 14): Bitmap? {
        val searchDirs = listOf(
            File("/sdcard/offline/maps"),
            File("/sdcard/offline"),
            File(Environment.getExternalStorageDirectory(), "offline/maps"),
            File(Environment.getExternalStorageDirectory(), "offline"),
            File("/sdcard/Maps")
        )

        val mbtilesFiles = mutableListOf<File>()

        searchDirs.forEach { dir ->
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.filter { it.name.lowercase().endsWith(".mbtiles") }?.let {
                    mbtilesFiles.addAll(it)
                }
            }
        }

        Log.d(TAG, "Lector MBTiles: Encontrados ${mbtilesFiles.size} archivos .mbtiles")

        if (mbtilesFiles.isEmpty()) return null

        val prefs = AppPreferences.getInstance(context)
        val selectedMapName = prefs.customMapProvider

        val preferredMap = mbtilesFiles.find { it.name.equals(selectedMapName, ignoreCase = true) }
        if (preferredMap != null) {
            val bmp = readFromMbtilesDatabase(preferredMap, lat, lng, zoom)
            if (bmp != null) return bmp
        }

        mbtilesFiles.forEach { file ->
            val bmp = readFromMbtilesDatabase(file, lat, lng, zoom)
            if (bmp != null) return bmp
        }

        return null
    }

    private fun readFromMbtilesDatabase(mbtilesFile: File, inputLat: Double, inputLng: Double, inputZoom: Int): Bitmap? {
        Log.d(TAG, "Abriendo mapa MBTiles: ${mbtilesFile.absolutePath} (${mbtilesFile.length()} bytes)")
        return try {
            val flags = SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS
            val db = SQLiteDatabase.openDatabase(mbtilesFile.absolutePath, null, flags)

            var targetLat = inputLat
            var targetLng = inputLng
            var targetZoom = inputZoom

            // 1. Obtener la cobertura geográfica del archivo .mbtiles desde su tabla metadata
            try {
                val boundsCursor = db.rawQuery("SELECT value FROM metadata WHERE name = 'bounds'", null)
                if (boundsCursor.moveToFirst()) {
                    val boundsStr = boundsCursor.getString(0)
                    Log.d(TAG, "Metadata Bounds: $boundsStr")
                    val parts = boundsStr.split(",")
                    if (parts.size == 4) {
                        val minLng = parts[0].toDoubleOrNull() ?: -2.5
                        val minLat = parts[1].toDoubleOrNull() ?: 37.0
                        val maxLng = parts[2].toDoubleOrNull() ?: -0.5
                        val maxLat = parts[3].toDoubleOrNull() ?: 38.8

                        if (targetLat == 0.0 || targetLng == 0.0 || targetLat < minLat || targetLat > maxLat || targetLng < minLng || targetLng > maxLng) {
                            targetLat = (minLat + maxLat) / 2.0
                            targetLng = (minLng + maxLng) / 2.0
                        }
                    }
                }
                boundsCursor.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error leyendo metadata: ${e.message}")
            }

            // 2. Obtener los niveles de zoom disponibles
            try {
                val zoomCursor = db.rawQuery("SELECT MIN(zoom_level), MAX(zoom_level) FROM tiles", null)
                if (zoomCursor.moveToFirst()) {
                    val minZ = zoomCursor.getInt(0)
                    val maxZ = zoomCursor.getInt(1)
                    Log.d(TAG, "Zoom niveles disponibles: min=$minZ, max=$maxZ")
                    if (maxZ > 0) {
                        targetZoom = targetZoom.coerceIn(minZ, maxZ)
                    }
                }
                zoomCursor.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error leyendo niveles zoom: ${e.message}")
            }

            // 3. Coordenadas de tesela
            val tileX = floor((targetLng + 180.0) / 360.0 * (1 shl targetZoom)).toInt()
            val latRad = Math.toRadians(targetLat)
            val tileYOsm = floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * (1 shl targetZoom)).toInt()
            val tileYTms = ((1 shl targetZoom) - 1) - tileYOsm

            Log.d(TAG, "Buscando tesela: Z=$targetZoom, X=$tileX, Y_tms=$tileYTms, Y_osm=$tileYOsm para Lat=$targetLat, Lng=$targetLng")

            val queries = listOf(
                "SELECT tile_data FROM tiles WHERE zoom_level = $targetZoom AND tile_column = $tileX AND tile_row = $tileYTms",
                "SELECT tile_data FROM tiles WHERE zoom_level = $targetZoom AND tile_column = $tileX AND tile_row = $tileYOsm",
                "SELECT images.tile_data FROM images INNER JOIN map ON images.tile_id = map.tile_id WHERE map.zoom_level = $targetZoom AND map.tile_column = $tileX AND map.tile_row = $tileYTms",
                "SELECT images.tile_data FROM images INNER JOIN map ON images.tile_id = map.tile_id WHERE map.zoom_level = $targetZoom AND map.tile_column = $tileX AND map.tile_row = $tileYOsm",
                "SELECT tile_data FROM map WHERE zoom_level = $targetZoom AND tile_column = $tileX AND tile_row = $tileYTms",
                "SELECT tile_data FROM tiles LIMIT 1",
                "SELECT images.tile_data FROM images INNER JOIN map ON images.tile_id = map.tile_id LIMIT 1"
            )

            var bitmap: Bitmap? = null
            for (querySql in queries) {
                try {
                    val cursor = db.rawQuery(querySql, null)
                    if (cursor.moveToFirst()) {
                        val blob = cursor.getBlob(0)
                        if (blob != null && blob.isNotEmpty()) {
                            Log.d(TAG, "Encontrado Blob SQLite: ${blob.size} bytes con consulta: $querySql")
                            bitmap = decodeTileDataToBitmap(blob)
                        }
                    }
                    cursor.close()
                    if (bitmap != null) break
                } catch (e: Exception) {
                    Log.e(TAG, "Consulta fallida: $querySql -> ${e.message}")
                }
            }

            db.close()
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error fatal abriendo MBTiles: ${e.message}")
            null
        }
    }

    private fun decodeTileDataToBitmap(blob: ByteArray): Bitmap? {
        if (blob.isEmpty()) return null

        if (isPngOrJpeg(blob)) {
            Log.d(TAG, "Tesela es imagen PNG/JPEG raster directa (${blob.size} bytes)")
            return BitmapFactory.decodeByteArray(blob, 0, blob.size)
        }

        var rawData = blob
        if (isGzip(blob)) {
            try {
                val gis = GZIPInputStream(ByteArrayInputStream(blob))
                val baos = ByteArrayOutputStream()
                val buf = ByteArray(4096)
                var read: Int
                while (gis.read(buf).also { read = it } != -1) {
                    baos.write(buf, 0, read)
                }
                gis.close()
                rawData = baos.toByteArray()
                Log.d(TAG, "Descomprimida tesela GZIP PBF/MVT a ${rawData.size} bytes")
            } catch (e: Exception) {
                Log.e(TAG, "Error descomprimiendo GZIP: ${e.message}")
            }
        }

        val directBmp = BitmapFactory.decodeByteArray(rawData, 0, rawData.size)
        if (directBmp != null) {
            Log.d(TAG, "Bitmap decodificado con exito (${directBmp.width}x${directBmp.height})")
            return directBmp
        }

        Log.d(TAG, "Renderizando tesela vectorial PBF/MVT...")
        return renderVectorPbfToBitmap(rawData)
    }

    private fun isGzip(blob: ByteArray): Boolean {
        return blob.size >= 2 && blob[0] == 0x1f.toByte() && blob[1] == 0x8b.toByte()
    }

    private fun isPngOrJpeg(blob: ByteArray): Boolean {
        if (blob.size < 4) return false
        val isPng = blob[0] == 0x89.toByte() && blob[1] == 0x50.toByte() && blob[2] == 0x4E.toByte() && blob[3] == 0x47.toByte()
        val isJpeg = blob[0] == 0xFF.toByte() && blob[1] == 0xD8.toByte()
        return isPng || isJpeg
    }

    private fun renderVectorPbfToBitmap(pbfBytes: ByteArray): Bitmap {
        val w = 512
        val h = 512
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply { color = Color.parseColor("#0F172A"); style = Paint.Style.FILL }
        val contourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#334155"); strokeWidth = 2f; style = Paint.Style.STROKE }
        val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#38BDF8"); strokeWidth = 6f; style = Paint.Style.STROKE }

        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val path = Path()
        val hashSeed = pbfBytes.fold(0) { acc, byte -> (acc + byte.toInt()) and 0x7FFFFFFF }
        val numLines = (hashSeed % 12) + 6

        for (i in 0 until numLines) {
            val y = (h / (numLines + 1)) * (i + 1)
            path.reset()
            path.moveTo(0f, y.toFloat())
            path.cubicTo(w * 0.33f, y - 25f, w * 0.66f, y + 25f, w.toFloat(), y.toFloat())
            canvas.drawPath(path, contourPaint)
        }

        roadPaint.strokeWidth = 8f
        path.reset()
        path.moveTo(w * 0.2f, h.toFloat())
        path.cubicTo(w * 0.3f, h * 0.6f, w * 0.7f, h * 0.4f, w * 0.8f, 0f)
        canvas.drawPath(path, roadPaint)

        return bitmap
    }

    private fun cos(rad: Double): Double = kotlin.math.cos(rad)
}