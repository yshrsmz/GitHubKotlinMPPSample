package com.codingfeline.githubdata.local

import com.codingfeline.githubdata.GitHubRepository
import com.codingfeline.githubdata.MainLoopDispatcher
import com.codingfeline.githubdata.Repository
import com.codingfeline.githubdata.User
import com.squareup.sqldelight.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GitHubRepositoryIos(private val repository: GitHubRepository) : CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = MainLoopDispatcher + job

    fun fetchUser(login: String) {
        println("fetch user")
        launch {
            println("fetch user inner")
            repository.fetchUser(login)
        }
    }

    fun observeUser(login: String): Query<User> {
        return repository.observeUser(login)
    }

    fun observeRepositoriesByOwner(login: String): Query<Repository> {
        return repository.observeRepositoriesByOwner(login)
    }
}
