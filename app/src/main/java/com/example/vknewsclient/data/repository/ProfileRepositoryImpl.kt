package com.example.vknewsclient.data.repository

import com.example.vknewsclient.data.mapper.NewsFeedMapper
import com.example.vknewsclient.data.network.ApiService
import com.example.vknewsclient.data.network.TokenManager
import com.example.vknewsclient.domain.entity.User
import com.example.vknewsclient.domain.repository.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mapper: NewsFeedMapper,
    private val tokenManager: TokenManager
) : ProfileRepository {

    override suspend fun getUserInfo(): User {
        val result = apiService.getUser(accessToken = tokenManager.getAccessToken())
        return mapper.mapUserResponseToUser(result)
    }
}