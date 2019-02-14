package com.codingfeline.githubdomain

import com.codingfeline.githubdata.GitHubRepository

class FetchViewer(
    private val gitHubRepository: GitHubRepository
) {

    suspend operator fun invoke() {
        gitHubRepository.fetchViewer()
    }
}
