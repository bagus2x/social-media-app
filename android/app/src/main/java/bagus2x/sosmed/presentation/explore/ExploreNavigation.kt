package bagus2x.sosmed.presentation.explore

import androidx.hilt.navigation.compose.hiltViewModel
import bagus2x.sosmed.presentation.common.Destination
import bagus2x.sosmed.presentation.explore.search.SearchScreen
import bagus2x.sosmed.presentation.explore.trending.TrendingScreen

object SearchScreen : Destination(
    authority = "search-screen",
    screen = { _, navHostController ->
        SearchScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

object SearchSheet : Destination(
    authority = "search-sheet",
    screen = { _, navHostController ->
        SearchScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

object TrendingScreen : Destination(
    authority = "trending-screen",
    screen = { _, navHostController ->
        TrendingScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

