package com.example.altgraph

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TickGateTest {
    @Test
    fun gateActivatesOnFirstAndDeactivatesOnLast() {
        val g = TickGate()
        val a = Any()
        val b = Any()
        assertTrue(g.add(a))
        assertFalse(g.add(b))
        assertFalse(g.remove(a))
        assertTrue(g.isActive)
        assertFalse(g.remove(a))
        assertTrue(g.remove(b))
        assertFalse(g.isActive)
    }
}
