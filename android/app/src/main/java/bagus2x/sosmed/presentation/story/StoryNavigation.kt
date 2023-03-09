package bagus2x.sosmed.presentation.story

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import bagus2x.sosmed.presentation.common.Destination
import bagus2x.sosmed.presentation.story.storydetail.StoryDetailScreen

@OptIn(ExperimentalAnimationApi::class)
object StoryDetailScreen : Destination(
    authority = "story_detail",
    arguments = listOf(navArgument("story_id") { type = NavType.LongType }),
    screen = { _, navController ->
        StoryDetailScreen(
            navController = navController,
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

    operator fun invoke(storyId: Long): String {
        return buildRoute("story_id" to storyId.toString())
    }
}
