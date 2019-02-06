package com.codingfeline.githubdata

import com.squareup.sqldelight.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class GitHubRepositoryIos(private val repository: GitHubRepository) : CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = MainLoopDispatcher + job

    fun fetchViewer() {
        launch {
            repository.fetchViewer()
        }
    }

    fun observeUser(login: String): Query<User> {
        return repository.observeUser(login)
    }

    fun observeViewer(): Query<User> {
        return repository.observeViewer()
    }

    fun observeRepositoriesByOwner(login: String): Query<Repository> {
        return repository.observeRepositoriesByOwner(login)
    }
}
