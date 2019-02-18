package com.codingfeline.github.data

import com.squareup.sqldelight.Query

interface QueryNotifier<T : Any> {

    fun updateQuery(newQuery: Query<T>)

    fun dispose()
}
