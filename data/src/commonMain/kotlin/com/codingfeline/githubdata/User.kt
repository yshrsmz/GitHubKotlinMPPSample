package com.codingfeline.githubdata

data class User(
    val id: String,
    val login: String,
    val name: String,
    val bio: String,
    val avatarUrl: String,
    val company: String,
    val email: String,
    val repositories: List<Repository>
)
