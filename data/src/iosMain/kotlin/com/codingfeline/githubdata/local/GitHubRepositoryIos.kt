package com.codingfeline.githubdata.local

import com.codingfeline.githubdata.MainLoopDispatcher
import com.codingfeline.githubdata.remote.GitHubRemoteGateway
import com.codingfeline.githubdata.remote.GitHubRemoteGatewayImpl
import com.codingfeline.githubdata.remote.response.toUser
import com.codingfeline.kgql.core.KgqlError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GitHubRepository : CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = MainLoopDispatcher + job

    val gateway: GitHubRemoteGateway =
        GitHubRemoteGatewayImpl()


    fun fetchUser(login: String, callback: (user: com.codingfeline.githubdata.User?, errors: List<KgqlError>) -> Unit) {
        launch {
            val result = gateway.fetchUserRepository(login)

            callback(result.data?.toUser(), result.errors ?: emptyList())
        }
    }

    fun observeUser(login: String, callback: (user: User?) -> Unit) {

    }
}
