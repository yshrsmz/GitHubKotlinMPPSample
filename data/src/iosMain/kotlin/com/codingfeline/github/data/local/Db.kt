package com.codingfeline.github.data.local

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

object Db {

    private val driverRef = AtomicReference<SqlDriver?>(null)
    private val dbRef = AtomicReference<Database?>(null)

    val ready: Boolean
        get() = driverRef.value != null

    internal fun setup(driver: SqlDriver) {
        val db = createDatabase(driver)
        driverRef.value = driver.freeze()
        dbRef.value = db.freeze()
    }

    internal fun clear() {
        driverRef.value!!.close()
        dbRef.value = null
        driverRef.value = null
    }

    // called from swift
    @Suppress("unused")
    fun defaultDriver() {
        Db.setup(NativeSqliteDriver(Database.Schema, "github.db"))
    }

    val instance: Database
        get() = dbRef.value!!
}
