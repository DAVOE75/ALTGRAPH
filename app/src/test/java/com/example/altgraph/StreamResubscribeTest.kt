package com.example.altgraph

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StreamResubscribeTest {

    @Test
    fun backoffDoublesAndCaps() {
        val chain = generateSequence(nextBackoffMs(null)) { nextBackoffMs(it) }.take(7).toList()
        assertEquals(listOf(2000L, 4000L, 8000L, 16000L, 32000L, 60000L, 60000L), chain)
    }

    @Test
    fun noResubscribeAfterGenerationChange() {
        assertFalse(shouldResubscribe(3, 4, true))
        assertFalse(shouldResubscribe(3, 3, false))
        assertTrue(shouldResubscribe(3, 3, true))
    }

    @Test
    fun longLivedStreamResetsBackoff() = assertEquals(2000L, backoffAfterTerminal(32000L, 60_000L))

    @Test
    fun shortLivedStreamKeepsGrowing() = assertEquals(8000L, backoffAfterTerminal(4000L, 5_000L))

    @Test
    fun firstTerminalStartsAtTwoSeconds() = assertEquals(2000L, backoffAfterTerminal(null, null))
}
