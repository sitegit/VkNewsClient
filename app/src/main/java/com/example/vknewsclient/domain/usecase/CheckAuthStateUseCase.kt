package com.example.vknewsclient.domain.usecase

import com.example.vknewsclient.domain.repository.NewsFeedRepository
class CheckAuthStateUseCase(
    private val repository: NewsFeedRepository
) {

    suspend operator fun invoke() {
        repository.checkAuthState()
    }
}