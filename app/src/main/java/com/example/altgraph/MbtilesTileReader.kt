package com.example.altgraph

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan

object MbtilesTileReader {

    fun getTileBitmapForLocation(context: Context, lat: Double, lng: Double, zoom: Int = 14): Bitmap? {
        val searchDirs = listOf(
            File(Environment.getExternalStorageDirectory(), "offline/maps"),
            File(Environment.getExternalStorageDirectory(), "offline"),
            File(Environment.getExternalStorageDirectory(), "Maps"),
            File("/sdcard/offline/maps"),
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

        if (mbtilesFiles.isEmpty()) return null

        // Si el usuario seleccionó un mapa activo preferido en AppPreferences, usar ese primero
        val prefs = AppPreferences.getInstance(context)
        val selectedMapName = prefs.customMapProvider

        val preferredMap = mbtilesFiles.find { it.name.equals(selectedMapName, ignoreCase = true) }
        if (preferredMap != null) {
            val bmp = readFromMbtilesDatabase(preferredMap, lat, lng, zoom)
            if (bmp != null) return bmp
        }

        // Probar cada archivo .mbtiles disponible (ej. murcia_sureste.mbtiles)
        mbtilesFiles.forEach { file ->
            val bmp = readFromMbtilesDatabase(file, lat, lng, zoom)
            if (bmp != null) return bmp
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
                        val minLng = parts[0].toDoubleOrNull() ?: -2.5
                        val minLat = parts[1].toDoubleOrNull() ?: 37.0
                        val maxLng = parts[2].toDoubleOrNull() ?: -0.5
                        val maxLat = parts[3].toDoubleOrNull() ?: 38.8

                        // Si estamos sin fijar GPS (0.0, 0.0) o fuera del mapa, centramos en el medio del mapa .mbtiles (Murcia Sureste)
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

            // 3. Coordenadas de tesela TMS y OSM
            val tileX = floor((targetLng + 180.0) / 360.0 * (1 shl targetZoom)).toInt()
            val latRad = Math.toRadians(targetLat)
            val tileYOsm = floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * (1 shl targetZoom)).toInt()
            val tileYTms = ((1 shl targetZoom) - 1) - tileYOsm

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
                            bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                        }
                    }
                    cursor.close()
                    if (bitmap != null) break
                } catch (e: Exception) {}
            }

            db.close()
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun cos(rad: Double): Double = kotlin.math.cos(rad)
}