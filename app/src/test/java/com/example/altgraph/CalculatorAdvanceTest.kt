package com.example.altgraph

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorAdvanceTest {

    private fun field(c: AltimetriaStrategyCalculator, name: String): Any? {
        val f = AltimetriaStrategyCalculator::class.java.getDeclaredField(name)
        f.isAccessible = true
        return f.get(c)
    }

    private fun num(c: AltimetriaStrategyCalculator, name: String): Double =
        (field(c, name) as Number).toDouble()

    @Test
    fun advanceFalseDoesNotMutateState() {
        val c = AltimetriaStrategyCalculator()
        c.currentSpeed = 10.0
        c.calculateStrategy() // advance por defecto

        val dist = num(c, "liveDistanceAccumulated")
        val look = num(c, "smartLookahead")
        val last = num(c, "lastIntegrationMs")
        val apm = ClimbStateManager.currentApm.value.toDouble()

        repeat(3) {
            Thread.sleep(50)
            c.calculateStrategy(advance = false)
        }

        assertEquals(dist, num(c, "liveDistanceAccumulated"), 0.0)
        assertEquals(look, num(c, "smartLookahead"), 0.0)
        assertEquals(last, num(c, "lastIntegrationMs"), 0.0)
        assertEquals(apm, ClimbStateManager.currentApm.value.toDouble(), 0.0)
    }

    @Test
    fun advanceFalseReturnsIdenticalNonTrivialStrategyRepeatedly() {
        val c = AltimetriaStrategyCalculator()
        c.currentSpeed = 10.0
        c.calculateStrategy() // avanza una vez: liveDistanceAccumulated = 10 m

        val first = c.calculateStrategy(advance = false)
        Thread.sleep(50)
        val second = c.calculateStrategy(advance = false)

        assertEquals(first, second)
        // No trivial: perfil libre con ventana y sub-bloques reales, y el ciclista dentro de la ventana
        assertTrue(first.subBlocks.isNotEmpty())
        assertTrue(first.profileElevations.size == first.subBlocks.size + 1)
        assertTrue(first.riderProgress > 0f)
    }
}
