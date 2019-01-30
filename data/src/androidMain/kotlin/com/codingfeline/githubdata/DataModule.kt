package com.codingfeline.githubdata

import android.content.Context
import com.codingfeline.githubdata.local.Db
import com.codingfeline.githubdata.local.GitHubLocalGatewayImpl
import com.codingfeline.githubdata.remote.GitHubRemoteGatewayImpl

internal var repository: GitHubRepository? = null

fun getGitHubRepository(context: Context): GitHubRepository {
    if (!Db.ready) {
        Db.defaultDriver(context)
    }
    return repository ?: run {
        GitHubRepositoryImpl(
            localGateway = GitHubLocalGatewayImpl(Db.instance),
            remoteGateway = GitHubRemoteGatewayImpl()
        ).also { repository = it }
    }
}
