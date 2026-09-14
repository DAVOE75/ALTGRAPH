package com.example.altgraph

import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan

object MbtilesTileReader {

    fun getTileBitmapForLocation(lat: Double, lng: Double, zoom: Int = 14): Bitmap? {
        val mapsDir = File(Environment.getExternalStorageDirectory(), "Maps")
        if (!mapsDir.exists() || !mapsDir.isDirectory) return null

        val allFiles = mapsDir.listFiles() ?: return null

        // Descomprimir automáticamente cualquier archivo .zip presente en /sdcard/Maps/
        allFiles.filter { it.name.lowercase().endsWith(".zip") }.forEach { zipFile ->
            try {
                val zis = ZipInputStream(FileInputStream(zipFile))
                var entry = zis.nextEntry
                while (entry != null) {
                    val entryName = entry.name.lowercase()
                    if (entryName.endsWith(".mbtiles") || entryName.endsWith(".map")) {
                        val outFile = File(mapsDir, File(entry.name).name)
                        if (!outFile.exists()) {
                            val fos = FileOutputStream(outFile)
                            zis.copyTo(fos)
                            fos.flush()
                            fos.close()
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
                zis.close()
            } catch (e: Exception) {}
        }

        val mapFiles = mapsDir.listFiles()?.filter {
            val n = it.name.lowercase()
            n.endsWith(".mbtiles") || n.endsWith(".map")
        }
        if (mapFiles.isNullOrEmpty()) return null

        val mapFile = mapFiles.first()
        if (mapFile.name.lowercase().endsWith(".mbtiles")) {
            return readFromMbtilesDatabase(mapFile, lat, lng, zoom)
        }
        return null
    }

    private fun readFromMbtilesDatabase(mbtilesFile: File, inputLat: Double, inputLng: Double, inputZoom: Int): Bitmap? {
        return try {
            val db = SQLiteDatabase.openDatabase(mbtilesFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)

            var targetLat = inputLat
            var targetLng = inputLng
            var targetZoom = inputZoom

            // 1. Obtener la cobertura geográfica del archivo .mbtiles desde su tabla metadata
            try {
                val boundsCursor = db.rawQuery("SELECT value FROM metadata WHERE name = 'bounds'", null)
                if (boundsCursor.moveToFirst()) {
                    val boundsStr = boundsCursor.getString(0)
                    val parts = boundsStr.split(",")
                    if (parts.size == 4) {
                        val minLng = parts[0].toDoubleOrNull() ?: -2.0
                        val minLat = parts[1].toDoubleOrNull() ?: 37.0
                        val maxLng = parts[2].toDoubleOrNull() ?: -0.5
                        val maxLat = parts[3].toDoubleOrNull() ?: 38.5

                        // Si estamos sin fijar GPS (0.0, 0.0) o fuera del mapa, centramos en el medio del mapa .mbtiles
                        if (targetLat == 0.0 || targetLng == 0.0 || targetLat < minLat || targetLat > maxLat || targetLng < minLng || targetLng > maxLng) {
                            targetLat = (minLat + maxLat) / 2.0
                            targetLng = (minLng + maxLng) / 2.0
                        }
                    }
                }
                boundsCursor.close()
            } catch (e: Exception) {}

            // 2. Obtener los niveles de zoom disponibles en la base de datos
            try {
                val zoomCursor = db.rawQuery("SELECT MIN(zoom_level), MAX(zoom_level) FROM tiles", null)
                if (zoomCursor.moveToFirst()) {
                    val minZ = zoomCursor.getInt(0)
                    val maxZ = zoomCursor.getInt(1)
                    if (maxZ > 0) {
                        targetZoom = targetZoom.coerceIn(minZ, maxZ)
                    }
                }
                zoomCursor.close()
            } catch (e: Exception) {}

            // 3. Buscar la tesela de imagen
            val tileX = floor((targetLng + 180.0) / 360.0 * (1 shl targetZoom)).toInt()
            val latRad = Math.toRadians(targetLat)
            val tileYOsm = floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * (1 shl targetZoom)).toInt()
            val tileYTms = ((1 shl targetZoom) - 1) - tileYOsm

            var cursor = db.rawQuery(
                "SELECT tile_data FROM tiles WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?",
                arrayOf(targetZoom.toString(), tileX.toString(), tileYTms.toString())
            )

            // Si no se encuentra en la coordenada TMS exacta, buscar cualquier tesela disponible para mostrar el mapa en pantalla
            if (!cursor.moveToFirst()) {
                cursor.close()
                cursor = db.rawQuery("SELECT tile_data FROM tiles LIMIT 1", null)
            }

            var bitmap: Bitmap? = null
            if (cursor.moveToFirst()) {
                val blob = cursor.getBlob(0)
                if (blob != null && blob.isNotEmpty()) {
                    bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                }
            }
            cursor.close()
            db.close()
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun cos(rad: Double): Double = kotlin.math.cos(rad)
}