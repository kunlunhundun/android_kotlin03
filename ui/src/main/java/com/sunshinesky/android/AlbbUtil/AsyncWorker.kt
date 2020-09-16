
package com.sunshinesky.android.AlbbUtil

import android.os.Handler
import java9.util.concurrent.CompletableFuture
import java9.util.concurrent.CompletionStage
import java.util.concurrent.Executor

/**
 * Helper class for running asynchronous tasks and ensuring they are completed on the main thread.
 */

class AsyncWorker(private val executor: Executor, private val handler: Handler) {

    fun runAsync(run: () -> Unit): CompletionStage<Void> {
        val future = CompletableFuture<Void>()
        executor.execute {
            try {
                run()
                handler.post { future.complete(null) }
            } catch (t: Throwable) {
                handler.post { future.completeExceptionally(t) }
            }
        }
        return future
    }

    fun <T> supplyAsync(get: () -> T?): CompletionStage<T> {
        val future = CompletableFuture<T>()
        executor.execute {
            try {
                val result = get()
                handler.post { future.complete(result) }
            } catch (t: Throwable) {
                handler.post { future.completeExceptionally(t) }
            }
        }
        return future
    }
}