package com.codingfeline.githubdata.remote

import android.os.Build
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(
    sdk = [Build.VERSION_CODES.P],
    manifest = Config.NONE
)
@RunWith(RobolectricTestRunner::class)
class GitHubRemoteRepositoryTest {

    val gateway = GitHubRemoteGatewayImpl()

    @Ignore
    @Test
    fun test() {
        runBlocking {
            val user = gateway.fetchViewerRepository("yshrsmz")

            println(user)
        }
    }
}
