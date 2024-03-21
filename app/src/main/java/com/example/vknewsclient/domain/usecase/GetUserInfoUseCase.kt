package com.example.vknewsclient.domain.usecase

import com.example.vknewsclient.domain.entity.User
import com.example.vknewsclient.domain.repository.ProfileRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): User {
        return repository.getUserInfo()
    }
}