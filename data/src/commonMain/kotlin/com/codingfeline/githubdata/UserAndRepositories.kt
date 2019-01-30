package com.codingfeline.githubdata

data class UserAndRepositories(
    val user: User,
    val repositories: List<Repository>
)
