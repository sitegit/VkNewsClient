package com.example.vknewsclient.ui.theme.screen.comments

import com.example.vknewsclient.domain.FeedPost
import com.example.vknewsclient.domain.PostComment

sealed class CommentsScreenState  {

    data object Initial : CommentsScreenState()
    data class Comments(val feedPost: FeedPost, val comments: List<PostComment>) : CommentsScreenState()
}
