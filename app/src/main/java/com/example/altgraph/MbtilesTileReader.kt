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

    private fun readFromMbtilesDatabase(mbtilesFile: File, lat: Double, lng: Double, zoom: Int): Bitmap? {
        return try {
            val db = SQLiteDatabase.openDatabase(mbtilesFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY)
            val tileX = floor((lng + 180.0) / 360.0 * (1 shl zoom)).toInt()
            val latRad = Math.toRadians(lat)
            val tileYOsm = floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / PI) / 2.0 * (1 shl zoom)).toInt()
            val tileYTms = ((1 shl zoom) - 1) - tileYOsm

            val cursor = db.rawQuery(
                "SELECT tile_data FROM tiles WHERE zoom_level = ? AND tile_column = ? AND tile_row = ?",
                arrayOf(zoom.toString(), tileX.toString(), tileYTms.toString())
            )

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