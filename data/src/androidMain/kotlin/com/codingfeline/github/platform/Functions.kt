package com.codingfeline.github.platform

import android.os.Handler
import android.os.Looper

actual fun checkIfFrozen(name: String, instance: Any?) {
    println("$name is not frozen on Android")
}

actual fun printCurrentThread() {
    println("current thread: ${Thread.currentThread()}, ${Thread.currentThread().name}")
}

internal actual val isMainThread: Boolean get() = Looper.getMainLooper() === Looper.myLooper()

private val btfHandler = Handler(Looper.getMainLooper())

internal actual fun <B> backToFront(b: () -> B, job: (B) -> Unit) {
    btfHandler.post { job(b()) }
}
