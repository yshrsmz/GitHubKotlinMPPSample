package com.codingfeline.githubdata

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSThread
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_t
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

internal actual val ApplicationDispatcher: CoroutineDispatcher = NsQueueDispatcher()

internal class NsQueueDispatcher(
    private val dispatchQueue: dispatch_queue_t = dispatch_get_main_queue()
) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }
}

actual fun checkIfFrozen(name: String, instance: Any?) {
    println("$name is${if (instance.isFrozen) "" else " not"} frozen")
}

actual fun printCurrentThread() {
    println(
        "current thread: ${NSThread.currentThread()}, ${NSOperationQueue.currentQueue?.underlyingQueue()}"
    )
}

actual fun Any.ensureNeverFrozen() = ensureNeverFrozen()

//actual typealias AtomicReference<T> = AtomicReference<T>
actual class AtomicReference<V> actual constructor(initialValue: V) {
    private val atom = kotlin.native.concurrent.AtomicReference(initialValue)
    actual fun get(): V = atom.value

    actual fun set(value_: V) {
        atom.value = value_
    }

    /**
     * Compare current value with expected and set to new if they're the same. Note, 'compare' is checking
     * the actual object id, not 'equals'.
     */
    actual fun compareAndSet(expected: V, new: V): Boolean = atom.compareAndSet(expected, new)
}

actual fun <T> T.freeze() = freeze()
