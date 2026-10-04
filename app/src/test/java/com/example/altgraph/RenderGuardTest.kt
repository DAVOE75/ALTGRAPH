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
}
