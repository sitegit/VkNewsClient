package com.example.vknewsclient.presentation.news

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vknewsclient.domain.FeedPost

@Composable
fun NewsFeedScreen(
    onCommentsClickListener: (FeedPost) -> Unit
) {
    val viewModel: NewsFeedViewModel = viewModel()
    val screenState = viewModel.screenState.observeAsState(NewsFeedScreenState.Initial)

    when (val currentState = screenState.value) {
        is NewsFeedScreenState.Posts -> FeedPosts(
            posts = currentState.posts,
            viewModel = viewModel,
            onCommentsClickListener = onCommentsClickListener
        )
        is NewsFeedScreenState.Initial -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun FeedPosts(
    posts: List<FeedPost>,
    viewModel: NewsFeedViewModel,
    onCommentsClickListener: (FeedPost) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            top = 16.dp,
            start = 8.dp,
            end = 8.dp,
            bottom = 92.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = posts, key = { it.id }) { feedPost ->
            val dissmissState = rememberDismissState()

            if (dissmissState.isDismissed(direction = DismissDirection.EndToStart)) {
                viewModel.removePost(feedPost)
            }

            SwipeToDismiss(
                modifier = Modifier.animateItemPlacement(),
                state = dissmissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {},
                dismissContent = {
                    PostCard(
                        feedPost = feedPost,
                        onViewsClickListener = { statisticItem ->
                            viewModel.updateCount(feedPost, statisticItem)
                        },
                        onLikeClickListener = { _ ->
                            viewModel.changeLikeStatus(feedPost)
                        },
                        onCommentClickListener = {
                            onCommentsClickListener(feedPost)
                        },
                        onShareClickListener = { statisticItem ->
                            viewModel.updateCount(feedPost, statisticItem)
                        }
                    )
                }
            )
        }
    }
}