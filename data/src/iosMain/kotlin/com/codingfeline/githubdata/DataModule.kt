package com.codingfeline.githubdata

import com.codingfeline.githubdata.local.Db
import com.codingfeline.githubdata.local.GitHubLocalGatewayImpl
import com.codingfeline.githubdata.remote.GitHubRemoteGatewayImpl

fun getGitHubRepository(): GitHubRepositoryIos {
    if (!Db.ready) {
        Db.defaultDriver()
    }

    return GitHubRepositoryIos(
        repository = GitHubRepositoryImpl(
            localGateway = GitHubLocalGatewayImpl(Db.instance),
            remoteGateway = GitHubRemoteGatewayImpl()
        )
    )
}

