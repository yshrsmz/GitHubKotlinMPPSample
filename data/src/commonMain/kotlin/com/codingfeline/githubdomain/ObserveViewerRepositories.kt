package com.codingfeline.githubdomain

import com.codingfeline.githubdata.AtomicReference
import com.codingfeline.githubdata.GitHubRepository
import com.codingfeline.githubdata.Repository
import com.codingfeline.githubdata.freeze
import com.codingfeline.githubdata.value
import com.squareup.sqldelight.Query

typealias RepositoriesCallback = (repositories: List<Repository>) -> Unit

class ObserveViewerRepositories(
    val gitHubRepository: GitHubRepository
) {

    private val query: AtomicReference<Query<Repository>?> = AtomicReference(null)

    private var callback: AtomicReference<RepositoriesCallback?> = AtomicReference(null)

    private val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            checkAndUpdate()
        }
    }

    private fun checkAndUpdate() {
        callback.value?.let { cb ->
            val result = query.value?.executeAsList() ?: emptyList()
            cb(result)
        }
    }

    operator fun invoke(callback: RepositoriesCallback) {
        this.callback.value = callback.freeze()
        query.value = gitHubRepository.selectRepositoriesForViewer()
            .also { it.addListener(listener) }

        checkAndUpdate()
    }

    fun dispose() {
        query.value?.removeListener(listener)
        callback.value = null
    }
}
