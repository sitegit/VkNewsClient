package com.example.vknewsclient.presentation.comments

import com.example.vknewsclient.domain.entity.FeedPost
import com.example.vknewsclient.domain.entity.PostComment

sealed class CommentsScreenState  {

    data object Initial : CommentsScreenState()

    data object Loading : CommentsScreenState()

    data class Comments(val feedPost: FeedPost, val comments: List<PostComment>) : CommentsScreenState()
}
