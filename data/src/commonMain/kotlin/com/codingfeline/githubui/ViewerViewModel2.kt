package com.codingfeline.githubui

import com.codingfeline.githubdata.Repository
import com.codingfeline.githubdata.User
import com.codingfeline.githubdomain.FetchViewer
import com.codingfeline.githubdomain.ObserveViewer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


sealed class ViewerResult : Result {
    data class UserLoaded(val user: User) : ViewerResult()

    data class RepositoriesLoaded(
        val login: String,
        val repositories: List<Repository>
    ) : ViewerResult()
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
class ViewerViewModel2(
    bgContext: CoroutineContext,
    private val fetchViewer: FetchViewer,
    private val observeViewer: ObserveViewer
) : MviViewModel2<ViewerResult, ViewerState, ViewerEffect>({ ViewerState.Loading }, bgContext) {

    init {
        observeViewer { viewer ->
            launch { sendResult(ViewerResult.UserLoaded(user = viewer)) }
        }

        launch {
            fetchViewer()
        }
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
                    if (previousState.user.login == result.login) {
                        previousState.copy(repositories = result.repositories)
                    } else {
                        previousState
                    }
                } else {
                    previousState
                }
            }
        }
    }
}
