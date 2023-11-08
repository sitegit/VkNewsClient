package com.example.vknewsclient.presentation.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKPreferencesKeyValueStorage
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthenticationResult

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _authState = MutableLiveData<AuthState>(AuthState.Initial)
    val authState: LiveData<AuthState> = _authState

    init {
        val storage = VKPreferencesKeyValueStorage(application)
        val token = VKAccessToken.restore(storage)
        Log.i("MyTag", "${ token?.accessToken }")
        val loggedIn = token != null && token.isValid
        _authState.value = if (loggedIn) AuthState.Authorized else AuthState.NotAuthorized
    }

    fun performAuthResult(result: VKAuthenticationResult) {
        _authState.value = if (result is VKAuthenticationResult.Success)
            AuthState.Authorized
        else {
            Log.i("MyTag", (result as VKAuthenticationResult.Failed).exception.toString())
            AuthState.NotAuthorized
        }
    }
}