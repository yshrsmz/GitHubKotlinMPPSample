package com.codingfeline.github.data.local

import co.touchlab.stately.collections.frozenCopyOnWriteList
import co.touchlab.stately.freeze
import com.codingfeline.arch.BasePub
import com.codingfeline.arch.Sub
import com.squareup.sqldelight.Query

class QueryPub<Q : Any, Z>(
    val query: Query<Q>,
    val extractData: (Query<Q>) -> Z
) : Query.Listener, BasePub<Z>() {

    private val subList = frozenCopyOnWriteList<Sub<Z>>()

    init {
        query.addListener(this)
        freeze()
    }

    override fun subs(): MutableCollection<Sub<Z>> = subList

    override fun queryResultsChanged() {
        applyNext { extractData(query) }
    }

    fun refresh() {
        queryResultsChanged()
    }

    override fun dispose() {
        super.dispose()
        query.removeListener(this)
    }
}
