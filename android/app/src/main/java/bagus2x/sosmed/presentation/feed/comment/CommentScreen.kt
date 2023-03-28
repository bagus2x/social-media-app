package bagus2x.sosmed.presentation.feed.comment

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.feed.CommentScreen
import bagus2x.sosmed.presentation.feed.components.Comment
import bagus2x.sosmed.presentation.feed.components.CommentBox

@Composable
fun CommentScreen(
    navController: NavController,
    viewModel: CommentViewModel
) {
    val state by viewModel.state.collectAsState()
    val comments = viewModel.comments.collectAsLazyPagingItems()
    CommentScreen(
        stateProvider = { state },
        comments = comments,
        setCommentToBeReplied = viewModel::setCommentToBeReplied,
        cancelComment = viewModel::cancelComment,
        loadReplies = viewModel::loadReplies,
        setDescription = viewModel::setDescription,
        setMedias = viewModel::setMedias,
        sendComment = viewModel::sendComment,
        snackbarConsumed = viewModel::snackbarConsumed,
        navigateToCommentScreen = { comment ->
            navController.navigate(CommentScreen(comment.id))
        },
        navigateUp = navController::navigateUp
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun CommentScreen(
    stateProvider: () -> CommentState,
    comments: LazyPagingItems<Comment>,
    setCommentToBeReplied: (Comment) -> Unit,
    cancelComment: () -> Unit,
    loadReplies: (Comment) -> Unit,
    setDescription: (String) -> Unit,
    setMedias: (List<DeviceMedia>) -> Unit,
    sendComment: () -> Unit,
    snackbarConsumed: () -> Unit,
    navigateToCommentScreen: (Comment) -> Unit,
    navigateUp: () -> Unit
) {
    val showSnackbar = LocalShowSnackbar.current
    LaunchedEffect(Unit) {
        snapshotFlow {
            stateProvider()
        }.collect { state ->
            if (state.snackbar.isNotBlank()) {
                showSnackbar(state.snackbar)
                snackbarConsumed()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp,
                title = {
                    Text(text = stringResource(R.string.text_comments))
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left_outlined),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column {
            val state = stateProvider()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .imeNestedScroll()
                    .weight(1f),
            ) {
                item {
                    val comment = state.parentComment ?: return@item
                    Comment(
                        comment = comment,
                        indentation = 0,
                        showMoreVisible = false,
                        onCommentClicked = { },
                        onReplyClicked = { },
                        onShowMoreClicked = { },
                        onUrlClicked = { },
                        onHashtagClicked = { },
                        onMentionClicked = { },
                        modifier = Modifier.animateItemPlacement()
                    )
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
                            onHashtagClicked = { },
                            onMentionClicked = { },
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
            CommentBox(
                description = state.description,
                setDescription = setDescription,
                commentToBeReplied = state.commentToBeReplied,
                cancelComment = cancelComment,
                loading = state.loading,
                onGalleryClicked = { /*TODO*/ },
                onCameraClicked = { /*TODO*/ },
                onSendClicked = sendComment,
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                profile = null
            )
        }
    }
}
