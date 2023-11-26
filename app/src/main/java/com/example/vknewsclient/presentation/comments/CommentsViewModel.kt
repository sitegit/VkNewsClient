package com.example.vknewsclient.presentation.comments

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.vknewsclient.data.repository.NewsFeedRepository
import com.example.vknewsclient.domain.FeedPost
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CommentsViewModel(feedPost: FeedPost, application: Application) : ViewModel() {

    private val repository = NewsFeedRepository(application)

    val screenState = repository.getComments(feedPost)
        .map {
            CommentsScreenState.Comments(
                feedPost = feedPost,
                comments = it
            ) as CommentsScreenState
        }
        .onStart { emit(CommentsScreenState.Loading) } // Инициализация состояния загрузки
}