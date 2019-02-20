package com.codingfeline.github.data.remote

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.codingfeline.github.initKodein
import io.ktor.client.HttpClient
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(
    sdk = [Build.VERSION_CODES.P],
    manifest = Config.NONE
)
@RunWith(RobolectricTestRunner::class)
class GitHubRemoteRepositoryTest : KodeinAware {

    override val kodein = initKodein(InstrumentationRegistry.getInstrumentation().targetContext)

    val httpClient by instance<HttpClient>()

    val gateway = GitHubRemoteGatewayImpl(httpClient)

    @Ignore
    @Test
    fun test() {
        runBlocking {
            val user = gateway.fetchViewerRepository()

            println(user)
        }
    }
}
