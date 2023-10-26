package com.example.vknewsclient.ui.theme.screen

import androidx.activity.compose.BackHandler
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
import com.example.vknewsclient.MainViewModel
import com.example.vknewsclient.domain.FeedPost
import com.example.vknewsclient.ui.theme.HomeScreenState
import com.example.vknewsclient.ui.theme.PostCard

@Composable
fun HomeScreen(
    viewModel: MainViewModel
) {
    val screenState = viewModel.screenState.observeAsState(HomeScreenState.Initial)

    when (val currentState = screenState.value) {
        is HomeScreenState.Posts -> FeedPosts(
            posts = currentState.posts,
            viewModel = viewModel
        )
        is HomeScreenState.Comments -> {
            CommentsScreen(
                feedPost = currentState.feedPost,
                comments = currentState.comments,
                onBackPressedListener = {
                    viewModel.closeComments()
                }
            )
            BackHandler {
                viewModel.closeComments()
            }
        }
        is HomeScreenState.Initial -> {}
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun FeedPosts(
    posts: List<FeedPost>,
    viewModel: MainViewModel
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
                background = {},
                directions = setOf(DismissDirection.EndToStart),
                dismissContent = {
                    PostCard(
                        feedPost = feedPost,
                        onViewsClickListener = { statisticItem ->
                            viewModel.updateCount(feedPost, statisticItem)
                        },
                        onLikeClickListener = { statisticItem ->
                            viewModel.updateCount(feedPost, statisticItem)
                        },
                        onCommentClickListener = {
                            viewModel.showComments(feedPost)
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