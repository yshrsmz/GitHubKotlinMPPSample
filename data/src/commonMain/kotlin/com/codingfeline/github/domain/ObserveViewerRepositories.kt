package com.codingfeline.github.domain

import com.codingfeline.github.data.GitHubRepository
import com.codingfeline.github.data.Repository
import com.codingfeline.github.data.local.QueryPub

class ObserveViewerRepositories(
    val gitHubRepository: GitHubRepository
) {

    operator fun <T> invoke(transformer: (List<Repository>) -> T): QueryPub<Repository, T> {
        return QueryPub(gitHubRepository.selectRepositoriesForViewer()) { transformer(it.executeAsList()) }
    }

}
