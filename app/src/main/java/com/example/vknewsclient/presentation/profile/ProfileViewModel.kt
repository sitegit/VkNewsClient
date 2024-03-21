package com.example.vknewsclient.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vknewsclient.domain.usecase.GetUserInfoUseCase
import com.example.vknewsclient.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Initial)
    val user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            _user.value = ProfileScreenState.Content(getUserInfoUseCase())
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}