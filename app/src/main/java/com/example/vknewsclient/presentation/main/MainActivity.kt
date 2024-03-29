package com.example.vknewsclient.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vknewsclient.domain.entity.AuthState
import com.example.vknewsclient.presentation.core.getApplicationComponent
import com.example.vknewsclient.presentation.ui.theme.VkNewsClientTheme
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKScope


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val component = getApplicationComponent()
            val viewModel: MainViewModel = viewModel(factory = component.getViewModelFactory())
            val authState = viewModel.authState.collectAsState(initial = AuthState.Initial)

            val authLauncher = rememberLauncherForActivityResult(
                contract = VK.getVKAuthActivityResultContract()
            ) {
                viewModel.performAuthResult()
            }

            VkNewsClientTheme {
                when (authState.value) {
                    is AuthState.Authorized -> MainScreen()
                    is AuthState.NotAuthorized -> LoginScreen {
                        authLauncher.launch(listOf(VKScope.WALL, VKScope.FRIENDS))
                    }
                    else -> {}
                }
            }
        }
    }
}

