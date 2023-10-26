package com.example.vknewsclient.ui.theme.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.vknewsclient.domain.FeedPost
import com.example.vknewsclient.navigation.AppNavGraph
import com.example.vknewsclient.navigation.rememberNavigationState
import com.example.vknewsclient.navigation.NavigationItem
import com.example.vknewsclient.navigation.Screen
import com.example.vknewsclient.ui.theme.screen.comments.CommentsScreen
import com.example.vknewsclient.ui.theme.screen.home.HomeScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navigateState = rememberNavigationState()
    val commentsToPost: MutableState<FeedPost?> = remember {
        mutableStateOf(null)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navigateState.navHostController.currentBackStackEntryAsState()

                val items = listOf(
                    NavigationItem.Home,
                    NavigationItem.Favourite,
                    NavigationItem.Profile
                )

                items.forEach { item ->
                    val selected = navBackStackEntry?.destination?.hierarchy?.any {
                        it.route == item.screen.route
                    } ?: false

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navigateState.navigateTo(item.screen.route)
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = null)
                        },
                        label = {
                            Text(text = stringResource(id = item.titleResId))
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSecondary,
                            indicatorColor = MaterialTheme.colorScheme.secondary
                        )
                    )
                }
            }
        },
        content = {
            AppNavGraph(
                navHostController = navigateState.navHostController,
                newsFeedScreenContent = {
                    HomeScreen(
                        onCommentsClickListener = {
                            commentsToPost.value = it
                            navigateState.navigateToComments()
                        }
                    )
                },
                commentsScreenContent = {
                    CommentsScreen(commentsToPost.value!!) {
                        navigateState.navHostController.popBackStack()
                    }
                    BackHandler {
                        navigateState.navHostController.popBackStack()
                    }
                },
                favouriteScreenContent = { Text(text = "Favourite", color = Color.Blue) },
                profileScreenContent = { Text(text = "Profile", color = Color.Blue) }
            )
        }
    )
}