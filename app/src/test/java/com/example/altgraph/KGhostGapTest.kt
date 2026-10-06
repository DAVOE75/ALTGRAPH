package com.example.altgraph

import io.hammerhead.karooext.models.DataPoint
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.StreamState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class KGhostGapTest {

    private val wide = 1000f
    private val m: (String) -> Float = { it.length * 10f }

    @Test
    fun streamingBecomesPart() {
        val s = StreamState.Streaming(
            DataPoint("x", mapOf(DataType.Field.SINGLE to 12.0, "estimated" to 1.0))
        )
        assertEquals(GapPart(12.0, true), kghostPart(s))
    }

    @Test
    fun searchingClearsPart() {
        assertNull(kghostPart(StreamState.Searching))
        assertNull(kghostPart(StreamState.Idle))
    }

    @Test
    fun noneIsNull() {
        assertNull(combineGhostGap(null, null))
    }

    @Test
    fun onlyDistanceShowsDistance() {
        val g = combineGhostGap(null, GapPart(40.0, false))
        assertEquals(GhostGap(null, 40.0, false), g)
        assertEquals("+40 m", formatGhostGap(g!!, wide, m))
    }

    @Test
    fun estimatedPropagates() {
        assertTrue(combineGhostGap(GapPart(5.0, false), GapPart(20.0, true))!!.estimated)
    }

    @Test
    fun formatsBoth() {
        assertEquals("+12 s · +40 m", formatGhostGap(GhostGap(12.0, 40.0, false), wide, m))
    }

    @Test
    fun formatsMinutesAndKm() {
        assertEquals("−1:05 · −1.3 km", formatGhostGap(GhostGap(-65.0, -1250.0, false), wide, m))
    }

    @Test
    fun zeroHasNoSign() {
        assertEquals("0 s · 0 m", formatGhostGap(GhostGap(0.0, 0.0, false), wide, m))
    }

    @Test
    fun fallsBackToTimeWhenTooWide() {
        assertEquals("+12 s", formatGhostGap(GhostGap(12.0, 40.0, false), 60f, m))
    }

    @Test
    fun colours() {
        assertEquals(0xFF4CAF50.toInt(), ghostGapColor(GhostGap(5.0, null, false)))
        assertEquals(0xFFF44336.toInt(), ghostGapColor(GhostGap(-5.0, null, false)))
        assertEquals(0xFFFFFFFF.toInt(), ghostGapColor(GhostGap(0.0, null, false)))
        assertEquals(0xFFFFC107.toInt(), ghostGapColor(GhostGap(-5.0, null, true)))
    }
}
