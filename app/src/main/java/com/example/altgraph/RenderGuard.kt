package com.example.altgraph

import android.os.DeadObjectException
import android.util.Log
import kotlinx.coroutines.Job
import java.util.concurrent.atomic.AtomicBoolean
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

/**
 * Cancelación de un startStream/startView que se instala ANTES de preparar nada.
 * KarooExtension llama a cancel() desde otro hilo Binder y Emitter.cancel() solo invoca el callback
 * presente en ese momento (no recuerda el cancel): si llega durante la preparación queda apuntado
 * y attach() para el bucle y libera en cuanto existe. release debe ser idempotente.
 */
class CancelLatch {
    private val cancelled = AtomicBoolean(false)
    @Volatile private var job: Job? = null
    @Volatile private var release: (() -> Unit)? = null

    fun cancel() {
        cancelled.set(true)
        job?.cancel()
        release?.invoke()
    }

    fun attach(job: Job, release: () -> Unit) {
        // Orden: publicar job/release antes de leer cancelled (cancel() hace lo inverso), así al menos uno libera
        this.release = release
        this.job = job
        if (cancelled.get()) {
            job.cancel()
            release()
        }
    }
}
