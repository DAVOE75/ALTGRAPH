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

data class IgnMapInfo(
    val fileName: String,
    val filePath: String,
    val sizeBytes: Long,
    val isActive: Boolean
)

object IgnMbtilesTileEngine {

    private var activeDb: SQLiteDatabase? = null
    private var activeDbPath: String? = null

    private val MAP_SEARCH_DIRS = listOf(
        File("/sdcard/offline/maps"),
        File("/storage/emulated/0/offline/maps"),
        File(Environment.getExternalStorageDirectory(), "offline/maps"),
        File("/sdcard/Maps"),
        File(Environment.getExternalStorageDirectory(), "Maps")
    )

    fun getInstalledIgnMaps(context: Context): List<IgnMapInfo> {
        val result = mutableListOf<IgnMapInfo>()
        val processedPaths = mutableSetOf<String>()
        val activeMapName = AppPreferences.getInstance(context).customMapProvider

        MAP_SEARCH_DIRS.forEach { dir ->
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.forEach { file ->
                    if (file.isFile && file.name.lowercase().endsWith(".mbtiles") && !processedPaths.contains(file.name.lowercase())) {
                        processedPaths.add(file.name.lowercase())
                        val isActive = file.name.equals(activeMapName, ignoreCase = true) || (activeMapName.isEmpty() && result.isEmpty())
                        result.add(
                            IgnMapInfo(
                                fileName = file.name,
                                filePath = file.absolutePath,
                                sizeBytes = file.length(),
                                isActive = isActive
                            )
                        )
                    }
                }
            }
        }
        return result
    }

    fun getTileBitmap(context: Context, lat: Double, lng: Double, zoom: Int = 14): Bitmap? {
        val maps = getInstalledIgnMaps(context)
        if (maps.isEmpty()) return null

        val prefs = AppPreferences.getInstance(context)
        val selectedName = prefs.customMapProvider
        val targetMap = maps.find { it.fileName.equals(selectedName, ignoreCase = true) } ?: maps.first()

        if (activeDbPath != targetMap.filePath || activeDb == null || !activeDb!!.isOpen) {
            try {
                activeDb?.close()
                val flags = SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS
                activeDb = SQLiteDatabase.openDatabase(targetMap.filePath, null, flags)
                activeDbPath = targetMap.filePath
            } catch (e: Exception) {
                return null
            }
        }

        val db = activeDb ?: return null

        return try {
            var targetLat = lat
            var targetLng = lng
            var targetZoom = zoom

            // 1. Obtener limites geográficos de la tabla metadata del mapa IGN
            try {
                val cursor = db.rawQuery("SELECT value FROM metadata WHERE name = 'bounds'", null)
                if (cursor.moveToFirst()) {
                    val boundsStr = cursor.getString(0)
                    val parts = boundsStr.split(",")
                    if (parts.size == 4) {
                        val minLng = parts[0].toDoubleOrNull() ?: -18.0
                        val minLat = parts[1].toDoubleOrNull() ?: 27.0
                        val maxLng = parts[2].toDoubleOrNull() ?: 4.0
                        val maxLat = parts[3].toDoubleOrNull() ?: 44.0

                        if (targetLat == 0.0 || targetLng == 0.0 || targetLat < minLat || targetLat > maxLat || targetLng < minLng || targetLng > maxLng) {
                            targetLat = (minLat + maxLat) / 2.0
                            targetLng = (minLng + maxLng) / 2.0
                        }
                    }
                }
                cursor.close()
            } catch (e: Exception) {}

            // 2. Obtener los niveles de zoom soportados por este fichero MBTiles del IGN
            try {
                val zCursor = db.rawQuery("SELECT MIN(zoom_level), MAX(zoom_level) FROM tiles", null)
                if (zCursor.moveToFirst()) {
                    val minZ = zCursor.getInt(0)
                    val maxZ = zCursor.getInt(1)
                    if (maxZ > 0) {
                        targetZoom = targetZoom.coerceIn(minZ, maxZ)
                    }
                }
                zCursor.close()
            } catch (e: Exception) {}

            // 3. Convertir coordenadas GPS a indices de teselas TMS / OSM
            val tileX = floor((targetLng + 180.0) / 360.0 * (1 shl targetZoom)).toInt()
            val latRad = Math.toRadians(targetLat)
            val tileYOsm = floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * (1 shl targetZoom)).toInt()
            val tileYTms = ((1 shl targetZoom) - 1) - tileYOsm

            val queries = listOf(
                "SELECT tile_data FROM tiles WHERE zoom_level = $targetZoom AND tile_column = $tileX AND tile_row = $tileYTms",
                "SELECT tile_data FROM tiles WHERE zoom_level = $targetZoom AND tile_column = $tileX AND tile_row = $tileYOsm",
                "SELECT tile_data FROM tiles WHERE zoom_level = $targetZoom LIMIT 1",
                "SELECT tile_data FROM tiles LIMIT 1"
            )

            var bitmap: Bitmap? = null
            for (querySql in queries) {
                try {
                    val c = db.rawQuery(querySql, null)
                    if (c.moveToFirst()) {
                        val blob = c.getBlob(0)
                        if (blob != null && blob.isNotEmpty()) {
                            bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                        }
                    }
                    c.close()
                    if (bitmap != null) break
                } catch (e: Exception) {}
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun cos(rad: Double): Double = kotlin.math.cos(rad)
}