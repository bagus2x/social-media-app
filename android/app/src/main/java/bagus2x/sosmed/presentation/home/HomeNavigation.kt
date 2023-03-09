package bagus2x.sosmed.presentation.home

import androidx.hilt.navigation.compose.hiltViewModel
import bagus2x.sosmed.presentation.common.Destination

object HomeScreen : Destination(
    authority = "home",
    screen = { _, navHostController ->
        HomeScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    },
)
