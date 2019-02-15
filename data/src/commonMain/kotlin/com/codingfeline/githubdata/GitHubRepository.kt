package com.codingfeline.githubdata

import com.codingfeline.githubdata.local.GitHubLocalGateway
import com.codingfeline.githubdata.remote.GitHubRemoteGateway
import com.codingfeline.githubdata.remote.response.toUserAndRepositories
import com.squareup.sqldelight.Query
import kotlin.native.concurrent.ThreadLocal

interface GitHubRepository {
    val localGateway: GitHubLocalGateway

    suspend fun fetchViewer()

    fun selectViewer(): Query<User>

    fun selectRepositoriesByOwner(login: String): Query<Repository>

    fun selectRepositoriesForViewer(): Query<Repository>
}

class GitHubRepositoryImpl(
    @ThreadLocal override val localGateway: GitHubLocalGateway,
    @ThreadLocal private val remoteGateway: GitHubRemoteGateway
) : GitHubRepository {

    init {
        println("GitHubRepositoryImpl#init---")
        printCurrentThread()
        checkIfFrozen("GitHubRepositoryImpl", this)
    }

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

    override fun selectViewer(): Query<User> {
        return localGateway.selectViewer()
    }

    override fun selectRepositoriesByOwner(login: String): Query<Repository> {
        return localGateway.selectRepositoriesForUser(login)
    }

    override fun selectRepositoriesForViewer(): Query<Repository> {
        return localGateway.selectRepositoriesForViewer()
    }

}
