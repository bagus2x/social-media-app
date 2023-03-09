package bagus2x.sosmed.presentation.feed.feeddetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.rememberLazyListState
import bagus2x.sosmed.presentation.feed.CommentScreen
import bagus2x.sosmed.presentation.feed.MediaDetailScreen
import bagus2x.sosmed.presentation.feed.components.Comment
import bagus2x.sosmed.presentation.feed.components.CommentBox
import bagus2x.sosmed.presentation.feed.feeddetail.components.FeedDetail
import bagus2x.sosmed.presentation.feed.feeddetail.components.FeedDetailTopBar

@Composable
fun FeedDetailScreen(
    navController: NavController,
    viewModel: FeedDetailViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val comments = viewModel.comments.collectAsLazyPagingItems()
    FeedDetailScreen(
        stateProvider = { state },
        snackbarConsumed = viewModel::snackbarConsumed,
        comments = comments,
        favoriteFeed = viewModel::favoriteFeed,
        setDescription = viewModel::setDescription,
        createComment = viewModel::createComment,
        navigateUp = navController::navigateUp,
        setCommentToBeReplied = viewModel::setCommentToBeReplied,
        cancelComment = viewModel::cancelComment,
        loadReplies = viewModel::loadReplies,
        navigateToMediaDetailScreen = { feed, media ->
            val index = feed.medias.indexOf(media)
            navController.navigate(MediaDetailScreen(feedId = feed.id, mediaIndex = index))
        },
        navigateToCommentScreen = { comment ->
            navController.navigate(CommentScreen(comment.id))
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun FeedDetailScreen(
    stateProvider: () -> FeedDetailState,
    snackbarConsumed: () -> Unit,
    comments: LazyPagingItems<Comment>,
    favoriteFeed: (Feed) -> Unit,
    setDescription: (String) -> Unit,
    createComment: () -> Unit,
    navigateUp: () -> Unit,
    setCommentToBeReplied: (Comment) -> Unit,
    cancelComment: () -> Unit,
    loadReplies: (Comment) -> Unit,
    navigateToMediaDetailScreen: (Feed, Media) -> Unit,
    navigateToCommentScreen: (Comment) -> Unit,
) {
    val state = stateProvider()
    val localShowSnackbar = LocalShowSnackbar.current
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        snapshotFlow {
            stateProvider()
        }.collect { state ->
            if (state.snackbar.isNotBlank()) {
                localShowSnackbar(state.snackbar)
                snackbarConsumed()
            }
        }
    }
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            val feed = state.feedState.feed
            if (feed != null) {
                FeedDetailTopBar(
                    feed = feed,
                    onBackClicked = navigateUp,
                    onMoreVertClicked = { }
                )
            }
        },
        bottomBar = {
            CommentBox(
                description = state.commentState.description,
                setDescription = setDescription,
                commentToBeReplied = state.commentState.commentToBeReplied,
                cancelComment = cancelComment,
                loading = state.commentState.loading,
                onGalleryClicked = { },
                onCameraClicked = { },
                onSendClicked = createComment,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                focusRequester = focusRequester
            )
        }
    ) {
        val uriHandler = LocalUriHandler.current
        val lazyListState = comments.rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imeNestedScroll(),
            state = lazyListState,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            val feed = state.feedState.feed
            if (feed != null) {
                item(key = "feed") {
                    FeedDetail(
                        feed = feed,
                        onImageClicked = { image ->
                            navigateToMediaDetailScreen(feed, image)
                        },
                        onVideoClicked = { video ->
                            navigateToMediaDetailScreen(feed, video)
                        },
                        onFavoriteClicked = { favoriteFeed(feed) },
                        onCommentClicked = { focusRequester.requestFocus() },
                        onRepostClicked = { },
                        onSendClicked = { },
                        onUrlClicked = { uriHandler.openUri(it) },
                        onHashtagClicked = { },
                        onMentionClicked = { }
                    )
                }
            }
            items(
                items = comments,
                key = Comment::id
            ) { comment ->
                if (comment != null) {
                    Comment(
                        comment = comment,
                        indentation = comment.pathSize - 1,
                        showMoreVisible = comment.totalLoadedReplies == 0 && comment.totalReplies != 0,
                        onCommentClicked = { },
                        onReplyClicked = { setCommentToBeReplied(comment) },
                        onShowMoreClicked = { loadReplies(comment) },
                        onUrlClicked = { },
                        onHashtagClicked = {},
                        onMentionClicked = {},
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}
