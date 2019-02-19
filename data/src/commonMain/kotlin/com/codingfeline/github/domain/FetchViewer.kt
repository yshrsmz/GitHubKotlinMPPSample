package com.codingfeline.github.domain

import com.codingfeline.github.data.GitHubRepository

class FetchViewer(
    private val gitHubRepository: GitHubRepository
) {

    suspend operator fun invoke() {
        gitHubRepository.fetchViewer()
    }
}
