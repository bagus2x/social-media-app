package bagus2x.sosmed.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.model.Story
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.components.Feed
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.isEmptyAndNotLoading
import bagus2x.sosmed.presentation.common.rememberLazyListState
import bagus2x.sosmed.presentation.explore.SearchScreen
import bagus2x.sosmed.presentation.feed.FeedDetailScreen
import bagus2x.sosmed.presentation.feed.MediaDetailScreen
import bagus2x.sosmed.presentation.feed.NewFeedScreen
import bagus2x.sosmed.presentation.home.components.HomeTopBar
import bagus2x.sosmed.presentation.home.components.Jumbotron
import bagus2x.sosmed.presentation.home.components.Story
import bagus2x.sosmed.presentation.story.StoryDetailScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    val feeds = viewModel.feeds.collectAsLazyPagingItems()
    HomeScreen(
        stateProvider = { state },
        stories = stories,
        feeds = feeds,
        consumeSnackbar = viewModel::consumeSnackbar,
        favoriteFeed = viewModel::favoriteFeed,
        navigateToStoriesDetailScreen = { story ->
            navController.navigate(StoryDetailScreen(story.id))
        },
        navigateToNewPostScreen = {
            navController.navigate(NewFeedScreen())
        },
        navigateToFeedDetailScreen = { feed ->
            navController.navigate(FeedDetailScreen(feedId = feed.id))
        },
        navigateToMediaDetailScreen = { post, media ->
            val index = post.medias.indexOf(media)
            navController.navigate(MediaDetailScreen(feedId = post.id, mediaIndex = index))
        },
        navigateToSearchScreen = {
            navController.navigate(SearchScreen())
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    stateProvider: () -> HomeState,
    stories: List<Story>,
    feeds: LazyPagingItems<Feed>,
    consumeSnackbar: () -> Unit,
    favoriteFeed: (Feed) -> Unit,
    navigateToStoriesDetailScreen: (Story) -> Unit,
    navigateToNewPostScreen: () -> Unit,
    navigateToFeedDetailScreen: (Feed) -> Unit,
    navigateToMediaDetailScreen: (Feed, Media) -> Unit,
    navigateToSearchScreen: () -> Unit
) {
    val showSnackbar = LocalShowSnackbar.current
    LaunchedEffect(Unit) {
        snapshotFlow { stateProvider() }.collect { state ->
            if (state.snackbar.isNotBlank()) {
                showSnackbar(state.snackbar)
                consumeSnackbar()
            }
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { feeds.loadState to feeds.itemSnapshotList }.collectLatest { (loadState, snapshotList) ->
            if (loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached) {
                while (snapshotList.size == 0) {
                    delay(1000)
                    feeds.refresh()
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            HomeTopBar(
                onAddClicked = navigateToNewPostScreen
            )
        }
    ) {
        val lazyColumnState = feeds.rememberLazyListState()
        val pullRefreshState = rememberPullRefreshState(
            refreshing = feeds.loadState.refresh is LoadState.Loading,
            onRefresh = feeds::refresh
        )
        val uriHandler = LocalUriHandler.current
        LazyColumn(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            state = lazyColumnState
        ) {
            item(key = "stories") {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(
                        items = stories,
                        key = Story::id
                    ) { story ->
                        Story(
                            story = story,
                            onClick = { navigateToStoriesDetailScreen(story) },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
                Divider(color = MaterialTheme.colors.onBackground.copy(.05f))
            }
            items(
                items = feeds,
                key = Feed::id
            ) { feed ->
                if (feed != null) {
                    Feed(
                        feed = feed,
                        onImageClicked = { image ->
                            navigateToMediaDetailScreen(feed, image)
                        },
                        onVideoClicked = { video ->
                            navigateToMediaDetailScreen(feed, video)
                        },
                        onFavoriteClicked = { favoriteFeed(feed) },
                        onCommentClicked = {
                            navigateToFeedDetailScreen(feed)
                        },
                        onRepostClicked = { },
                        onSendClicked = {},
                        onFeedClicked = {
                            navigateToFeedDetailScreen(feed)
                        },
                        onUrlClicked = uriHandler::openUri,
                        onHashtagClicked = {},
                        onMentionClicked = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
                    )
                    Divider()
                }
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
        PullRefreshIndicator(
            refreshing = true,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        if (feeds.isEmptyAndNotLoading()) {
            Jumbotron(
                onClick = navigateToSearchScreen,
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 64.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
