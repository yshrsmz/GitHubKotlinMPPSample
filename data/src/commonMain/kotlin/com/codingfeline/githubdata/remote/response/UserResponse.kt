package com.codingfeline.githubdata.remote.response

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

data class RepositoriesResponse(
    val nodes: List<RepositoryResponse>
)

data class RepositoryResponse(
    val id: String,
    val name: String,
    val description: String,
    val updatedAt: String,
    val url: String,
    val owner: OwnerResponse
)

data class OwnerResponse(
    val id: String,
    val login: String
)
