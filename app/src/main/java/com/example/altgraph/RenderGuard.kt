package com.example.altgraph

import android.os.DeadObjectException
import android.util.Log
import kotlin.coroutines.cancellation.CancellationException

/**
 * Ejecuta un frame de un campo: si el proceso de Karoo ha muerto (DeadObjectException)
 * avisa con onDead para cancelar la vista; cualquier otro fallo solo pierde este frame.
 */
inline fun safeUpdate(onDead: () -> Unit, block: () -> Unit) {
    try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: DeadObjectException) {
        onDead()
    } catch (e: Exception) {
        Log.w("RenderGuard", "Frame perdido", e)
    }
}
