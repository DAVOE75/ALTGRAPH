package com.example.altgraph

import android.content.Context
import android.os.Environment
import java.io.File

data class MapPackage(
    val name: String,
    val sizeBytes: Long,
    val path: String,
    val format: String,
    val isCustom: Boolean
)

object MapManager {

    private val MAP_DIRS = listOf(
        File(Environment.getExternalStorageDirectory(), "Maps"),
        File(Environment.getExternalStorageDirectory(), "Hammerhead/maps"),
        File(Environment.getExternalStorageDirectory(), "Android/data/io.hammerhead.rideapp/files/maps")
    )

    fun getInstalledMaps(context: Context): List<MapPackage> {
        val result = mutableListOf<MapPackage>()

        MAP_DIRS.forEach { dir ->
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.forEach { file ->
                    val name = file.name.lowercase()
                    if (name.endsWith(".mbtiles") || name.endsWith(".map") || name.endsWith(".zip")) {
                        val format = when {
                            name.endsWith(".mbtiles") -> "MBTiles (BikeSpot/Vector)"
                            name.endsWith(".map") -> "Mapsforge (OpenAndroMaps)"
                            else -> "Archive Package"
                        }
                        result.add(
                            MapPackage(
                                name = file.name,
                                sizeBytes = file.length(),
                                path = file.absolutePath,
                                format = format,
                                isCustom = true
                            )
                        )
                    }
                }
            }
        }
        return result
    }

    fun getFreeStorageBytes(): Long {
        return Environment.getExternalStorageDirectory().freeSpace
    }

    fun formatBytes(bytes: Long): String {
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb >= 1024) {
            "%.2f GB".format(mb / 1024.0)
        } else {
            "%.1f MB".format(mb)
        }
    }
}