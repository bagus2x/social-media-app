package bagus2x.sosmed.presentation.explore.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.components.Feed
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.components.TextField
import bagus2x.sosmed.presentation.common.isEmpty
import bagus2x.sosmed.presentation.explore.search.components.Jumbotron
import bagus2x.sosmed.presentation.explore.search.components.User
import bagus2x.sosmed.presentation.feed.FeedDetailScreen
import bagus2x.sosmed.presentation.user.ProfileScreen
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val users = viewModel.users.collectAsLazyPagingItems()
    val feeds = viewModel.feeds.collectAsLazyPagingItems()
    SearchScreen(
        stateProvider = { state },
        users = users,
        feeds = feeds,
        setQuery = viewModel::setQuery,
        navigateToProfileScreen = { user ->
            navController.navigate(ProfileScreen(user.id))
        },
        navigateToFeedDetailScreen = { feed ->
            navController.navigate(FeedDetailScreen(feed.id))
        },
        navigateUp = navController::navigateUp
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    stateProvider: () -> SearchState,
    users: LazyPagingItems<User>,
    feeds: LazyPagingItems<Feed>,
    setQuery: (String) -> Unit,
    navigateToProfileScreen: (User) -> Unit,
    navigateToFeedDetailScreen: (Feed) -> Unit,
    navigateUp: () -> Unit
) {
    val state = stateProvider()
    Scaffold(
        topBar = {
            TopAppBar(
                elevation = 2.dp,
                backgroundColor = MaterialTheme.colors.background
            ) {
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                TextField(
                    value = state.query,
                    onValueChange = setQuery,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(text = "Search on medsos")
                    }
                )
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close_outlined),
                        contentDescription = null
                    )
                }
            }
        },
        modifier = Modifier.systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val pagerState = rememberPagerState()
            val tabItems = stringArrayResource(R.array.text_search_tab_items)
            val scope = rememberCoroutineScope()
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = MaterialTheme.colors.background,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colors.primary
                    )
                }
            ) {
                tabItems.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = {
                            Text(text = title)
                        }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                pageCount = tabItems.size,
                modifier = Modifier.weight(1f)
            ) { index ->
                if (index == 0) {
                    Box {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                items = users,
                                key = User::id
                            ) { user ->
                                if (user != null) {
                                    User(
                                        user = user,
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = { navigateToProfileScreen(user) }
                                    )
                                }
                            }
                        }
                        if (users.loadState.refresh is LoadState.NotLoading && users.loadState.append.endOfPaginationReached) {
                            if (users.isEmpty() && state.query.isNotBlank()) {
                                Jumbotron(
                                    query = state.query,
                                    modifier = Modifier
                                        .padding(horizontal = 32.dp, vertical = 64.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
                if (index == 1) {
                    Box {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                items = feeds,
                                key = Feed::id
                            ) { feed ->
                                if (feed != null) {
                                    Feed(
                                        feed = feed,
                                        onImageClicked = { },
                                        onVideoClicked = { },
                                        onFavoriteClicked = { },
                                        onCommentClicked = { },
                                        onRepostClicked = { },
                                        onSendClicked = { },
                                        onFeedClicked = { navigateToFeedDetailScreen(feed) },
                                        onUrlClicked = { },
                                        onHashtagClicked = { },
                                        onMentionClicked = { }
                                    )
                                    Divider()
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(72.dp))
                            }
                        }
                        if (feeds.loadState.refresh is LoadState.NotLoading && feeds.loadState.append.endOfPaginationReached) {
                            if (feeds.isEmpty() && state.query.isNotBlank()) {
                                Jumbotron(
                                    query = state.query,
                                    modifier = Modifier
                                        .padding(horizontal = 32.dp, vertical = 64.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

