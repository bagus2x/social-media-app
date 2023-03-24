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
    authStateProvider: () -> AuthState
) {
    AnimatedNavHost(
        navController = navHostController,
        startDestination = HomeScreen(),
        modifier = modifier
    ) {
        HomeScreen.composable(navHostController, authStateProvider)
        TrendingScreen.composable(navHostController, authStateProvider)
        SearchScreen.composable(navHostController, authStateProvider)
        SearchSheet.bottomSheet(navHostController, authStateProvider)
        NotificationScreen.composable(navHostController, authStateProvider)
        NewFeedScreen.composable(navHostController, authStateProvider)
        ProfileScreen.composable(navHostController, authStateProvider)
        ProfileSettingsScreen.composable(navHostController, authStateProvider)
        MyProfileScreen.composable(navHostController, authStateProvider)
        EditProfileScreen.composable(navHostController, authStateProvider)
        MediaDetailScreen.composable(navHostController, authStateProvider)
        FeedDetailScreen.composable(navHostController, authStateProvider)
        SignInScreen.composable(navHostController)
        SignUpScreen.composable(navHostController)
        StoryDetailScreen.composable(navHostController, authStateProvider)
        CommentScreen.composable(navHostController, authStateProvider)
        ChatScreen.composable(navHostController, authStateProvider)
        MessagesScreen.composable(navHostController, authStateProvider)
        navigation(
            route = "create_new_chat",
            startDestination = "new_chat"
        ) {
            NewChatScreen.composable(navHostController, authStateProvider)
            NewGroupChatScreen.composable(navHostController, authStateProvider)
        }
        ChatDetailScreen.composable(navHostController, authStateProvider)
        FollowersScreen.composable(navHostController, authStateProvider)
        FollowingScreen.composable(navHostController, authStateProvider)
    }
}
