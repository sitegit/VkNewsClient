package com.example.vknewsclient.presentation.profile

import com.example.vknewsclient.domain.entity.User

sealed class ProfileScreenState {
    data object Initial : ProfileScreenState()
    data class Content(val user: User) : ProfileScreenState()
}