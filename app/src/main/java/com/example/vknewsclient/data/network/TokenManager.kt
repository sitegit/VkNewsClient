package com.example.vknewsclient.data.network

import com.vk.api.sdk.VKPreferencesKeyValueStorage
import com.vk.api.sdk.auth.VKAccessToken
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val storage: VKPreferencesKeyValueStorage
) {

    val token
        get() = VKAccessToken.restore(storage)

    fun getAccessToken(): String {
        return token?.accessToken ?: throw IllegalStateException("Token is null")
    }
}