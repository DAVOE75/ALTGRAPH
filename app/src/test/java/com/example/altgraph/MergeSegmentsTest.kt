package com.example.altgraph

import com.example.altgraph.MapOverlayManager.GradeSegment
import org.junit.Assert.assertEquals
import org.junit.Test

class MergeSegmentsTest {

    private fun merge(vararg s: GradeSegment) = MapOverlayManager.mergeShortSegments(s.toList())

    @Test
    fun longFlatDoesNotSwallowFollowingClimb() {
        // Llano de 2 km y luego una subida en tramos cortos de colores distintos
        val climb = (0 until 10).map { i -> GradeSegment(2000.0 + i * 100, 2100.0 + i * 100, 8.0) }
        val result = merge(GradeSegment(0.0, 2000.0, 1.0), *climb.toTypedArray())
        assertEquals(listOf(GradeSegment(0.0, 2000.0, 1.0)), result.take(1))
        assertEquals(2000.0, result[1].startDist, 0.0)
        assertEquals(3000.0, result.last().endDist, 0.0)
        result.drop(1).forEach { assertEquals(8.0, it.grade, 1e-9) }
    }

    @Test
    fun shortRunsAccumulateWithWeightedGrade() {
        val result = merge(
            GradeSegment(0.0, 100.0, 2.0),
            GradeSegment(100.0, 250.0, 6.0),
            GradeSegment(250.0, 300.0, 8.0),
            GradeSegment(300.0, 1000.0, 4.0)
        )
        assertEquals(2, result.size)
        assertEquals(0.0, result[0].startDist, 0.0)
        assertEquals(300.0, result[0].endDist, 0.0)
        assertEquals((2.0 * 100 + 6.0 * 150 + 8.0 * 50) / 300, result[0].grade, 1e-9)
        assertEquals(GradeSegment(300.0, 1000.0, 4.0), result[1])
    }

    @Test
    fun shortSteepClimbBeforeLongDescentIsKept() {
        // Una rampa corta no se come la bajada larga que la sigue (ni al revés)
        val result = merge(GradeSegment(0.0, 200.0, 15.0), GradeSegment(200.0, 5200.0, -3.0))
        assertEquals(listOf(GradeSegment(0.0, 200.0, 15.0), GradeSegment(200.0, 5200.0, -3.0)), result)
    }

    @Test
    fun shortClimbAfterLongFlatIsKept() {
        val result = merge(GradeSegment(0.0, 5000.0, 1.0), GradeSegment(5000.0, 5200.0, 12.0))
        assertEquals(listOf(GradeSegment(0.0, 5000.0, 1.0), GradeSegment(5000.0, 5200.0, 12.0)), result)
    }

    @Test
    fun nonContiguousSegmentsAreNotMerged() {
        // Dos puertos separados (modo "climbs"): el hueco entre ellos no se pinta
        val result = merge(GradeSegment(0.0, 100.0, 6.0), GradeSegment(5000.0, 5100.0, 7.0))
        assertEquals(2, result.size)
    }

    @Test
    fun emptyInput() {
        assertEquals(emptyList<GradeSegment>(), merge())
    }
}
