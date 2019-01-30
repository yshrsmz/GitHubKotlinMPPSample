package com.codingfeline.githubdata

import com.codingfeline.githubdata.local.GitHubLocalGateway
import com.codingfeline.githubdata.remote.GitHubRemoteGateway
import com.codingfeline.githubdata.remote.response.toUserAndRepositories
import com.squareup.sqldelight.Query

interface GitHubRepository {

    suspend fun fetchUser(login: String)

    fun observeUser(login: String): Query<User>

    fun observeRepositoriesByOwner(login: String): Query<Repository>
}

class GitHubRepositoryImpl(
    private val localGateway: GitHubLocalGateway,
    private val remoteGateway: GitHubRemoteGateway
) : GitHubRepository {

    override suspend fun fetchUser(login: String) {
        println("GithubRepository#fetchUser")
        val result = remoteGateway.fetchUserRepository(login)

        if (result.data != null) {
            val dto = result.data.toUserAndRepositories()
            localGateway.saveUserAndRepositories(dto.user, dto.repositories)
        }
    }

    override fun observeUser(login: String): Query<User> {
        return localGateway.observeUser(login)
    }

    override fun observeRepositoriesByOwner(login: String): Query<Repository> {
        return localGateway.observeRepositoriesForUser(login)
    }

}
