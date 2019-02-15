package com.codingfeline.githubdata

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.atomic.AtomicReference

internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Main

actual fun checkIfFrozen(name: String, instance: Any?) {
    println("$name is not frozen on Android")
}

actual fun printCurrentThread() {
    println("current thread: ${Thread.currentThread()}, ${Thread.currentThread().name}")
}

actual fun Any.ensureNeverFrozen() {
    // no-op
}

actual typealias AtomicReference<V> = AtomicReference<V>

actual fun <T> T.freeze(): T = this
