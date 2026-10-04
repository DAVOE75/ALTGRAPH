package com.example.altgraph

import org.junit.Assert.assertEquals
import org.junit.Test

class FreeRideDistanceTest {

    private fun liveDistance(c: AltimetriaStrategyCalculator): Double {
        val f = AltimetriaStrategyCalculator::class.java.getDeclaredField("liveDistanceAccumulated")
        f.isAccessible = true
        return f.getDouble(c)
    }

    @Test
    fun severalCallsInTheSameSecondDoNotMultiplyDistance() {
        val c = AltimetriaStrategyCalculator()
        c.currentSpeed = 10.0

        c.calculateStrategy() // primera llamada: 1 s, como antes
        val afterFirst = liveDistance(c)
        assertEquals(10.0, afterFirst, 1e-6)

        // Otra vista / stream del mismo campo llama enseguida: casi no hay tiempo transcurrido
        c.calculateStrategy()
        c.calculateStrategy()
        assertEquals(afterFirst, liveDistance(c), 1.0)
    }
}
