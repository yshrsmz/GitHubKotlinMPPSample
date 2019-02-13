package com.codingfeline.githubui

import com.codingfeline.githubdata.GitHubRepository
import com.codingfeline.githubdata.Repository
import com.codingfeline.githubdata.User
import com.squareup.sqldelight.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ViewerViewModel constructor(
    uiContext: CoroutineContext,
    private val githubRepository: GitHubRepository
) : ViewModel(), CoroutineScope {

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext = uiContext + job

    private var viewState: ViewerViewState = ViewerViewState.Loading

    private var stateListener: ((state: ViewerViewState) -> Unit)? = null


    private val userQuery: Query<User> = githubRepository.observeViewer()

    private var repoQuery: Query<Repository>? = null

    private val userListener = object : Query.Listener {
        override fun queryResultsChanged() {
            val user = userQuery.executeAsOneOrNull() ?: return

            val oldState = viewState

            val state = when (oldState) {
                is ViewerViewState.Loading -> {
                    ViewerViewState.Data(viewer = user, repositories = emptyList())
                }
                is ViewerViewState.Data -> {
                    if (oldState.viewer.id != user.id) {
                        oldState.copy(viewer = user, repositories = emptyList())
                    } else {
                        oldState.copy(viewer = user)
                    }
                }
                is ViewerViewState.Error -> {
                    ViewerViewState.Data(viewer = user, repositories = emptyList())
                }
            }

            if (state != oldState) {
                viewState = state
                stateListener?.invoke(state)

                // update repository query
                repoQuery = githubRepository.observeRepositoriesByOwner(state.viewer.login)
                    .also { it.addListener(repoListener) }
            }
        }
    }

    fun test() {

    }

    private val repoListener = object : Query.Listener {
        override fun queryResultsChanged() {
            val repos = repoQuery?.executeAsList() ?: emptyList()

            val oldState = viewState

            val state = when (oldState) {
                is ViewerViewState.Data -> {
                    oldState.copy(repositories = repos)
                }
                else -> oldState
            }
            if (state != oldState) {
                viewState = state
                stateListener?.invoke(state)
            }
        }
    }


    fun init() {
        launch {
            githubRepository.fetchViewer()
        }
    }

    fun observeViewStateChange(callback: (state: ViewerViewState) -> Unit) {
        stateListener = callback
        // notify initial state
        stateListener?.invoke(viewState)
    }

    override fun onCleared() {
        super.onCleared()
        stateListener = null
        userQuery.removeListener(userListener)
        repoQuery?.removeListener(repoListener)
        job.cancel()
    }
}


sealed class ViewerViewState {
    object Loading : ViewerViewState()

    data class Data(
        val viewer: User,
        val repositories: List<Repository>
    ) : ViewerViewState()

    data class Error(val message: String) : ViewerViewState()
}
