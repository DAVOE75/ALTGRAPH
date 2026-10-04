package com.example.altgraph

import android.os.DeadObjectException
import org.junit.Assert.assertEquals
import org.junit.Test

class RenderGuardTest {
    @Test
    fun deadObjectCallsOnDeadOnce() {
        var dead = 0
        safeUpdate(onDead = { dead++ }) { throw DeadObjectException() }
        assertEquals(1, dead)
    }

    @Test
    fun otherExceptionIsSwallowedWithoutOnDead() {
        var dead = 0
        safeUpdate(onDead = { dead++ }) { throw IllegalStateException("x") }
        assertEquals(0, dead)
    }

    @Test
    fun runsBlockWhenNoException() {
        var ran = 0
        safeUpdate(onDead = { error("no") }) { ran++ }
        assertEquals(1, ran)
    }

    @Test
    fun cancellationIsRethrownWithoutOnDead() {
        var dead = 0
        try {
            safeUpdate(onDead = { dead++ }) { throw kotlinx.coroutines.CancellationException("c") }
            org.junit.Assert.fail("debía propagarse")
        } catch (e: kotlinx.coroutines.CancellationException) {
        }
        assertEquals(0, dead)
    }
}
