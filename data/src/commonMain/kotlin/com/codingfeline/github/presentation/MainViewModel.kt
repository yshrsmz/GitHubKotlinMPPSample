package com.codingfeline.github.presentation

import co.touchlab.stately.ensureNeverFrozen
import com.codingfeline.arch.MainThreadPubSub
import com.codingfeline.arch.Sub
import com.codingfeline.github.data.GitHubRepository
import com.codingfeline.github.data.Repository
import com.codingfeline.github.data.User
import com.codingfeline.github.data.local.QueryPub
import com.codingfeline.github.domain.FetchViewer
import com.codingfeline.github.domain.ObserveViewer
import com.codingfeline.github.domain.ObserveViewerRepositories
import com.squareup.sqldelight.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

sealed class MainResult {
    data class UserLoaded(val user: User?) : MainResult()
    data class RepositoriesLoaded(val repositories: List<Repository>) : MainResult()
}

sealed class MainState : State {

    object Loading : MainState()

    data class Data(
        val user: User,
        val repositories: List<Repository>
    ) : MainState()

    data class Error(
        val message: String
    ) : MainState()
}

class MainViewModel(
    bgContext: CoroutineContext,
    val fetchViewer: FetchViewer,
    val observeViewer: ObserveViewer,
    val observeViewerRepositories: ObserveViewerRepositories,
    val gitHubRepository: GitHubRepository
) : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()

    private val userQueryPub: QueryPub<User, MainResult>

    private val repositoryQueryPub: QueryPub<Repository, MainResult>

    private val mainPub = MainThreadPubSub<MainResult>()

    override val coroutineContext: CoroutineContext = bgContext + job

    init {
        ensureNeverFrozen()

        userQueryPub = QueryPub(gitHubRepository.selectViewer()) { q: Query<User> ->
            println("userQueryPub#extractData")
            val result = q.executeAsOneOrNull()
            MainResult.UserLoaded(result)
        }

        repositoryQueryPub = QueryPub(gitHubRepository.selectRepositoriesForViewer()) { q: Query<Repository> ->
            println("repositoryQueryPub#extractData")
            val result = q.executeAsList()
            MainResult.RepositoriesLoaded(result)
        }

        userQueryPub.addSub(mainPub)
        repositoryQueryPub.addSub(mainPub)

        mainPub.addSub(object : Sub<MainResult> {
            override fun onNext(next: MainResult) {
                println("onNext: $next")

                launch {
                    delay(10)
                    print("onNext-launch")
                }
            }

            override fun onError(t: Throwable) {
                println("onError: $t")
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

    override fun onCleared() {
        super.onCleared()
        userQueryPub.dispose()
        repositoryQueryPub.dispose()
        job.cancel()
    }
}
