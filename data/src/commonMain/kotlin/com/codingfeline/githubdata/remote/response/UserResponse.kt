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
    val email: String
)
