package com.codingfeline.githubui

import com.codingfeline.githubdata.Repository
import com.codingfeline.githubdata.User
import com.codingfeline.githubdata.checkIfFrozen
import com.codingfeline.githubdata.printCurrentThread
import com.codingfeline.githubdomain.FetchViewer
import com.codingfeline.githubdomain.ObserveViewer
import com.codingfeline.githubdomain.ObserveViewerRepositories
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal


sealed class ViewerResult : Result {
    data class UserLoaded(val user: User) : ViewerResult()
    data class RepositoriesLoaded(val repositories: List<Repository>) : ViewerResult()
}

sealed class ViewerState : State {
    object Loading : ViewerState()
    data class Data(
        val user: User,
        val repositories: List<Repository>
    ) : ViewerState()

    data class Error(val message: String) : ViewerState()
}

sealed class ViewerEffect : Effect {

}

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class ViewerViewModel(
    bgContext: CoroutineContext,
    @ThreadLocal val fetchViewer: FetchViewer,
    @ThreadLocal val observeViewer: ObserveViewer,
    @ThreadLocal val observeViewerRepositories: ObserveViewerRepositories
) : MviViewModel2<ViewerResult, ViewerState, ViewerEffect>({ ViewerState.Loading }, bgContext) {

    fun init() {
        println("ViewerViewModel#init")
        printCurrentThread()
        checkIfFrozen("observeViewer", observeViewer)
        checkIfFrozen("observeViewerRepositories", observeViewerRepositories)
        checkIfFrozen("observeViewerRepositories.repository", observeViewerRepositories.gitHubRepository)

//        observeViewerRepositories { repos ->
//            println("repos: $repos")
//            offerResult(ViewerResult.RepositoriesLoaded(repositories = repos))
////            launch { sendResult(ViewerResult.RepositoriesLoaded(repositories = repos)) }
//        }

        println("ViewerViewModel#init2")

        observeViewer { viewer ->
            offerResult(ViewerResult.UserLoaded(user = viewer))
            //            launch { sendResult(ViewerResult.UserLoaded(user = viewer)) }
        }

        println("ViewerViewModel#init3")

        launch {
            try {
                fetchViewer()
            } catch (e: Exception) {
                println("e: $e")
            }
        }

        println("ViewerViewModel#init4")
    }

    @ExperimentalCoroutinesApi
    override fun reduce(result: ViewerResult, previousState: ViewerState): ViewerState {
        return when (result) {
            is ViewerResult.UserLoaded -> {
                if (previousState is ViewerState.Data) {
                    if (previousState.user.login == result.user.login) {
                        previousState.copy(user = result.user)
                    } else {
                        previousState.copy(user = result.user, repositories = emptyList())
                    }
                } else {
                    ViewerState.Data(user = result.user, repositories = emptyList())
                }
            }
            is ViewerResult.RepositoriesLoaded -> {
                if (previousState is ViewerState.Data) {
                    previousState.copy(repositories = result.repositories)
                } else {
                    previousState
                }
            }
        }
    }
}
