package bagus2x.sosmed.presentation.explore

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.hilt.navigation.compose.hiltViewModel
import bagus2x.sosmed.presentation.common.Destination
import bagus2x.sosmed.presentation.explore.search.SearchScreen
import bagus2x.sosmed.presentation.explore.trending.TrendingScreen

@OptIn(ExperimentalAnimationApi::class)
object SearchScreen : Destination(
    authority = "search-screen",
    screen = { _, navHostController ->
        SearchScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

@OptIn(ExperimentalAnimationApi::class)
object SearchSheet : Destination(
    authority = "search-sheet",
    screen = { _, navHostController ->
        SearchScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

@OptIn(ExperimentalAnimationApi::class)
object TrendingScreen : Destination(
    authority = "trending-screen",
    screen = { _, navHostController ->
        TrendingScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

