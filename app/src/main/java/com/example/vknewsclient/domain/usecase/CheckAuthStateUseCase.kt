package com.example.vknewsclient.domain.usecase

import com.example.vknewsclient.domain.repository.AuthRepository
import javax.inject.Inject

class CheckAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {

    suspend operator fun invoke() {
        repository.checkAuthState()
    }
}