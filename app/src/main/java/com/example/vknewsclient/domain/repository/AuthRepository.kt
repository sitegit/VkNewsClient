package com.example.vknewsclient.domain.repository

import com.example.vknewsclient.domain.entity.AuthState
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    fun getAuthStateFlow(): StateFlow<AuthState>

    suspend fun checkAuthState()

    suspend fun logout()
}