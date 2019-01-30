package com.codingfeline.githubdata.local

import com.codingfeline.githubdata.Repository
import com.codingfeline.githubdata.User
import com.squareup.sqldelight.Query

interface GitHubLocalGateway {

    fun saveUser(user: User)

    fun saveRepositories(repositories: List<Repository>)

    fun saveUserAndRepositories(user: User, repositories: List<Repository>)

    fun observeUser(login: String): Query<User>

    fun observeRepositoriesForUser(ownerLogin: String): Query<Repository>
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

    override fun observeUser(login: String): Query<User> {
        return database.userQueries.select(
            login = login,
            mapper = { id, login2, name, bio, avatar_url, company, email ->
                User(id, login2, name, bio, avatar_url, company, email)
            }
        )
    }

    override fun observeRepositoriesForUser(ownerLogin: String): Query<Repository> {
        return database.repositoryQueries.forUser(
            owner_login = ownerLogin,
            mapper = { id, name, description, updated_at, url, owner_id, owner_login ->
                Repository(id, name, description, updated_at, url, owner_id, owner_login)
            }
        )
    }
}
