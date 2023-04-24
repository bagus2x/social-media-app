package bagus2x.sosmed.presentation.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import bagus2x.sosmed.presentation.common.Destination

@OptIn(ExperimentalAnimationApi::class)
object HomeScreen : Destination(
    authority = "home",
    screen = { _, navHostController ->
        HomeScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    },
)
