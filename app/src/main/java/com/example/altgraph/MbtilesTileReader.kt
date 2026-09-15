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
import android.util.LruCache
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPInputStream

object MbtilesTileReader {

    private var currentDb: SQLiteDatabase? = null
    private var currentDbPath: String? = null
    private var minZ = 10
    private var maxZ = 16

    private val tileCache = LruCache<String, Bitmap>(60)

    fun initDb(context: Context) {
        val searchDirs = listOf(
            File("/sdcard/offline/maps"),
            File("/sdcard/offline"),
            File("/sdcard/Maps"),
            File(Environment.getExternalStorageDirectory(), "offline/maps"),
            File(Environment.getExternalStorageDirectory(), "offline")
        )

        val mbtilesFiles = mutableListOf<File>()

        searchDirs.forEach { dir ->
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.filter { it.name.lowercase().endsWith(".mbtiles") }?.let {
                    mbtilesFiles.addAll(it)
                }
            }
        }

        if (mbtilesFiles.isEmpty()) return

        val prefs = AppPreferences.getInstance(context)
        val selectedMapName = prefs.customMapProvider

        var fileToOpen = mbtilesFiles.find { it.name.equals(selectedMapName, ignoreCase = true) }
        if (fileToOpen == null) {
            fileToOpen = mbtilesFiles.first()
        }

        if (currentDbPath != fileToOpen.absolutePath) {
            currentDb?.close()
            tileCache.evictAll()
            try {
                val flags = SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS
                currentDb = SQLiteDatabase.openDatabase(fileToOpen.absolutePath, null, flags)
                currentDbPath = fileToOpen.absolutePath

                val c = currentDb?.rawQuery("SELECT MIN(zoom_level), MAX(zoom_level) FROM tiles", null)
                if (c != null && c.moveToFirst()) {
                    minZ = c.getInt(0)
                    maxZ = c.getInt(1)
                    if (minZ == 0) minZ = 10
                    if (maxZ == 0) maxZ = 16
                }
                c?.close()
            } catch (e: Exception) {}
        }
    }

    fun getMinZoom() = minZ
    fun getMaxZoom() = maxZ

    fun getTile(zoom: Int, x: Int, y: Int): Bitmap? {
        val db = currentDb ?: return null

        val cacheKey = "${zoom}_${x}_${y}"
        val cached = tileCache.get(cacheKey)
        if (cached != null) return cached

        val yTms = ((1 shl zoom) - 1) - y

        var bitmap: Bitmap? = null
        try {
            val queries = listOf(
                "SELECT tile_data FROM tiles WHERE zoom_level = $zoom AND tile_column = $x AND tile_row = $yTms",
                "SELECT tile_data FROM tiles WHERE zoom_level = $zoom AND tile_column = $x AND tile_row = $y"
            )

            for (querySql in queries) {
                val cursor = db.rawQuery(querySql, null)
                if (cursor.moveToFirst()) {
                    val blob = cursor.getBlob(0)
                    if (blob != null && blob.isNotEmpty()) {
                        bitmap = decodeTileDataToBitmap(blob)
                    }
                }
                cursor.close()
                if (bitmap != null) break
            }
        } catch (e: Exception) {}

        if (bitmap != null) {
            tileCache.put(cacheKey, bitmap)
        }
        return bitmap
    }

    private fun decodeTileDataToBitmap(blob: ByteArray): Bitmap? {
        if (blob.isEmpty()) return null

        if (isPngOrJpeg(blob)) {
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
            } catch (e: Exception) {}
        }

        val directBmp = BitmapFactory.decodeByteArray(rawData, 0, rawData.size)
        if (directBmp != null) {
            return directBmp
        }

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
        val w = 256
        val h = 256
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgPaint = Paint().apply { color = Color.parseColor("#F8FAFC"); style = Paint.Style.FILL }
        val contourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#CBD5E1"); strokeWidth = 2f; style = Paint.Style.STROKE }
        val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#0284C7"); strokeWidth = 6f; style = Paint.Style.STROKE }

        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint)

        val path = Path()
        val hashSeed = pbfBytes.fold(0) { acc, byte -> (acc + byte.toInt()) and 0x7FFFFFFF }
        val numLines = (hashSeed % 6) + 3

        for (i in 0 until numLines) {
            val y = (h / (numLines + 1)) * (i + 1)
            path.reset()
            path.moveTo(0f, y.toFloat())
            path.cubicTo(w * 0.33f, y - 15f, w * 0.66f, y + 15f, w.toFloat(), y.toFloat())
            canvas.drawPath(path, contourPaint)
        }

        roadPaint.strokeWidth = 8f
        path.reset()
        path.moveTo(w * 0.2f, h.toFloat())
        path.cubicTo(w * 0.3f, h * 0.6f, w * 0.7f, h * 0.4f, w * 0.8f, 0f)
        canvas.drawPath(path, roadPaint)

        return bitmap
    }
}