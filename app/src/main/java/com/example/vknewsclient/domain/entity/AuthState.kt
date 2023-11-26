package com.example.vknewsclient.domain.entity

sealed class AuthState {

    data object Authorized : AuthState()
    data object NotAuthorized : AuthState()
    data object Initial : AuthState()
}
