package com.codingfeline.githubdata.local

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

object Db {
    private var driverRef: SqlDriver? = null
    private var dbRef: Database? = null

    val ready: Boolean
        get() = driverRef != null

    internal fun setup(driver: SqlDriver) {
        val db = createDatabase(driver)
        driverRef = driver
        dbRef = db
    }

    internal fun clear() {
        driverRef!!.close()
        dbRef = null
        driverRef = null
    }

    fun defaultDriver(context: Context) {
        Db.setup(AndroidSqliteDriver(Database.Schema, context, "github.db"))
    }

    val instance: Database
        get() = dbRef!!
}
