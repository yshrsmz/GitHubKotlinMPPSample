package com.codingfeline.githubdata

import com.codingfeline.githubdata.local.GitHubLocalGateway
import com.codingfeline.githubdata.local.Viewer
import com.codingfeline.githubdata.remote.GitHubRemoteGateway
import com.codingfeline.githubdata.remote.response.toUserAndRepositories
import com.squareup.sqldelight.Query

interface GitHubRepository {

    suspend fun fetchViewer()

    fun observeUser(login: String): Query<User>

    fun observeViewer(): Query<User>

    fun observeRepositoriesByOwner(login: String): Query<Repository>

    fun observeAllViewer(): Query<Viewer>
}

class GitHubRepositoryImpl(
    private val localGateway: GitHubLocalGateway,
    private val remoteGateway: GitHubRemoteGateway
) : GitHubRepository {

    override suspend fun fetchViewer() {
        printCurrentThread()
        println("GithubRepository#fetchViewer")
        checkIfFrozen("GitHubRepositoryImpl$this", this)
        checkIfFrozen("localGateway", localGateway)
        checkIfFrozen("remoteGateway", remoteGateway)
        val result = remoteGateway.fetchViewerRepository()

        if (result.data != null) {
            val dto = result.data.toUserAndRepositories()
            localGateway.saveUserAndRepositories(dto.user, dto.repositories)
        } else {
            result.errors?.forEach {
                println("ERROR: ${it.message}")
            }
        }
    }

    override fun observeUser(login: String): Query<User> {
        return localGateway.observeUser(login)
    }

    override fun observeViewer(): Query<User> {
        return localGateway.observeViewer()
    }

    override fun observeAllViewer(): Query<Viewer> {
        return localGateway.observeAllViewer()
    }

    override fun observeRepositoriesByOwner(login: String): Query<Repository> {
        return localGateway.observeRepositoriesForUser(login)
    }

}
