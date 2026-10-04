package com.example.altgraph

/**
 * Recurso compartido por las vistas abiertas de un mismo DataType (conexión al Karoo,
 * BroadcastReceiver): se crea con la primera vista y se libera al cerrarse la última.
 *
 * Se cuenta por token (la vista) en vez de con un entero, para que un cancel repetido
 * o una vista nueva abierta antes de cerrar la anterior no descuadren la cuenta.
 */
class RefCounted<T : Any>(private val onRelease: (T) -> Unit) {
    private var value: T? = null
    private val holders = mutableSetOf<Any>()

    @Synchronized
    fun acquire(token: Any, create: () -> T): T {
        holders.add(token)
        return value ?: create().also { value = it }
    }

    @Synchronized
    fun release(token: Any) {
        if (holders.remove(token) && holders.isEmpty()) {
            value?.let(onRelease)
            value = null
        }
    }
}
