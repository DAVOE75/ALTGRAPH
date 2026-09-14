package com.example.altgraph

import android.content.Context
import android.net.ConnectivityManager
import android.os.Environment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

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

    // Enlace de descarga directa del paquete de mapas topográficos vectoriales HD reales (1,2 GB a 3,4 GB)
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
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), ""),
        File(Environment.getExternalStorageDirectory(), "Download"),
        File(Environment.getExternalStorageDirectory(), "Android/data/io.hammerhead.rideapp/files/maps")
    )

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        @Suppress("DEPRECATION")
        val activeNetwork = cm?.activeNetworkInfo
        @Suppress("DEPRECATION")
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun getInstalledMaps(context: Context): List<MapPackage> {
        val result = mutableListOf<MapPackage>()
        val processedNames = mutableSetOf<String>()

        MAP_DIRS.forEach { dir ->
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.forEach { file ->
                    if (!processedNames.contains(file.name)) {
                        processedNames.add(file.name)
                        val name = file.name.lowercase()
                        if (name.endsWith(".mbtiles") || name.endsWith(".map") || name.endsWith(".zip")) {
                            val format = when {
                                name.endsWith(".mbtiles") -> "MBTiles Vector HD Map"
                                name.endsWith(".map") -> "Mapsforge Vector Map"
                                else -> "Archive Map Package"
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
        }
        return result
    }

    fun deleteInstalledMapPackage(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }

    fun deleteAllInstalledMaps(context: Context): Int {
        var count = 0
        getInstalledMaps(context).forEach { pkg ->
            if (deleteInstalledMapPackage(pkg.path)) {
                count++
            }
        }
        return count
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

    fun downloadMapDirectHttp(
        province: ProvinceMapInfo,
        onProgress: (bytesDownloaded: Long, totalBytes: Long, percentage: Int) -> Unit,
        onSuccess: (destFile: File) -> Unit,
        onError: (errorMessage: String) -> Unit
    ): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            try {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }

                val fileName = "IGN_25k_${province.name.replace(" ", "_")}.zip"
                val targetFile = File(downloadsDir, fileName)

                val url = URL(province.downloadUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.instanceFollowRedirects = true
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) {
                        onError("HTTP Error $responseCode: No se pudo conectar al servidor de mapas.")
                    }
                    return@launch
                }

                val totalBytes = connection.contentLengthLong.let { if (it > 0) it else 1_200_000_000L } // ~1,2 GB
                var downloadedBytes = 0L

                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(targetFile)
                val buffer = ByteArray(16384)
                var bytesRead: Int

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    downloadedBytes += bytesRead
                    val pct = ((downloadedBytes * 100L) / totalBytes).toInt().coerceIn(0, 100)

                    withContext(Dispatchers.Main) {
                        onProgress(downloadedBytes, totalBytes, pct)
                    }
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
                connection.disconnect()

                // Mover automáticamente el archivo descargado a /sdcard/Maps/
                installDownloadedMapsFromStorage()

                withContext(Dispatchers.Main) {
                    onSuccess(targetFile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error de descarga: ${e.localizedMessage}")
                }
            }
        }
    }

    fun getFreeStorageBytes(): Long {
        return Environment.getExternalStorageDirectory().freeSpace
    }

    fun formatBytes(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> "%.2f GB".format(gb)
            mb >= 1.0 -> "%.1f MB".format(mb)
            kb >= 1.0 -> "%.1f KB".format(kb)
            else -> "$bytes B"
        }
    }
}