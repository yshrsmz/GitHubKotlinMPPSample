package com.codingfeline.githubdomain

import com.codingfeline.githubdata.GitHubRepository
import com.codingfeline.githubdata.User
import com.squareup.sqldelight.Query

class ObserveViewer(
    private val gitHubRepository: GitHubRepository
) {

    private lateinit var query: Query<User>

    private var callback: ((viewer: User) -> Unit)? = null

    private val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            checkAndUpdate()
        }
    }

    operator fun invoke(callback: (viewer: User) -> Unit) {
        this.callback = callback
        query = gitHubRepository.observeViewer()
        query.addListener(listener)

        checkAndUpdate()
    }

    private fun checkAndUpdate() {
        callback?.let { cb ->
            val result = query.executeAsOneOrNull()
            if (result != null) {
                cb(result)
            }
        }
    }

    fun dispose() {
        query.removeListener(listener)
        callback = null
    }
}
