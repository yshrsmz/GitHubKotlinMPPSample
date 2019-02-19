package com.codingfeline.github.data.local

import com.squareup.sqldelight.db.SqlDriver

fun createDatabase(driver: SqlDriver): Database {
    return Database(driver)
}
