package com.example.altgraph

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
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

    const val CNIG_PORTAL_URL = "https://centrodedescargas.cnig.es/CentroDescargas/mapas-moviles"

    // Enlaces de descarga directa OpenData IGN / OpenAndroMaps Spain Topo
    private const val BASE_MAP_URL = "https://ftp.snt.utwente.nl/pub/misc/openandromaps/maps/europe/Spain_Portugal.zip"

    val PROVINCES = listOf(
        ProvinceMapInfo("Álava", BASE_MAP_URL),
        ProvinceMapInfo("Albacete", BASE_MAP_URL),
        ProvinceMapInfo("Alicante", BASE_MAP_URL),
        ProvinceMapInfo("Almería", BASE_MAP_URL),
        ProvinceMapInfo("Asturias", BASE_MAP_URL),
        ProvinceMapInfo("Ávila", BASE_MAP_URL),
        ProvinceMapInfo("Badajoz", BASE_MAP_URL),
        ProvinceMapInfo("Baleares", BASE_MAP_URL),
        ProvinceMapInfo("Barcelona", BASE_MAP_URL),
        ProvinceMapInfo("Burgos", BASE_MAP_URL),
        ProvinceMapInfo("Cáceres", BASE_MAP_URL),
        ProvinceMapInfo("Cádiz", BASE_MAP_URL),
        ProvinceMapInfo("Cantabria", BASE_MAP_URL),
        ProvinceMapInfo("Castellón", BASE_MAP_URL),
        ProvinceMapInfo("Ciudad Real", BASE_MAP_URL),
        ProvinceMapInfo("Córdoba", BASE_MAP_URL),
        ProvinceMapInfo("Cuenca", BASE_MAP_URL),
        ProvinceMapInfo("Girona", BASE_MAP_URL),
        ProvinceMapInfo("Granada", BASE_MAP_URL),
        ProvinceMapInfo("Guadalajara", BASE_MAP_URL),
        ProvinceMapInfo("Guipúzcoa", BASE_MAP_URL),
        ProvinceMapInfo("Huelva", BASE_MAP_URL),
        ProvinceMapInfo("Huesca", BASE_MAP_URL),
        ProvinceMapInfo("Jaén", BASE_MAP_URL),
        ProvinceMapInfo("La Rioja", BASE_MAP_URL),
        ProvinceMapInfo("Las Palmas", BASE_MAP_URL),
        ProvinceMapInfo("León", BASE_MAP_URL),
        ProvinceMapInfo("Lleida", BASE_MAP_URL),
        ProvinceMapInfo("Lugo", BASE_MAP_URL),
        ProvinceMapInfo("Madrid", BASE_MAP_URL),
        ProvinceMapInfo("Málaga", BASE_MAP_URL),
        ProvinceMapInfo("Murcia", BASE_MAP_URL),
        ProvinceMapInfo("Navarra", BASE_MAP_URL),
        ProvinceMapInfo("Ourense", BASE_MAP_URL),
        ProvinceMapInfo("Palencia", BASE_MAP_URL),
        ProvinceMapInfo("Pontevedra", BASE_MAP_URL),
        ProvinceMapInfo("Salamanca", BASE_MAP_URL),
        ProvinceMapInfo("Santa Cruz de Tenerife", BASE_MAP_URL),
        ProvinceMapInfo("Segovia", BASE_MAP_URL),
        ProvinceMapInfo("Sevilla", BASE_MAP_URL),
        ProvinceMapInfo("Soria", BASE_MAP_URL),
        ProvinceMapInfo("Tarragona", BASE_MAP_URL),
        ProvinceMapInfo("Teruel", BASE_MAP_URL),
        ProvinceMapInfo("Toledo", BASE_MAP_URL),
        ProvinceMapInfo("Valencia", BASE_MAP_URL),
        ProvinceMapInfo("Valladolid", BASE_MAP_URL),
        ProvinceMapInfo("Vizcaya", BASE_MAP_URL),
        ProvinceMapInfo("Zamora", BASE_MAP_URL),
        ProvinceMapInfo("Zaragoza", BASE_MAP_URL)
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

    fun openCnigPortal(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CNIG_PORTAL_URL)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
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

        val destinationFile = File(mapsDir, "IGN_25k_${province.name.replace(" ", "_")}.zip")
        val request = DownloadManager.Request(Uri.parse(province.downloadUrl)).apply {
            setTitle("Descargando Mapa Topo 1:25.000 ${province.name}")
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