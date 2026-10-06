package com.example.altgraph

import org.junit.Assert.assertEquals
import org.junit.Test

class AltgraphRepositoryConsumersTest {

    @Test
    fun registersOnceAndRemoveAllClearsThem() {
        var next = 0
        val active = mutableListOf<String>()
        val removed = mutableListOf<String>()
        val registry = ConsumerRegistry(
            add = { _ -> "c${++next}".also { active.add(it) } },
            remove = { id -> removed.add(id); active.remove(id) }
        )

        registry.register(listOf("nav", "loc"))
        // Una reconexión no vuelve a dar de alta: el SDK ya los re-registra
        registry.register(listOf("nav", "loc"))
        assertEquals(listOf("c1", "c2"), active)
        assertEquals(emptyList<String>(), removed)

        registry.removeAll()
        assertEquals(listOf("c1", "c2"), removed)
        assertEquals(emptyList<String>(), active)
    }

    @Test
    fun reAddReplacesIdAndRemoveAllUsesNewOne() {
        var next = 0
        val removed = mutableListOf<String>()
        val registry = ConsumerRegistry(
            add = { _ -> "c${++next}" },
            remove = { id -> removed.add(id) }
        )

        registry.register(listOf("nav", "loc"))
        registry.reAdd("loc")
        // El SDK ya quitó el consumer terminado: reAdd no vuelve a quitarlo
        assertEquals(emptyList<String>(), removed)

        registry.removeAll()
        assertEquals(listOf("c1", "c3"), removed)
    }
}
