package com.example.vknewsclient.navigation

import com.example.vknewsclient.domain.FeedPost

sealed class Screen(
    val route: String
) {

    data object Home : Screen(ROUTE_HOME)
    data object Comments : Screen(ROUTE_COMMENTS) {
        private const val ROUTE_FOR_ARGS = "comments"
        fun getRouteWithArgs(feedPost: FeedPost): String {
            return "$ROUTE_FOR_ARGS/${feedPost.id}/${feedPost.contentText}"
        }
    }
    data object NewsFeed : Screen(ROUTE_NEWS_FEED)
    data object Favourite : Screen(ROUTE_FAVOURITE)
    data object Profile : Screen(ROUTE_PROFILE)

    companion object {
        const val KEY_FEED_POST_ID = "feed_post_id"
        const val KEY_FEED_POST_TEST_STRING = "test_string"

        const val ROUTE_HOME = "home"
        const val ROUTE_COMMENTS = "comments/{$KEY_FEED_POST_ID}/{$KEY_FEED_POST_TEST_STRING}"
        const val ROUTE_NEWS_FEED = "news_feed"
        const val ROUTE_FAVOURITE = "favourite"
        const val ROUTE_PROFILE = "profile"
    }
}