package com.codingfeline.githubdata.remote.response

import com.codingfeline.githubdata.Repository
import com.codingfeline.githubdata.User
import com.codingfeline.githubdata.UserAndRepositories
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val login: String,
    val name: String,
    val bio: String,
    val avatarUrl: String,
    val company: String,
    val email: String,
    val repositories: RepositoriesResponse
)

@Serializable
data class RepositoriesResponse(
    val nodes: List<RepositoryResponse>
)

@Serializable
data class RepositoryResponse(
    val id: String,
    val name: String,
    val description: String,
    val updatedAt: String,
    val url: String,
    val owner: OwnerResponse
)

@Serializable
data class OwnerResponse(
    val id: String,
    val login: String
)

fun RepositoryResponse.toRepository(): Repository {
    return Repository(
        id = id,
        name = name,
        description = description,
        updatedAt = updatedAt,
        url = url,
        ownerId = owner.id,
        ownerLogin = owner.login
    )
}

fun UserResponse.toUser(): User {
    return User(
        id = id,
        login = login,
        name = name,
        bio = bio,
        avatarUrl = avatarUrl,
        company = company,
        email = email
    )
}

fun UserResponse.toUserAndRepositories(): UserAndRepositories {
    return UserAndRepositories(
        user = toUser(),
        repositories = repositories.nodes.map { it.toRepository() }
    )
}
