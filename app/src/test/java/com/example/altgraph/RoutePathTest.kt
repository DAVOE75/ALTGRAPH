package com.example.altgraph

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutePathTest {

    // Recta norte-sur de ~3 km (0.009 grados de latitud ~ 1 km)
    private val line = listOf(40.000 to -3.0, 40.009 to -3.0, 40.018 to -3.0, 40.027 to -3.0)
    private val encoded = PolylineCodec.encode(line, 1e5)

    @Test
    fun subPathInterpolatesEndpointsAndKeepsInnerPoints() {
        val path = RoutePath.fromPolyline(encoded, 3000.0)!!
        val sub = path.subPath(500.0, 2500.0)
        assertEquals(4, sub.size) // inicio interpolado, 2 vértices, fin interpolado
        assertEquals(40.0045, sub.first().first, 1e-4)
        assertEquals(40.009, sub[1].first, 1e-5)
        assertEquals(40.018, sub[2].first, 1e-5)
        assertEquals(40.0225, sub.last().first, 1e-4)
    }

    @Test
    fun subPathWithinOneSegmentHasTwoPoints() {
        val path = RoutePath.fromPolyline(encoded, 3000.0)!!
        val sub = path.subPath(100.0, 200.0)
        assertEquals(2, sub.size)
        assertTrue(sub[0].first < sub[1].first)
    }

    @Test
    fun emptyOrDegenerateInput() {
        assertNull(RoutePath.fromPolyline("", 1000.0))
        val path = RoutePath.fromPolyline(encoded, null)!!
        assertTrue(path.subPath(200.0, 100.0).size < 2)
    }
}
