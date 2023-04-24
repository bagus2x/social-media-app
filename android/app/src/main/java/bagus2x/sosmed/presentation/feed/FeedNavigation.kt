@file:OptIn(ExperimentalAnimationApi::class)

package bagus2x.sosmed.presentation.feed

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import bagus2x.sosmed.presentation.common.Destination
import bagus2x.sosmed.presentation.feed.comment.CommentScreen
import bagus2x.sosmed.presentation.feed.feeddetail.FeedDetailScreen
import bagus2x.sosmed.presentation.feed.mediadetail.MediaDetailScreen
import bagus2x.sosmed.presentation.feed.newfeed.NewFeedScreen

@OptIn(ExperimentalAnimationApi::class)
object NewFeedScreen : Destination(
    authority = "new_feed",
    screen = { _, navHostController ->
        NewFeedScreen(
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
)

@OptIn(ExperimentalAnimationApi::class)
object MediaDetailScreen : Destination(
    authority = "media_detail",
    arguments = listOf(
        navArgument("feed_id") { type = NavType.LongType },
        navArgument("media_index") { type = NavType.IntType },
    ),
    screen = { _, navHostController ->
        MediaDetailScreen(
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

    operator fun invoke(feedId: Long, mediaIndex: Int): String {
        return buildRoute("feed_id" to feedId, "media_index" to mediaIndex)
    }
}

@OptIn(ExperimentalAnimationApi::class)
object FeedDetailScreen : Destination(
    authority = "feed_detail",
    arguments = listOf(navArgument("feed_id") { type = NavType.LongType }),
    screen = { _, navHostController ->
        FeedDetailScreen(
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

    operator fun invoke(feedId: Long): String {
        return buildRoute("feed_id" to feedId)
    }
}

@OptIn(ExperimentalAnimationApi::class)
object CommentScreen : Destination(
    authority = "comment_screen",
    arguments = listOf(navArgument("parent_id") { type = NavType.LongType }),
    screen = { _, navHostController ->
        CommentScreen(
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

    operator fun invoke(parentId: Long): String {
        return buildRoute("parent_id" to parentId)
    }
}
