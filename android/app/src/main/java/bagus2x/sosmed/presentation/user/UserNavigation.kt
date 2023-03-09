package bagus2x.sosmed.presentation.user

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.navArgument
import bagus2x.sosmed.presentation.common.Destination
import bagus2x.sosmed.presentation.user.edit.EditProfileScreen
import bagus2x.sosmed.presentation.user.followers.FollowersScreen
import bagus2x.sosmed.presentation.user.following.FollowingScreen
import bagus2x.sosmed.presentation.user.settings.ProfileSettingsScreen
import bagus2x.sosmed.presentation.user.profile.ProfileScreen

@OptIn(ExperimentalAnimationApi::class)
object ProfileScreen : Destination(
    authority = "profile",
    arguments = listOf(navArgument("user_id") { nullable = true }),
    screen = { _, navHostController ->
        ProfileScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    },
    enterTransition = {
        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(500))
    }
) {
    fun getUserId(savedStateHandle: SavedStateHandle): Long? {
        return savedStateHandle.get<String>("user_id")?.toLong()
    }

    operator fun invoke(userid: Long): String {
        return buildRoute("user_id" to userid)
    }
}

@OptIn(ExperimentalAnimationApi::class)
object MyProfileScreen : Destination(
    authority = "my_profile",
    screen = { _, navHostController ->
        ProfileScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

@OptIn(ExperimentalAnimationApi::class)
object EditProfileScreen : Destination(
    authority = "edit_profile",
    enterTransition = {
        slideIntoContainer(AnimatedContentScope.SlideDirection.Up, animationSpec = tween(500))
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(500))
    },
    screen = { _, navHostController ->
        EditProfileScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

object ProfileSettingsScreen : Destination(
    authority = "profile-menu-sheet",
    screen = { _, navHostController ->
        ProfileSettingsScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

@OptIn(ExperimentalAnimationApi::class)
object FollowersScreen : Destination(
    authority = "followers-screen",
    arguments = listOf(navArgument("user_id") { nullable = true }),
    screen = { _, navHostController ->
        FollowersScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    },
    enterTransition = {
        slideIntoContainer(AnimatedContentScope.SlideDirection.Up, animationSpec = tween(500))
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(500))
    }
) {
    fun getUserId(savedStateHandle: SavedStateHandle): Long? {
        return savedStateHandle.get<String>("user_id")?.toLong()
    }

    operator fun invoke(userid: Long?): String {
        if (userid == null) {
            return this()
        }
        return buildRoute("user_id" to userid)
    }
}

@OptIn(ExperimentalAnimationApi::class)
object FollowingScreen : Destination(
    authority = "following-screen",
    arguments = listOf(navArgument("user_id") { nullable = true }),
    screen = { _, navHostController ->
        FollowingScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    },
    enterTransition = {
        slideIntoContainer(AnimatedContentScope.SlideDirection.Up, animationSpec = tween(500))
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(500))
    }
) {
    fun getUserId(savedStateHandle: SavedStateHandle): Long? {
        return savedStateHandle.get<String>("user_id")?.toLong()
    }

    operator fun invoke(userid: Long?): String {
        if (userid == null) {
            return this()
        }
        return buildRoute("user_id" to userid)
    }
}
