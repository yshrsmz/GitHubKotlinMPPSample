package com.codingfeline.github.domain

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import com.codingfeline.github.data.GitHubRepository
import com.codingfeline.github.data.Repository
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
