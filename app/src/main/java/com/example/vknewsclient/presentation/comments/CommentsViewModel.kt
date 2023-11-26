package com.example.vknewsclient.presentation.comments

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.vknewsclient.data.repository.NewsFeedRepositoryImpl
import com.example.vknewsclient.domain.entity.FeedPost
import com.example.vknewsclient.domain.usecase.GetCommentsUseCase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CommentsViewModel(feedPost: FeedPost, application: Application) : ViewModel() {

    private val repository = NewsFeedRepositoryImpl(application)

    private val getCommentsUseCase = GetCommentsUseCase(repository)

    val screenState = getCommentsUseCase(feedPost)
        .map {
            CommentsScreenState.Comments(
                feedPost = feedPost,
                comments = it
            ) as CommentsScreenState
        }
        .onStart { emit(CommentsScreenState.Loading) } // Инициализация состояния загрузки
}