package com.codingfeline.githubdata

import co.touchlab.sqliter.DatabaseConfiguration
import com.codingfeline.githubdata.local.Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import com.squareup.sqldelight.drivers.ios.wrapConnection
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased.bind
import org.kodein.di.erased.eagerSingleton
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import kotlin.coroutines.CoroutineContext

internal fun appModule(): Kodein.Module {
    return Kodein.Module(name = "app") {
        bind<SqlDriver>() with eagerSingleton {
            //NativeSqliteDriver(Database.Schema, null)
            NativeSqliteDriver(
                configuration = DatabaseConfiguration(
                    name = "memorydb",
                    version = Database.Schema.version,
                    create = { connection ->
                        wrapConnection(connection) { Database.Schema.create(it) }
                    },
                    upgrade = { connection, oldVersion, newVersion ->
                        wrapConnection(connection) { Database.Schema.migrate(it, oldVersion, newVersion) }
                    }
                )
            )
        }
        bind<GitHubRepositoryIos>() with eagerSingleton { GitHubRepositoryIos(instance()) }
        bind<CoroutineContext>(tag = "uicontext") with provider { ApplicationDispatcher }
    }
}

fun initKodein(): Kodein {
    return Kodein {
        import(remoteModule)
        import(localModule)
        import(dataModule)
        import(appModule())
    }.also { kodein ->

    }
}

fun getGitHubRepository(kodein: Kodein): GitHubRepositoryIos {
    return kodein.direct.instance()
}
