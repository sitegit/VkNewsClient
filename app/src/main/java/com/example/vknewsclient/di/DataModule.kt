package com.example.vknewsclient.di

import android.content.Context
import com.example.vknewsclient.data.network.ApiFactory
import com.example.vknewsclient.data.network.ApiService
import com.example.vknewsclient.data.repository.AuthRepositoryImpl
import com.example.vknewsclient.data.repository.NewsFeedRepositoryImpl
import com.example.vknewsclient.data.repository.ProfileRepositoryImpl
import com.example.vknewsclient.domain.repository.AuthRepository
import com.example.vknewsclient.domain.repository.NewsFeedRepository
import com.example.vknewsclient.domain.repository.ProfileRepository
import com.vk.api.sdk.VKPreferencesKeyValueStorage
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindNewsFeedRepository(repositoryImpl: NewsFeedRepositoryImpl): NewsFeedRepository

    @ApplicationScope
    @Binds
    fun bindAuthRepository(repositoryImpl: AuthRepositoryImpl): AuthRepository

    @ApplicationScope
    @Binds
    fun bindProfileRepository(repositoryImpl: ProfileRepositoryImpl): ProfileRepository

    companion object {

        @ApplicationScope
        @Provides
        fun provideApiService(): ApiService {
            return ApiFactory.apiService
        }

        @ApplicationScope
        @Provides
        fun provideVkStorage(
            context: Context
        ): VKPreferencesKeyValueStorage {
            return VKPreferencesKeyValueStorage(context)
        }
    }
}