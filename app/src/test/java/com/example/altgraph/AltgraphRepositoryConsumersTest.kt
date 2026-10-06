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
        // Se quita el id previo del kind antes de dar de alta el nuevo (no queda ninguno vivo huérfano)
        assertEquals(listOf("c2"), removed)

        registry.removeAll()
        assertEquals(listOf("c2", "c1", "c3"), removed)
    }

    @Test
    fun doubleReAddKeepsOneLiveConsumer() {
        var next = 0
        val active = mutableListOf<String>()
        val removed = mutableListOf<String>()
        val registry = ConsumerRegistry(
            add = { _ -> "c${++next}".also { active.add(it) } },
            remove = { id -> removed.add(id); active.remove(id) }
        )

        registry.register(listOf("nav", "loc"))
        registry.reAdd("loc")
        registry.reAdd("loc")
        assertEquals(listOf("c1", "c4"), active)

        registry.removeAll()
        assertEquals(emptyList<String>(), active)
        assertEquals(listOf("c2", "c3", "c1", "c4"), removed)
    }

    @Test
    fun removeAllContinuesWhenOneRemoveThrows() {
        var next = 0
        val added = mutableListOf<String>()
        val removed = mutableListOf<String>()
        val registry = ConsumerRegistry(
            add = { _ -> "c${++next}".also { added.add(it) } },
            remove = { id -> removed.add(id); if (id == "c1") throw IllegalStateException("dead binder") }
        )

        registry.register(listOf("nav", "loc"))
        registry.removeAll()
        // Un remove que falla no impide quitar el resto
        assertEquals(listOf("c1", "c2"), removed)

        // El registro quedó vacío: un nuevo register da de alta de nuevo
        registry.register(listOf("nav"))
        assertEquals(listOf("c1", "c2", "c3"), added)
    }
}
