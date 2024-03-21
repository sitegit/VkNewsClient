package com.example.vknewsclient.presentation.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vknewsclient.domain.entity.FeedPost
import com.example.vknewsclient.domain.usecase.ChangeLikeStatusUseCase
import com.example.vknewsclient.domain.usecase.GetFavePostsUseCase
import com.example.vknewsclient.presentation.ui.common.NewsFeedScreenState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavouriteViewModel @Inject constructor(
    private val getFavePostsUseCase: GetFavePostsUseCase,
    private val changeLikeStatusUseCase: ChangeLikeStatusUseCase
) : ViewModel() {

    private val favePostsFlow = getFavePostsUseCase()

    val screenState = favePostsFlow
        .map { NewsFeedScreenState.Posts(posts = it) as NewsFeedScreenState }
        .onStart { emit(NewsFeedScreenState.Loading) }

    fun changeLikeStatus(feedPost: FeedPost) {
        viewModelScope.launch {
            changeLikeStatusUseCase(feedPost)
        }
    }
}