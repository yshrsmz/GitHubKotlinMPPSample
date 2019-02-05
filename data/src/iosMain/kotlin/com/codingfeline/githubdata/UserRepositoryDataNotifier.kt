package com.codingfeline.githubdata

import com.squareup.sqldelight.Query

class UserRepositoryDataNotifier(
    val onUpdate: (repositories: List<Repository>) -> Unit
) : QueryNotifier<Repository> {

    private var query: Query<Repository>? = null

    private val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            query?.executeAsList()?.let(onUpdate)
        }
    }

    override fun updateQuery(newQuery: Query<Repository>) {
        query?.removeListener(listener)
        query = newQuery
        newQuery.executeAsList().let(onUpdate)
    }

    override fun dispose() {
        query?.removeListener(listener)
    }
}
