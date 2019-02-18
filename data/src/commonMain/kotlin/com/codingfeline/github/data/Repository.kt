package com.codingfeline.githubdata

data class Repository(
    val id: String,
    val name: String,
    val description: String,
    val updatedAt: String,
    val url: String,
    val ownerId: String,
    val ownerLogin: String
)
