package com.codingfeline.githubdata.local

import com.squareup.sqldelight.db.SqlDriver

fun createDatabase(driver: SqlDriver): Database {
    return Database(driver)
}
