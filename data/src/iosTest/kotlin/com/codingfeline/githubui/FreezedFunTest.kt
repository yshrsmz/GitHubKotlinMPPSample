package com.codingfeline.github.presentation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_t
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.freeze
import kotlin.test.Test

class FreezedFunTest {

    class FreezeTest : CoroutineScope {
        override val coroutineContext: CoroutineContext = QueueDispatcher() + Job()

        val func = { s: String ->
            launch {
                delay(10)
                println("delayed: $s")
            }
        }.freeze()

        val func2 = { s: String ->
            launchCoroutine(s)
        }.freeze()

        fun launchCoroutine(s: String) {
            launch {
                delay(10)
                println("delayed: $s")
            }
        }

        fun test() = func("test")

        fun test2() = func2("test2")
    }

    @Test
    fun test() = runBlocking {
        FreezeTest().test2()
        Unit
    }
}

internal class QueueDispatcher(
    private val dispatchQueue: dispatch_queue_t = dispatch_get_main_queue()
) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }
}
