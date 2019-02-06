package com.codingfeline.githubdata

import com.squareup.sqldelight.Query
import kotlin.native.concurrent.AtomicReference

class UserRepositoryDataNotifier(
    val onUpdate: (repositories: List<Repository>) -> Unit
) : QueryNotifier<Repository> {

    private var query: AtomicReference<Query<Repository>?> = AtomicReference(null)

    private val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            query.value?.executeAsList()?.let(onUpdate)
        }
    }

    override fun updateQuery(newQuery: Query<Repository>) {
        query.value?.removeListener(listener)
        newQuery.addListener(listener)
        newQuery.executeAsList().let(onUpdate)
        query.value = newQuery
    }

    override fun dispose() {
        query.value?.removeListener(listener)
    }
}
