package com.example.altgraph

enum class GradeZone(val id: Int, val labelResId: Int, val colorHex: String) {
    DESCENT(0, R.string.grade_zone_descent, "#0072CE"), // Using a mid-blue for all grouped descents
    FLAT(1, R.string.grade_zone_flat, "#388E3C"),
    MILD(2, R.string.grade_zone_mild, "#FBC02D"),
    MEDIUM(3, R.string.grade_zone_medium, "#F57C00"),
    HARD(4, R.string.grade_zone_hard, "#E65100"),
    VERY_HARD(5, R.string.grade_zone_very_hard, "#D32F2F"),
    WALL(6, R.string.grade_zone_wall, "#B71C1C"),
    EXTREME(7, R.string.grade_zone_extreme, "#000000");

    companion object {
        fun fromGrade(grade: Double): GradeZone {
            return when {
                grade < 0.0 -> DESCENT
                grade < 3.0 -> FLAT
                grade < 5.0 -> MILD
                grade < 8.0 -> MEDIUM
                grade < 10.0 -> HARD
                grade < 13.0 -> VERY_HARD
                grade <= 17.0 -> WALL
                else -> EXTREME
            }
        }
    }
}

class GradeZonesTracker {
    val distancePerZone = LongArray(8)
    val timePerZone = LongArray(8)
    var totalDistance = 0L
    var totalTime = 0L

    @Synchronized
    fun addSample(grade: Double, speed: Double, deltaTimeSec: Long = 1) {
        if (speed < 1.0) return // Si va a menos de 3.6 km/h, no sumamos para evitar ruido en paradas

        val zone = GradeZone.fromGrade(grade)
        val dist = (speed * deltaTimeSec).toLong()
        
        distancePerZone[zone.id] += dist
        timePerZone[zone.id] += deltaTimeSec
        totalDistance += dist
        totalTime += deltaTimeSec
    }

    @Synchronized
    fun getPercentages(byTime: Boolean): DoubleArray {
        val percentages = DoubleArray(8)
        val total = if (byTime) totalTime else totalDistance
        if (total == 0L) return percentages

        val sourceArray = if (byTime) timePerZone else distancePerZone
        for (i in 0 until 8) {
            percentages[i] = (sourceArray[i].toDouble() / total.toDouble()) * 100.0
        }
        return percentages
    }
}
