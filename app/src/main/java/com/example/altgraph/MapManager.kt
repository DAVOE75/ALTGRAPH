package com.example.altgraph

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File

data class MapPackage(
    val name: String,
    val sizeBytes: Long,
    val path: String,
    val format: String,
    val isCustom: Boolean
)

data class ProvinceMapInfo(
    val name: String,
    val downloadUrl: String
)

object MapManager {

    val PROVINCES = listOf(
        ProvinceMapInfo("Álava", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/alava_25k.mbtiles"),
        ProvinceMapInfo("Albacete", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/albacete_25k.mbtiles"),
        ProvinceMapInfo("Alicante", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/alicante_25k.mbtiles"),
        ProvinceMapInfo("Almería", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/almeria_25k.mbtiles"),
        ProvinceMapInfo("Asturias", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/asturias_25k.mbtiles"),
        ProvinceMapInfo("Ávila", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/avila_25k.mbtiles"),
        ProvinceMapInfo("Badajoz", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/badajoz_25k.mbtiles"),
        ProvinceMapInfo("Baleares", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/baleares_25k.mbtiles"),
        ProvinceMapInfo("Barcelona", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/barcelona_25k.mbtiles"),
        ProvinceMapInfo("Burgos", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/burgos_25k.mbtiles"),
        ProvinceMapInfo("Cáceres", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/caceres_25k.mbtiles"),
        ProvinceMapInfo("Cádiz", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/cadiz_25k.mbtiles"),
        ProvinceMapInfo("Cantabria", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/cantabria_25k.mbtiles"),
        ProvinceMapInfo("Castellón", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/castellon_25k.mbtiles"),
        ProvinceMapInfo("Ciudad Real", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/ciudad_real_25k.mbtiles"),
        ProvinceMapInfo("Córdoba", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/cordoba_25k.mbtiles"),
        ProvinceMapInfo("Cuenca", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/cuenca_25k.mbtiles"),
        ProvinceMapInfo("Girona", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/girona_25k.mbtiles"),
        ProvinceMapInfo("Granada", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/granada_25k.mbtiles"),
        ProvinceMapInfo("Guadalajara", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/guadalajara_25k.mbtiles"),
        ProvinceMapInfo("Guipúzcoa", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/guipuzcoa_25k.mbtiles"),
        ProvinceMapInfo("Huelva", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/huelva_25k.mbtiles"),
        ProvinceMapInfo("Huesca", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/huesca_25k.mbtiles"),
        ProvinceMapInfo("Jaén", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/jaen_25k.mbtiles"),
        ProvinceMapInfo("La Rioja", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/la_rioja_25k.mbtiles"),
        ProvinceMapInfo("Las Palmas", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/las_palmas_25k.mbtiles"),
        ProvinceMapInfo("León", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/leon_25k.mbtiles"),
        ProvinceMapInfo("Lleida", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/lleida_25k.mbtiles"),
        ProvinceMapInfo("Lugo", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/lugo_25k.mbtiles"),
        ProvinceMapInfo("Madrid", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/madrid_25k.mbtiles"),
        ProvinceMapInfo("Málaga", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/malaga_25k.mbtiles"),
        ProvinceMapInfo("Murcia", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/murcia_25k.mbtiles"),
        ProvinceMapInfo("Navarra", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/navarra_25k.mbtiles"),
        ProvinceMapInfo("Ourense", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/ourense_25k.mbtiles"),
        ProvinceMapInfo("Palencia", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/palencia_25k.mbtiles"),
        ProvinceMapInfo("Pontevedra", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/pontevedra_25k.mbtiles"),
        ProvinceMapInfo("Salamanca", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/salamanca_25k.mbtiles"),
        ProvinceMapInfo("Santa Cruz de Tenerife", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/tenerife_25k.mbtiles"),
        ProvinceMapInfo("Segovia", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/segovia_25k.mbtiles"),
        ProvinceMapInfo("Sevilla", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/sevilla_25k.mbtiles"),
        ProvinceMapInfo("Soria", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/soria_25k.mbtiles"),
        ProvinceMapInfo("Tarragona", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/tarragona_25k.mbtiles"),
        ProvinceMapInfo("Teruel", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/teruel_25k.mbtiles"),
        ProvinceMapInfo("Toledo", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/toledo_25k.mbtiles"),
        ProvinceMapInfo("Valencia", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/valencia_25k.mbtiles"),
        ProvinceMapInfo("Valladolid", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/valladolid_25k.mbtiles"),
        ProvinceMapInfo("Vizcaya", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/vizcaya_25k.mbtiles"),
        ProvinceMapInfo("Zamora", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/zamora_25k.mbtiles"),
        ProvinceMapInfo("Zaragoza", "https://raw.githubusercontent.com/DAVOE75/ALTGRAPH/main/maps/zaragoza_25k.mbtiles")
    )

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
                            name.endsWith(".mbtiles") -> "MBTiles (IGN 1:25.000 / BikeSpot)"
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

    fun installDownloadedMapsFromStorage(): Int {
        var movedCount = 0
        val targetDir = MAP_DIRS[0]
        if (!targetDir.exists()) targetDir.mkdirs()

        val downloadDirs = listOf(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            File(Environment.getExternalStorageDirectory(), "Download")
        )

        downloadDirs.forEach { dDir ->
            if (dDir.exists() && dDir.isDirectory) {
                dDir.listFiles()?.forEach { f ->
                    val name = f.name.lowercase()
                    if (name.endsWith(".mbtiles") || name.endsWith(".map") || name.endsWith(".zip")) {
                        val destFile = File(targetDir, f.name)
                        try {
                            if (f.renameTo(destFile) || f.copyTo(destFile, overwrite = true).exists()) {
                                movedCount++
                                if (f.exists()) f.delete()
                            }
                        } catch (e: Exception) {}
                    }
                }
            }
        }
        return movedCount
    }

    fun downloadProvinceMap(context: Context, province: ProvinceMapInfo): Long {
        val mapsDir = MAP_DIRS.firstOrNull { it.exists() } ?: MAP_DIRS[0]
        if (!mapsDir.exists()) {
            mapsDir.mkdirs()
        }

        val destinationFile = File(mapsDir, "IGN_25k_${province.name.replace(" ", "_")}.mbtiles")
        val request = DownloadManager.Request(Uri.parse(province.downloadUrl)).apply {
            setTitle("Descargando Mapa IGN 1:25.000 ${province.name}")
            setDescription("Cartografía topográfica de alta definición para Karoo")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationUri(Uri.fromFile(destinationFile))
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return downloadManager.enqueue(request)
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