package com.example.vknewsclient.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.vknewsclient.domain.FeedPost
import com.example.vknewsclient.navigation.Screen.Companion.KEY_FEED_POST_ID
import com.example.vknewsclient.navigation.Screen.Companion.KEY_FEED_POST_TEST_STRING

fun NavGraphBuilder.homeScreenNavGraph(
    newsFeedScreenContent: @Composable () -> Unit,
    commentsScreenContent: @Composable (FeedPost) -> Unit
) {
    navigation(
        startDestination = Screen.NewsFeed.route,
        route = Screen.Home.route
    ) {
        composable(Screen.NewsFeed.route) {
            newsFeedScreenContent()
        }
        composable(
            route = Screen.Comments.route,
            arguments = listOf(
                navArgument(name = KEY_FEED_POST_ID) {
                    type = NavType.IntType
                },
                navArgument(name = KEY_FEED_POST_TEST_STRING) {
                    type = NavType.StringType
                }
            )
        ) {
            val feedPostId = it.arguments?.getInt(KEY_FEED_POST_ID) ?: 0
            val testStr = it.arguments?.getString(KEY_FEED_POST_TEST_STRING) ?: ""
            commentsScreenContent(FeedPost(id = feedPostId, contentText = testStr))
        }
    }
}