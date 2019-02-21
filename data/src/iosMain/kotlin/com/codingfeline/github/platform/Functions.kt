package com.codingfeline.github.platform

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.staticCFunction
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSThread
import platform.darwin.dispatch_async_f
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.DetachedObjectGraph
import kotlin.native.concurrent.attach
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

actual fun checkIfFrozen(name: String, instance: Any?) {
    println("$name is${if (instance.isFrozen) "" else " not"} frozen")
}

actual fun printCurrentThread() {
    println(
        "current thread: ${NSThread.currentThread()}, ${NSOperationQueue.currentQueue?.underlyingQueue()}"
    )
}

internal actual val isMainThread get() = NSThread.isMainThread

internal actual fun <B> backToFront(b: () -> B, job: (B) -> Unit) {
    dispatch_async_f(
        dispatch_get_main_queue(),
        DetachedObjectGraph { JobAndThing(job.freeze(), b()) }.asCPointer(),
        staticCFunction { p: COpaquePointer? ->
            initRuntimeIfNeeded()
            @Suppress("UNCHECKED_CAST") val result = DetachedObjectGraph<Any>(p).attach() as JobAndThing<B>
            result.job(result.thing)
        })
}

internal data class JobAndThing<B>(val job: (B) -> Unit, val thing: B)
