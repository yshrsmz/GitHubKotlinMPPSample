package com.codingfeline.github.data.local

import com.codingfeline.github.data.Repository
import com.codingfeline.github.data.User
import com.squareup.sqldelight.Query

interface GitHubLocalGateway {

    fun saveUser(user: User)

    fun saveRepositories(repositories: List<Repository>)

    fun saveUserAndRepositories(user: User, repositories: List<Repository>)

    fun selectUser(login: String): Query<User>

    fun selectViewer(): Query<User>

    fun selectAllViewer(): Query<Viewer>

    fun selectRepositoriesForUser(ownerLogin: String): Query<Repository>

    fun selectRepositoriesForViewer(): Query<Repository>
}

class GitHubLocalGatewayImpl(private val database: Database) : GitHubLocalGateway {

    override fun saveUser(user: User) {
        database.transaction {
            database.userQueries.upsertUser(
                id = user.id,
                login = user.login,
                name = user.name,
                bio = user.bio,
                avatar_url = user.avatarUrl,
                company = user.company,
                email = user.email
            )
            database.viewerQueries.upsertViewer(user.id)
        }
    }

    override fun saveRepositories(repositories: List<Repository>) {
        database.transaction {
            repositories.forEach { repo ->
                database.repositoryQueries.upsertRepository(
                    id = repo.id,
                    name = repo.name,
                    description = repo.description,
                    updated_at = repo.updatedAt,
                    url = repo.url,
                    owner_id = repo.ownerId,
                    owner_login = repo.ownerLogin
                )
            }
        }
    }

    override fun saveUserAndRepositories(user: User, repositories: List<Repository>) {
        database.transaction(noEnclosing = true) {
            saveUser(user)
            saveRepositories(repositories)
        }
    }

    override fun selectUser(login: String): Query<User> {
        return database.userQueries.select(
            login = login,
            mapper = { id, login2, name, bio, avatar_url, company, email ->
                User(id, login2, name, bio, avatar_url, company, email)
            }
        )
    }

    override fun selectViewer(): Query<User> {
        return database.viewerQueries.selectViewer(
            mapper = { id, login, name, bio, avatar_url, company, email ->
                User(id, login, name, bio, avatar_url, company, email)
            })
    }

    override fun selectAllViewer(): Query<Viewer> {
        return database.viewerQueries.selectAll()
    }

    override fun selectRepositoriesForUser(ownerLogin: String): Query<Repository> {
        return database.repositoryQueries.forUser(
            owner_login = ownerLogin,
            mapper = { id, name, description, updated_at, url, owner_id, owner_login ->
                Repository(id, name, description, updated_at, url, owner_id, owner_login)
            }
        )
    }

    override fun selectRepositoriesForViewer(): Query<Repository> {
        return database.repositoryQueries.selectViewerRepositories(
            mapper = { id, name, description, updated_at, url, owner_id, owner_login ->
                Repository(id, name, description, updated_at, url, owner_id, owner_login)
            }
        )
    }
}
