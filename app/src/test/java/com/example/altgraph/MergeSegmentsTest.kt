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
            GradeSegment(100.0, 400.0, 6.0),
            GradeSegment(400.0, 1000.0, 4.0)
        )
        assertEquals(2, result.size)
        assertEquals(GradeSegment(0.0, 400.0, 5.0), result[0])
        assertEquals(GradeSegment(400.0, 1000.0, 4.0), result[1])
    }

    @Test
    fun shortLastSegmentJoinsPrevious() {
        val result = merge(GradeSegment(0.0, 500.0, 4.0), GradeSegment(500.0, 600.0, 10.0))
        assertEquals(listOf(GradeSegment(0.0, 600.0, 5.0)), result)
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
