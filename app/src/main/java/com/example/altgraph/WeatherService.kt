package com.example.altgraph

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import org.json.JSONObject

object WeatherService {

    var currentWindSpeedKmh: Double = 0.0
    var currentWindDirectionDegrees: Int = 0
    var isDataFresh: Boolean = false

    fun fetchWeatherData(lat: Double, lon: Double) {
        thread {
            try {
                // Open-Meteo API (Free, no key required)
                val urlString = "https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current_weather=true"
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val json = JSONObject(response)
                    if (json.has("current_weather")) {
                        val current = json.getJSONObject("current_weather")
                        val windSpeed = current.optDouble("windspeed", 0.0)
                        val windDir = current.optInt("winddirection", 0)

                        Handler(Looper.getMainLooper()).post {
                            currentWindSpeedKmh = windSpeed
                            currentWindDirectionDegrees = windDir
                            isDataFresh = true
                            Log.d("WeatherService", "Weather updated: $windSpeed km/h at $windDir deg")
                        }
                    }
                }
                connection.disconnect()
            } catch (e: Exception) {
                Log.e("WeatherService", "Error fetching weather", e)
            }
        }
    }
}
