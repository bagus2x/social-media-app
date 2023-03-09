package bagus2x.sosmed.presentation.explore.trending

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Trending
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.explore.SearchScreen

@Composable
fun TrendingScreen(
    navController: NavController,
    viewModel: TrendingViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TrendingScreen(
        stateProvider = { state },
        navigateToSearchSheet = {
            navController.navigate(SearchScreen())
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrendingScreen(
    stateProvider: () -> TrendingState,
    navigateToSearchSheet: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background
            ) {
                Surface(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f),
                    shape = MaterialTheme.shapes.small,
                    onClick = navigateToSearchSheet,
                    color = MaterialTheme.colors.onBackground.copy(alpha = .06f),
                    contentColor = MaterialTheme.colors.onBackground.copy(alpha = .7f)
                ) {
                    Text(
                        text = "Search on medsos",
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = navigateToSearchSheet) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search_outlined),
                        contentDescription = null
                    )
                }
            }
        },
        modifier = Modifier.systemBarsPadding()
    ) {
        val state = stateProvider()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "What's happening?",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(
                items = state.trending,
                key = Trending::id
            ) { trending ->
                Surface(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            text = trending.name,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${trending.count} Mentioned",
                            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}

