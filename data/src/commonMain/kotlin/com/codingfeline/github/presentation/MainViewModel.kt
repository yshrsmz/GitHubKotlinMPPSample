package com.codingfeline.github.presentation

import co.touchlab.stately.ensureNeverFrozen
import com.codingfeline.arch.MainThreadPubSub
import com.codingfeline.arch.Pub
import com.codingfeline.arch.Sub
import com.codingfeline.github.data.Repository
import com.codingfeline.github.data.User
import com.codingfeline.github.domain.FetchViewer
import com.codingfeline.github.domain.ObserveViewer
import com.codingfeline.github.domain.ObserveViewerRepositories
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

sealed class MainResult : Result {
    data class UserLoaded(val user: User?) : MainResult()
    data class RepositoriesLoaded(val repositories: List<Repository>) : MainResult()
    data class Error(val cause: Throwable) : MainResult()
}

sealed class MainState : State {

    object Loading : MainState()

    data class Data(
        val user: User?,
        val repositories: List<Repository>
    ) : MainState()

    data class Error(
        val message: String
    ) : MainState()
}

sealed class MainEffect : Effect {
    data class Toast(val message: String) : MainEffect()
}

typealias CallbackA = (item: MainResult) -> Unit

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class MainViewModel(
    bgContext: CoroutineContext,
    val fetchViewer: FetchViewer,
    val observeViewer: ObserveViewer,
    val observeViewerRepositories: ObserveViewerRepositories
) : MviViewModel2<MainResult, MainState, MainEffect>({ MainState.Loading }, bgContext) {

    private val userQueryPub: Pub<MainResult>

    private val repositoryQueryPub: Pub<MainResult>

    private val mainPub = MainThreadPubSub<MainResult>()

    init {
        ensureNeverFrozen()

        userQueryPub = observeViewer { MainResult.UserLoaded(it) }

        repositoryQueryPub = observeViewerRepositories { MainResult.RepositoriesLoaded(it) }

        userQueryPub.addSub(mainPub)
        repositoryQueryPub.addSub(mainPub)

        mainPub.addSub(object : Sub<MainResult> {
            override fun onNext(next: MainResult) {
                println("onNext: $next")

                launch {
                    if (next is MainResult.UserLoaded) {
                        sendEffect(MainEffect.Toast("user loaded"))
                    }
                    sendResult(next)
                }
            }

            override fun onError(t: Throwable) {
                println("onError: $t")
                launch {
                    sendResult(MainResult.Error(t))
                }
            }
        })

        launch {
            println("fetchViewer")
            fetchViewer()
        }
    }

    fun init() {
        println("init")
    }

    override fun reduce(result: MainResult, previousState: MainState): MainState {
        return when (result) {
            is MainResult.UserLoaded -> {
                if (previousState is MainState.Data) {
                    when {
                        result.user?.id == previousState.user?.id -> {
                            previousState.copy(user = result.user)
                        }
                        result.user?.id == previousState.repositories.firstOrNull()?.ownerId -> {
                            previousState.copy(user = result.user)
                        }
                        else -> {
                            previousState.copy(user = result.user, repositories = emptyList())
                        }
                    }
                } else {
                    MainState.Data(user = result.user, repositories = emptyList())
                }
            }
            is MainResult.RepositoriesLoaded -> {
                when (previousState) {
                    is MainState.Data -> {
                        previousState.copy(repositories = result.repositories)
                    }
                    is MainState.Loading -> {
                        MainState.Data(user = null, repositories = result.repositories)
                    }
                    else -> previousState
                }
            }
            is MainResult.Error -> {
                MainState.Error(result.cause.message ?: "Something happend")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        userQueryPub.dispose()
        repositoryQueryPub.dispose()
    }
}
