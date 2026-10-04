package com.example.altgraph

import kotlinx.coroutines.Job
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CancelLatchTest {

    @Test
    fun cancelBeforeAttachCancelsJobAndReleases() {
        val latch = CancelLatch()
        var released = 0
        latch.cancel() // stopView llega mientras el campo aún se prepara
        val job = Job()
        latch.attach(job) { released++ }
        assertTrue(job.isCancelled)
        assertEquals(1, released)
    }

    @Test
    fun cancelAfterAttachCancelsJobAndReleases() {
        val latch = CancelLatch()
        var released = 0
        val job = Job()
        latch.attach(job) { released++ }
        assertEquals(0, released)
        latch.cancel()
        assertTrue(job.isCancelled)
        assertEquals(1, released)
    }
}
