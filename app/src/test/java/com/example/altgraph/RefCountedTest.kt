package com.example.altgraph

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class RefCountedTest {

    @Test
    fun releasedOnlyWhenLastHolderLeaves() {
        val released = mutableListOf<String>()
        var created = 0
        val ref = RefCounted<String> { released.add(it) }

        val a = Any()
        val b = Any()
        val first = ref.acquire(a) { created++; "conn" }
        // El host abre la vista B antes de cancelar A
        val second = ref.acquire(b) { created++; "other" }
        assertSame(first, second)
        assertEquals(1, created)

        ref.release(a)
        assertEquals(emptyList<String>(), released) // B sigue usándolo
        ref.release(a) // cancel repetido: no descuadra
        assertEquals(emptyList<String>(), released)
        ref.release(b)
        assertEquals(listOf("conn"), released)

        // Una vista nueva crea otro recurso
        ref.acquire(Any()) { created++; "conn2" }
        assertEquals(2, created)
    }

    @Test
    fun failedCreateDoesNotKeepAHolder() {
        val released = mutableListOf<String>()
        val ref = RefCounted<String> { released.add(it) }
        val failed = Any()
        try {
            ref.acquire(failed) { throw IllegalStateException("boom") }
        } catch (e: IllegalStateException) {}

        val ok = Any()
        ref.acquire(ok) { "conn" }
        ref.release(ok)
        assertEquals(listOf("conn"), released)
    }
}
