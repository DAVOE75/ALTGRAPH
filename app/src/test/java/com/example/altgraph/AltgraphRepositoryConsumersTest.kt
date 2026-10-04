package com.example.altgraph

import org.junit.Assert.assertEquals
import org.junit.Test

class AltgraphRepositoryConsumersTest {

    @Test
    fun reconnectReplacesConsumers() {
        var next = 0
        val active = mutableListOf<String>()
        val removed = mutableListOf<String>()
        val registry = ConsumerRegistry(
            add = { _ -> "c${++next}".also { active.add(it) } },
            remove = { id -> removed.add(id); active.remove(id) }
        )

        registry.register(listOf("nav", "loc"))
        registry.register(listOf("nav", "loc"))

        assertEquals(listOf("c1", "c2"), removed)
        assertEquals(listOf("c3", "c4"), active)
    }
}
