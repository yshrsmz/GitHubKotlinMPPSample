package com.codingfeline.github

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codingfeline.github.data.dataModule
import com.codingfeline.github.data.local.Database
import com.codingfeline.github.data.localModule
import com.codingfeline.github.data.remoteModule
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton
import kotlin.coroutines.CoroutineContext

internal fun appModule(context: Context): Kodein.Module {
    return Kodein.Module(name = "app") {
        bind<Context>() with instance(context)
        bind<SqlDriver>() with singleton {
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = instance(),
                name = null, // memory db for debug
                callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        db.execSQL("PRAGMA foreign_keys=ON;")
                    }
                })
        }
        bind<CoroutineContext>(Tags.UI_CONTEXT) with provider { Dispatchers.Main }
        bind<CoroutineContext>(Tags.BG_CONTEXT) with provider { Dispatchers.IO }
    }
}


fun initKodein(context: Context): Kodein {
    return Kodein {
        import(remoteModule)
        import(localModule)
        import(dataModule)
        import(appModule(context))
    }
}
