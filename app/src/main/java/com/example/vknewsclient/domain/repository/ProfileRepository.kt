package com.example.vknewsclient.domain.repository

import com.example.vknewsclient.domain.entity.User

interface ProfileRepository {

    suspend fun getUserInfo(): User
}