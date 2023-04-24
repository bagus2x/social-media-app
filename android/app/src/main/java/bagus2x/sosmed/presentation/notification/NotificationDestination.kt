package bagus2x.sosmed.presentation.notification

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import bagus2x.sosmed.presentation.common.Destination

@OptIn(ExperimentalAnimationApi::class)
object NotificationScreen : Destination(
    authority = "notification",
    screen = { _, navHostController ->
        NotificationScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)
