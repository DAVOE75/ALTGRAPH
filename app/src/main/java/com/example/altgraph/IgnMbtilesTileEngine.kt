package com.example.altgraph

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
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

    private const val TAG = "IGN_MAP"

    fun getInstalledIgnMaps(context: Context): List<IgnMapInfo> {
        val result = mutableListOf<IgnMapInfo>()
        val processedNames = mutableSetOf<String>()
        val activeMapName = AppPreferences.getInstance(context).customMapProvider

        val searchDirs = listOf(
            context.filesDir,
            context.getExternalFilesDir("maps") ?: File(""),
            File("/sdcard/offline/maps"),
            File("/sdcard/maps"),
            File("/sdcard/Maps"),
            File("/storage/emulated/0/offline/maps"),
            File(Environment.getExternalStorageDirectory(), "offline/maps")
        ).filter { it != File("") }

        searchDirs.forEach { dir ->
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.forEach { file ->
                    if (file.isFile && file.name.lowercase().endsWith(".mbtiles") && !processedNames.contains(file.name.lowercase())) {
                        processedNames.add(file.name.lowercase())
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

    fun getTileBitmap(context: Context, lat: Double, lng: Double, preferredZoom: Int = 14): Bitmap? {
        val maps = getInstalledIgnMaps(context)
        if (maps.isEmpty()) return null

        val prefs = AppPreferences.getInstance(context)
        val selectedName = prefs.customMapProvider

        val activeMaps = mutableListOf<IgnMapInfo>()
        maps.find { it.fileName.equals(selectedName, ignoreCase = true) }?.let { activeMaps.add(it) }
        activeMaps.addAll(maps.filter { !it.fileName.equals(selectedName, ignoreCase = true) })

        for (mapInfo in activeMaps) {
            val bmp = queryExactTileFromMap(mapInfo, lat, lng, preferredZoom)
            if (bmp != null) return bmp
        }

        return null
    }

    private fun queryExactTileFromMap(mapInfo: IgnMapInfo, inputLat: Double, inputLng: Double, preferredZoom: Int): Bitmap? {
        return try {
            val sourceFile = File(mapInfo.filePath)
            if (!sourceFile.exists()) return null

            val flags = SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS
            val db = SQLiteDatabase.openDatabase(sourceFile.absolutePath, null, flags)

            var targetLat = inputLat
            var targetLng = inputLng

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

            var minZ = 11
            var maxZ = 17
            try {
                val zCursor = db.rawQuery("SELECT MIN(zoom_level), MAX(zoom_level) FROM tiles", null)
                if (zCursor.moveToFirst()) {
                    val mz = zCursor.getInt(0)
                    val xz = zCursor.getInt(1)
                    if (xz > 0) {
                        minZ = mz
                        maxZ = xz
                    }
                }
                zCursor.close()
            } catch (e: Exception) {}

            val zoomLevelsToTry = mutableListOf<Int>()
            val startZoom = preferredZoom.coerceIn(minZ, maxZ)
            zoomLevelsToTry.add(startZoom)

            for (z in maxZ downTo minZ) {
                if (!zoomLevelsToTry.contains(z)) {
                    zoomLevelsToTry.add(z)
                }
            }

            for (z in zoomLevelsToTry) {
                val tileX = floor((targetLng + 180.0) / 360.0 * (1 shl z)).toInt()
                val latRad = Math.toRadians(targetLat)
                val tileYOsm = floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * (1 shl z)).toInt()
                val tileYTms = ((1 shl z) - 1) - tileYOsm

                val queryTms = "SELECT tile_data FROM tiles WHERE zoom_level = $z AND tile_column = $tileX AND tile_row = $tileYTms"
                val queryOsm = "SELECT tile_data FROM tiles WHERE zoom_level = $z AND tile_column = $tileX AND tile_row = $tileYOsm"

                for (q in listOf(queryTms, queryOsm)) {
                    try {
                        val c = db.rawQuery(q, null)
                        if (c.moveToFirst()) {
                            val blob = c.getBlob(0)
                            if (blob != null && blob.isNotEmpty()) {
                                val bmp = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                                c.close()
                                db.close()
                                if (bmp != null) return bmp
                            }
                        }
                        c.close()
                    } catch (e: Exception) {}
                }
            }

            try {
                val fc = db.rawQuery("SELECT tile_data FROM tiles LIMIT 1", null)
                if (fc.moveToFirst()) {
                    val blob = fc.getBlob(0)
                    if (blob != null && blob.isNotEmpty()) {
                        val bmp = BitmapFactory.decodeByteArray(blob, 0, blob.size)
                        fc.close()
                        db.close()
                        if (bmp != null) return bmp
                    }
                }
                fc.close()
            } catch (e: Exception) {}

            db.close()
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun cos(rad: Double): Double = kotlin.math.cos(rad)
}