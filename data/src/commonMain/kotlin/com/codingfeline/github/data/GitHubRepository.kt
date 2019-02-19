package com.codingfeline.github.data

import co.touchlab.stately.ensureNeverFrozen
import com.codingfeline.github.data.local.GitHubLocalGateway
import com.codingfeline.github.data.remote.GitHubRemoteGateway
import com.codingfeline.github.data.remote.response.toUserAndRepositories
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
        ensureNeverFrozen()
    }

    override suspend fun fetchViewer() {

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
