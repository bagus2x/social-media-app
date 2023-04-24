package bagus2x.sosmed.presentation.conversation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import bagus2x.sosmed.presentation.common.Destination
import bagus2x.sosmed.presentation.conversation.chat.ChatScreen
import bagus2x.sosmed.presentation.conversation.chatdetail.ChatDetailScreen
import bagus2x.sosmed.presentation.conversation.messages.MessagesScreen
import bagus2x.sosmed.presentation.conversation.newchat.NewChatScreen
import bagus2x.sosmed.presentation.conversation.newchat.NewGroupChatScreen

@OptIn(ExperimentalAnimationApi::class)
object ChatScreen : Destination(
    authority = "chat",
    screen = { _, navHostController ->
        ChatScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

@OptIn(ExperimentalAnimationApi::class)
object MessagesScreen : Destination(
    authority = "messages",
    arguments = listOf(navArgument("chat_id") { type = NavType.LongType }),
    screen = { _, navHostController ->
        MessagesScreen(
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

    operator fun invoke(chatId: Long) = buildRoute("chat_id" to "$chatId")
}

@OptIn(ExperimentalAnimationApi::class)
object NewChatScreen : Destination(
    authority = "new_chat",
    screen = { navBackStackEntry, navHostController ->
        val navigationGraphEntry = remember(navBackStackEntry) {
            navHostController.getBackStackEntry("new_chat")
        }
        NewChatScreen(
            navController = navHostController,
            viewModel = hiltViewModel(navigationGraphEntry)
        )
    },
    enterTransition = {
        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(500))
    }
)

@OptIn(ExperimentalAnimationApi::class)
object NewGroupChatScreen : Destination(
    authority = "new_group_chat",
    screen = { navBackStackEntry, navHostController ->
        val navigationGraphEntry = remember(navBackStackEntry) {
            navHostController.getBackStackEntry("new_chat")
        }
        NewGroupChatScreen(
            navController = navHostController,
            viewModel = hiltViewModel(navigationGraphEntry)
        )
    },
    enterTransition = {
        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(500))
    }
)

@OptIn(ExperimentalAnimationApi::class)
object ChatDetailScreen : Destination(
    authority = "chat-detail-screen",
    screen = { _, navHostController ->
        ChatDetailScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    },
    enterTransition = {
        slideIntoContainer(AnimatedContentScope.SlideDirection.Down, animationSpec = tween(500))
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentScope.SlideDirection.Up, animationSpec = tween(500))
    }
)
