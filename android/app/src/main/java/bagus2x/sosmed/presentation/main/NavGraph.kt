package bagus2x.sosmed.presentation.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import bagus2x.sosmed.presentation.auth.SignInScreen
import bagus2x.sosmed.presentation.auth.SignUpScreen
import bagus2x.sosmed.presentation.conversation.*
import bagus2x.sosmed.presentation.explore.SearchScreen
import bagus2x.sosmed.presentation.explore.SearchSheet
import bagus2x.sosmed.presentation.explore.TrendingScreen
import bagus2x.sosmed.presentation.feed.CommentScreen
import bagus2x.sosmed.presentation.feed.FeedDetailScreen
import bagus2x.sosmed.presentation.feed.MediaDetailScreen
import bagus2x.sosmed.presentation.feed.NewFeedScreen
import bagus2x.sosmed.presentation.home.HomeScreen
import bagus2x.sosmed.presentation.notification.NotificationScreen
import bagus2x.sosmed.presentation.story.StoryDetailScreen
import bagus2x.sosmed.presentation.user.*
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    authenticated: Boolean
) {
    AnimatedNavHost(
        navController = navHostController,
        startDestination = if (authenticated) HomeScreen() else SignInScreen(),
        modifier = modifier
    ) {
        HomeScreen.composable(navHostController)
        TrendingScreen.composable(navHostController)
        SearchScreen.composable(navHostController)
        SearchSheet.bottomSheet(navHostController)
        NotificationScreen.composable(navHostController)
        NewFeedScreen.composable(navHostController)
        ProfileScreen.composable(navHostController)
        ProfileSettingsScreen.composable(navHostController)
        MyProfileScreen.composable(navHostController)
        EditProfileScreen.composable(navHostController)
        MediaDetailScreen.composable(navHostController)
        FeedDetailScreen.composable(navHostController)
        SignInScreen.composable(navHostController)
        SignUpScreen.composable(navHostController)
        StoryDetailScreen.composable(navHostController)
        CommentScreen.composable(navHostController)
        ChatScreen.composable(navHostController)
        MessagesScreen.composable(navHostController)
        navigation(
            route = "create_new_chat",
            startDestination = "new_chat"
        ) {
            NewChatScreen.composable(navHostController)
            NewGroupChatScreen.composable(navHostController)
        }
        ChatDetailScreen.composable(navHostController)
        FollowersScreen.composable(navHostController)
        FollowingScreen.composable(navHostController)
    }
}
