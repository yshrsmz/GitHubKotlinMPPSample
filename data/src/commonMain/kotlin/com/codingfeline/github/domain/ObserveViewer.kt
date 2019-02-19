package com.codingfeline.github.domain

import com.codingfeline.github.data.GitHubRepository
import com.codingfeline.github.data.User
import com.codingfeline.github.data.local.QueryPub

class ObserveViewer(
    private val gitHubRepository: GitHubRepository
) {
    operator fun <T> invoke(transformer: (User?) -> T): QueryPub<User, T> {
        return QueryPub(gitHubRepository.selectViewer()) { transformer(it.executeAsOneOrNull()) }
    }
}
