package com.codingfeline.github.presentation

import co.touchlab.stately.concurrency.ThreadLocalRef
import co.touchlab.stately.ensureNeverFrozen
import co.touchlab.stately.freeze
import com.codingfeline.arch.reduxlike.Action
import com.codingfeline.arch.reduxlike.Effect
import com.codingfeline.arch.reduxlike.Intent
import com.codingfeline.arch.reduxlike.Processor
import com.codingfeline.arch.reduxlike.RViewModel
import com.codingfeline.arch.reduxlike.State
import com.codingfeline.github.data.GitHubRepository
import com.codingfeline.github.data.Repository
import com.codingfeline.github.data.User
import com.codingfeline.github.platform.backToFront
import com.squareup.sqldelight.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

sealed class MainIntent2 : Intent {
    object LoadViewer : MainIntent2()
}

sealed class MainAction2 : Action {
    object UserLoading : MainAction2()
    data class UserUpdated(val user: User) : MainAction2()

    object RepositoriesLoading : MainAction2()
    data class RepositoriesUpdated(val repositories: List<Repository>) : MainAction2()
}

sealed class MainState2 : State {
    abstract val user: User?
    abstract val repositories: List<Repository>?

    data class Loading(
        override val user: User?,
        override val repositories: List<Repository>?
    ) : MainState2()

    data class Content(
        override val user: User?,
        override val repositories: List<Repository>?
    ) : MainState2()

    data class Error(
        val message: String
    ) : MainState2() {
        override val user: User? = null
        override val repositories: List<Repository>? = null
    }

    companion object {
        fun initialState(): MainState2 = MainState2.Loading(null, null)
    }
}

sealed class MainEffect2 : Effect {

}

@ExperimentalCoroutinesApi
class MainViewModel2(
    bgContext: CoroutineContext,
    processor: MainProcessor
) : RViewModel<MainIntent2, MainAction2, MainState2, MainEffect2>(MainState2.initialState(), bgContext, processor) {


    init {
        ensureNeverFrozen()
    }

    override fun intentToAction(intent: MainIntent2, state: MainState2): MainAction2 {
        println("intentToAction: ${intent::class}")
        return when (intent) {
            is MainIntent2.LoadViewer -> MainAction2.UserLoading
        }
    }

    override fun reduce(previousState: MainState2, action: MainAction2): MainState2 {
        println("reduce: ${previousState::class}, ${action::class}")
        return when (action) {
            is MainAction2.UserLoading -> {
                MainState2.initialState()
            }
            is MainAction2.UserUpdated -> {
                when (previousState) {
                    is MainState2.Loading -> {
                        if (previousState.repositories != null) {
                            println("previousState.repositories is not null")
                            MainState2.Content(action.user, previousState.repositories)
                        } else {
                            println("previousState.repositories is null")
                            previousState.copy(user = action.user)
                        }
                    }
                    is MainState2.Content -> {
                        previousState.copy(user = action.user)
                    }
                    else -> previousState
                }
            }
            is MainAction2.RepositoriesLoading -> {
                previousState
            }
            is MainAction2.RepositoriesUpdated -> {
                when (previousState) {
                    is MainState2.Loading -> {
                        if (previousState.user == null) {
                            println("previousState.user is null")
                            previousState.copy(repositories = action.repositories)
                        } else {
                            println("previousState.user is not null")
                            MainState2.Content(user = previousState.user, repositories = action.repositories)
                        }
                    }
                    is MainState2.Content -> {
                        previousState.copy(repositories = action.repositories)
                    }
                    else -> previousState
                }
            }
        }.also { println("nextState->${it::class}") }
    }


    class MainProcessor(
        bgContext: CoroutineContext,
        private val gitHubRepository: GitHubRepository
    ) : Processor<MainAction2>(bgContext) {

        private var viewerChannel: ReceiveChannel<Query<User>>? = null
        private var repositoriesChannel: ReceiveChannel<Query<Repository>>? = null

        private fun processObserveViewer() {
            if (viewerChannel != null) return
            println("selectViewer")
            viewerChannel = gitHubRepository.selectViewer().asChannel()

            launch {
                viewerChannel?.consumeEach { q ->
                    println("selectViewer - consumeEach")
                    q.executeAsOneOrNull()?.let { put(MainAction2.UserUpdated(it)) }
                }
            }
        }

        private fun processObserveViewerRepositories() {
            if (repositoriesChannel != null) return
            repositoriesChannel = gitHubRepository.selectRepositoriesForViewer().asChannel()

            launch {
                repositoriesChannel?.consumeEach { q ->
                    put(MainAction2.RepositoriesUpdated(q.executeAsList()))
                }
            }
        }

        private fun processUserLoading() {
            launch {
                gitHubRepository.fetchViewer()
            }
        }

        override fun processAction(action: MainAction2) {
            when (action) {
                MainAction2.UserLoading -> {
                    processUserLoading()
                    processObserveViewer()
                    processObserveViewerRepositories()
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
        }
    }
}

class QueryListener<Q : Any, Z>(
    private val query: Query<Q>,
    private val extractor: (Query<Q>) -> Z
) {
    private val listeners = ThreadLocalRef<MutableCollection<(Z) -> Unit>>()

    private val queryListener = object : Query.Listener {
        override fun queryResultsChanged() {
            backToFront({ extractor(query).freeze() }) { item ->
                listeners.get()!!.forEach { it(item) }
            }
        }
    }

    init {
        listeners.set(mutableSetOf())
        freeze()
    }

    fun addListener(listener: (Z) -> Unit) {
        listeners.get()!!.add(listener)
    }

    fun removeListener(listener: (Z) -> Unit) {
        listeners.get()!!.remove(listener)
    }

    fun removeAllListener() {
        listeners.get()!!.clear()
    }

    fun refresh() {
        queryListener.queryResultsChanged()
    }

    fun dispose() {
        query.removeListener(queryListener)
        listeners.get()?.clear()
    }
}

@ExperimentalCoroutinesApi
fun <T : Any> Query<T>.asChannel(): ReceiveChannel<Query<T>> {
    val channel = Channel<Query<T>>(Channel.CONFLATED)
    channel.offer(this)

    val ref = ThreadLocalRef<(Query<T>) -> Unit>()
    ref.set { channel.offer(it) }

    val listener = object : Query.Listener, (Throwable?) -> Unit {
        override fun queryResultsChanged() {
            backToFront({ this@asChannel }, { q -> ref.get()?.let { it(q) } })
        }

        override fun invoke(t: Throwable?) {
            ref.remove()
            removeListener(this)
        }
    }

    addListener(listener)
    channel.invokeOnClose(listener)

    return channel
}
