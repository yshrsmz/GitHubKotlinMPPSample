package com.codingfeline.githubdata

import com.squareup.sqldelight.Query

class UserDataNotifier(
    private val query: Query<User>,
    val onUpdate: (User?) -> Unit
) {
    val listener = object : Query.Listener {
        override fun queryResultsChanged() {
            val result = query.executeAsOneOrNull()
            onUpdate(result)
        }
    }

    init {
        query.addListener(listener)
        val result = query.executeAsOneOrNull()
        onUpdate(result)
    }

    fun dispose() {
        query.removeListener(listener)
    }
}
