package bagus2x.sosmed.presentation.user.profile

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.LocalAuthenticatedUser
import bagus2x.sosmed.presentation.common.components.Feed
import bagus2x.sosmed.presentation.explore.SearchSheet
import bagus2x.sosmed.presentation.user.EditProfileScreen
import bagus2x.sosmed.presentation.user.FollowersScreen
import bagus2x.sosmed.presentation.user.FollowingScreen
import bagus2x.sosmed.presentation.user.ProfileSettingsScreen
import bagus2x.sosmed.presentation.user.profile.components.ProfileTopBar
import bagus2x.sosmed.presentation.user.profile.components.rememberNestedScrollConnection
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val feeds = viewModel.feeds.collectAsLazyPagingItems()
    ProfileScreen(
        stateProvider = { state },
        feeds = feeds,
        followUser = viewModel::followUser,
        navigateUp = navController::navigateUp,
        navigateToSearchSheet = {
            navController.navigate(SearchSheet())
        },
        navigateToProfileMenuSheet = {
            navController.navigate(ProfileSettingsScreen())
        },
        navigateToMessagesScreen = {},
        navigateToEditProfileScreen = {
            navController.navigate(EditProfileScreen())
        },
        navigateToFollowersScreen = {
            navController.navigate(FollowersScreen(userid = viewModel.userId))
        },
        navigateToFollowingScreen = {
            navController.navigate(FollowingScreen(userid = viewModel.userId))
        }
    )
}

enum class SwipingStates {
    EXPANDED, COLLAPSED
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ProfileScreen(
    stateProvider: () -> ProfileState,
    feeds: LazyPagingItems<Feed>,
    followUser: (User) -> Unit,
    navigateUp: () -> Unit,
    navigateToSearchSheet: () -> Unit,
    navigateToProfileMenuSheet: () -> Unit,
    navigateToEditProfileScreen: () -> Unit,
    navigateToMessagesScreen: () -> Unit,
    navigateToFollowersScreen: () -> Unit,
    navigateToFollowingScreen: () -> Unit
) {
    val density = LocalDensity.current
    val statusBarHeightInPx = WindowInsets.statusBars.getTop(density)
    val statusBarBackground = MaterialTheme.colors.background
    BoxWithConstraints(
        modifier = Modifier.drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(
                    color = statusBarBackground,
                    topLeft = Offset(x = 0f, y = 0f),
                    size = Size(width = size.width, height = statusBarHeightInPx.toFloat())
                )
            }
        }
    ) {
        val state = stateProvider()
        val swipingState = rememberSwipeableState(
            initialValue = SwipingStates.EXPANDED,
            animationSpec = tween(easing = LinearEasing),
        )
        val heightInPx = with(density) { maxHeight.toPx() }
        val nestedScrollConnection = rememberNestedScrollConnection(swipingState)
        Column(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize()
                .swipeable(
                    state = swipingState,
                    thresholds = { _, _ ->
                        FractionalThreshold(.5f)
                    },
                    orientation = Orientation.Vertical,
                    anchors = mapOf(
                        0f to SwipingStates.COLLAPSED,
                        heightInPx to SwipingStates.EXPANDED,
                    ),
                )
                .nestedScroll(nestedScrollConnection)
        ) {
            val computedProgress by remember {
                derivedStateOf {
                    if (swipingState.progress.to == SwipingStates.COLLAPSED) swipingState.progress.fraction
                    else 1f - swipingState.progress.fraction
                }
            }
            if (state.user != null) {
                ProfileTopBar(
                    user = state.user,
                    own = LocalAuthenticatedUser.current?.id == state.user.id,
                    onBackClicked = navigateUp,
                    onSettingClicked = navigateToProfileMenuSheet,
                    onSearchClicked = navigateToSearchSheet,
                    onMessageClicked = navigateToMessagesScreen,
                    onFollowClicked = { followUser(state.user) },
                    onEditClicked = navigateToEditProfileScreen,
                    onFollowingClicked = navigateToFollowingScreen,
                    onFollowersClicked = navigateToFollowersScreen,
                    modifier = Modifier.fillMaxWidth(),
                    progressProvider = { computedProgress },
                )
            }
            val pagerState = rememberPagerState()
            val tabItems = stringArrayResource(R.array.text_profile_tab_items)
            val scope = rememberCoroutineScope()
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                backgroundColor = MaterialTheme.colors.background,
                edgePadding = 0.dp,
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
                                    onFeedClicked = { },
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
                }
                if (index == 1) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(1000) {
                            Text(text = "B $it")
                        }
                    }
                }
                if (index == 2) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(1000) {
                            Text(text = "C $it")
                        }
                    }
                }
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}


