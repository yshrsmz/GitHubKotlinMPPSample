package com.codingfeline.githubdata

import com.squareup.sqldelight.Query
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.isFrozen

class UserDataNotifier(
    val onUpdate: (User?) -> Unit
) : QueryNotifier<User> {

    private val query: AtomicReference<Query<User>?> = AtomicReference(null)

    private val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            println("user query results changed")
            query.value?.executeAsOneOrNull()?.let(onUpdate)
        }
    }

    override fun updateQuery(newQuery: Query<User>) {
        println("query frozen?:${query.isFrozen}")
        query.value?.removeListener(listener)
        newQuery.addListener(listener)
        newQuery.executeAsOneOrNull().let(onUpdate)
        query.value = newQuery
    }

    override fun dispose() {
        query.value?.removeListener(listener)
    }
}
