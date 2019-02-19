package com.codingfeline.github.data

import com.codingfeline.github.domain.FetchViewer
import com.codingfeline.github.domain.ObserveViewer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal

abstract class RepositoryIos(
    @ThreadLocal private val bgContext: CoroutineContext
) : CoroutineScope {
    @ThreadLocal
    private val job = SupervisorJob()

    @ThreadLocal
    override val coroutineContext: CoroutineContext = (bgContext + job)
}

@ObsoleteCoroutinesApi
class GitHubRepositoryIos(
    private val repository: GitHubRepository,
    private val fetchViewerUsecase: FetchViewer,
    private val observeViewer: ObserveViewer,
    bgContext: CoroutineContext
) : RepositoryIos(bgContext) {

    private val channel = Channel<Int>(Channel.UNLIMITED)

//    init {
//        checkIfFrozen("coroutineContext", coroutineContext)
//        observeViewer { viewer ->
//            println("observeViewer callback: $viewer")
//            printCurrentThread()
//            checkIfFrozen("coroutineContext", coroutineContext)
//            checkIfFrozen("bgContext", bgContext)
//
//        }
//
//        launch {
//            channel.consumeEach {
//                checkIfFrozen("coroutineContext", coroutineContext)
//                println("channel received: $it")
//            }
//        }
//    }

    fun fetchViewer() {
        println("fetch viewer")
        launch {
            println("fetch viewer2")
            fetchViewerUsecase()
            channel.send(10)
        }
    }

}
