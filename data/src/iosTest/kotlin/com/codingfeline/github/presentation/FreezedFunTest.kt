package com.codingfeline.github.presentation

import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.concurrency.value
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_t
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen
import kotlin.test.Ignore
import kotlin.test.Test

class FreezedFunTest {

    data class JobWrapper<B>(val backJob: () -> B, val mainJobLocal: ThreadLocalRef<(B) -> Unit>)
    data class JobWrapper2<B>(val backJob: () -> B, val mainJob: (B) -> Unit)

    class Obj(var value: String)

    @Ignore
    @Test
    fun test() {
        val ref = ThreadLocalRef<(Obj) -> Unit>()
        ref.set { obj -> println("obj: $obj") }
        val jw = JobWrapper({ Obj("param") }, ref).freeze()

        println("JobWrapper: ${jw.isFrozen}")
        println("JobWrapper.backJob: ${jw.backJob.isFrozen}")
        println("JobWrapper.mainJobLocal: ${jw.mainJobLocal.isFrozen}")
        println("JobWrapper.mainJobLocal.value: ${jw.mainJobLocal.value.isFrozen}")

        val jw2 = JobWrapper2({ Obj("param") }, { obj -> println("obj: $obj") }).freeze()

        println("JobWrapper2: ${jw2.isFrozen}")
        println("JobWrapper2.backJob: ${jw2.backJob.isFrozen}")
        println("JobWrapper2.mainJobLocal: ${jw2.mainJob.isFrozen}")
        println("JobWrapper2.mainJobLocal.value: ${jw2.mainJob.isFrozen}")
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

