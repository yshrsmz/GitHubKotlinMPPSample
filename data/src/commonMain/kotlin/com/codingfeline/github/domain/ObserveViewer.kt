package com.codingfeline.github.domain

import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import com.codingfeline.github.data.GitHubRepository
import com.codingfeline.github.data.User
import com.squareup.sqldelight.Query

typealias ViewerCallback = (viewer: User) -> Unit

class ObserveViewer(
    private val gitHubRepository: GitHubRepository
) {

    private val query: AtomicReference<Query<User>?> = AtomicReference(null)

    private var callback: AtomicReference<ViewerCallback?> = AtomicReference(null)

    private val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            checkAndUpdate()
        }
    }

    operator fun invoke(callback: ViewerCallback) {
        this.callback.value = callback.freeze()
        query.value = gitHubRepository.selectViewer()
            .also { it.addListener(listener) }

        checkAndUpdate()
    }

    private fun checkAndUpdate() {
        val result = query.value?.executeAsOneOrNull()
        if (result != null) {
            callback.value?.invoke(result)
        }

//        callback?.let { cb ->
//            val result = query.executeAsOneOrNull()
//            if (result != null) {
//                cb(result)
//            }
//        }
    }

    fun dispose() {
        query.value?.removeListener(listener)
        callback.value = null
    }
}
