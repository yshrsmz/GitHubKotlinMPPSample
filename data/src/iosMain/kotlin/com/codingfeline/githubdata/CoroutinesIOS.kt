package com.codingfeline.githubdata

import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSThread
import kotlin.native.concurrent.isFrozen

internal actual val ApplicationDispatcher: CoroutineDispatcher = MainLoopDispatcher

actual fun checkIfFrozen(name: String, instance: Any?) {
    println("$name is${if (instance.isFrozen) "" else " not"} frozen")
}

actual fun printCurrentThread() {
    println(
        "current thread: ${NSThread.currentThread()}, ${NSOperationQueue.currentQueue?.underlyingQueue()}"
    )
}
