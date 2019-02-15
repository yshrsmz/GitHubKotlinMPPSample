package com.codingfeline.githubdata

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Main

actual fun checkIfFrozen(name: String, instance: Any?) {
    println("$name is not frozen on Android")
}

actual fun printCurrentThread() {
    println("current thread: ${Thread.currentThread()}, ${Thread.currentThread().name}")
}
