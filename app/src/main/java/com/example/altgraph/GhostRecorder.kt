package com.example.altgraph

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * GhostRecorder - records (distance, elapsedMs) checkpoints for a route,
 * saves the best run per route name, and provides ghost position during playback.
 */
class GhostRecorder(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ghost_records", Context.MODE_PRIVATE)

    data class Checkpoint(val distanceMeters: Double, val elapsedMs: Long)

    private var recordingRoute: String? = null
    private var recordingStart: Long = 0L
    private var lastCheckpointDist: Double = -1.0
    private val currentCheckpoints = mutableListOf<Checkpoint>()
    private var ghostCheckpoints: List<Checkpoint> = emptyList()

    private val CHECKPOINT_INTERVAL = 50.0

    fun startRoute(routeName: String) {
        recordingRoute = routeName
        recordingStart = 0L
        lastCheckpointDist = -1.0
        currentCheckpoints.clear()
        ghostCheckpoints = loadGhost(routeName)
    }

    fun update(riderDistanceMeters: Double) {
        if (riderDistanceMeters < 0) return
        val now = System.currentTimeMillis()
        if (recordingStart == 0L && riderDistanceMeters >= 0.0) {
            recordingStart = now
            lastCheckpointDist = 0.0
            currentCheckpoints.add(Checkpoint(0.0, 0L))
            return
        }
        if (recordingStart == 0L) return
        val elapsed = now - recordingStart
        if (riderDistanceMeters - lastCheckpointDist >= CHECKPOINT_INTERVAL) {
            currentCheckpoints.add(Checkpoint(riderDistanceMeters, elapsed))
            lastCheckpointDist = riderDistanceMeters
        }
    }

    fun finishRoute() {
        val name = recordingRoute ?: return
        if (currentCheckpoints.size < 5) return
        val bestMs = ghostCheckpoints.lastOrNull()?.elapsedMs
        val currentMs = currentCheckpoints.lastOrNull()?.elapsedMs ?: return
        if (bestMs == null || currentMs < bestMs) {
            saveGhost(name, currentCheckpoints)
            ghostCheckpoints = currentCheckpoints.toList()
        }
    }

    fun clearRoute() {
        recordingRoute = null
        recordingStart = 0L
        currentCheckpoints.clear()
        ghostCheckpoints = emptyList()
    }

    fun ghostDistanceAtTime(elapsedMs: Long): Double? {
        if (ghostCheckpoints.size < 2) return null
        if (elapsedMs <= 0) return ghostCheckpoints.first().distanceMeters
        for (i in 1 until ghostCheckpoints.size) {
            val prev = ghostCheckpoints[i - 1]
            val next = ghostCheckpoints[i]
            if (elapsedMs <= next.elapsedMs) {
                val fraction = if (next.elapsedMs > prev.elapsedMs)
                    (elapsedMs - prev.elapsedMs).toDouble() / (next.elapsedMs - prev.elapsedMs)
                else 0.0
                return prev.distanceMeters + fraction * (next.distanceMeters - prev.distanceMeters)
            }
        }
        val last = ghostCheckpoints.last()
        val secondLast = ghostCheckpoints[ghostCheckpoints.size - 2]
        val speed = if (last.elapsedMs > secondLast.elapsedMs)
            (last.distanceMeters - secondLast.distanceMeters) /
                    (last.elapsedMs - secondLast.elapsedMs).toDouble()
        else 0.0
        return last.distanceMeters + speed * (elapsedMs - last.elapsedMs)
    }

    fun ghostRelativeToRider(riderDistanceMeters: Double): Double? {
        if (recordingStart == 0L) return null
        val elapsed = System.currentTimeMillis() - recordingStart
        val ghostDist = ghostDistanceAtTime(elapsed) ?: return null
        return ghostDist - riderDistanceMeters
    }

    fun hasGhost(): Boolean = ghostCheckpoints.size >= 2
    fun currentElapsedMs(): Long = if (recordingStart > 0L) System.currentTimeMillis() - recordingStart else 0L

    private fun saveGhost(routeName: String, checkpoints: List<Checkpoint>) {
        val arr = JSONArray()
        for (cp in checkpoints) {
            val obj = JSONObject()
            obj.put("d", cp.distanceMeters)
            obj.put("t", cp.elapsedMs)
            arr.put(obj)
        }
        prefs.edit().putString("ghost_${routeName.take(80)}", arr.toString()).apply()
    }

    private fun loadGhost(routeName: String): List<Checkpoint> {
        val json = prefs.getString("ghost_${routeName.take(80)}", null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                Checkpoint(obj.getDouble("d"), obj.getLong("t"))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun deleteGhost(routeName: String) {
        prefs.edit().remove("ghost_${routeName.take(80)}").apply()
        if (recordingRoute == routeName) ghostCheckpoints = emptyList()
    }
}
