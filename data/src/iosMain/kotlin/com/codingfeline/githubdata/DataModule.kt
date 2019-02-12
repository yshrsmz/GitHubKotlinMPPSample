package com.codingfeline.githubdata

import com.codingfeline.githubdata.local.Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.erased.bind
import org.kodein.di.erased.eagerSingleton
import org.kodein.di.erased.instance

internal fun appModule(): Kodein.Module {
    return Kodein.Module(name = "app") {
        bind<SqlDriver>() with eagerSingleton { NativeSqliteDriver(Database.Schema, "github.db") }
        bind<GitHubRepositoryIos>() with eagerSingleton { GitHubRepositoryIos(instance()) }
    }
}

fun initKodein(): Kodein {
    return Kodein {
        import(remoteModule)
        import(localModule)
        import(dataModule)
        import(appModule())
    }
}

fun getGitHubRepository(kodein: Kodein): GitHubRepositoryIos {
    return kodein.direct.instance()
}
