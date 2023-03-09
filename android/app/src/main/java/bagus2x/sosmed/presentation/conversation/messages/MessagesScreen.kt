package bagus2x.sosmed.presentation.conversation.messages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import bagus2x.sosmed.domain.model.Message
import bagus2x.sosmed.presentation.common.LocalAuthenticatedUser
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.conversation.ChatDetailScreen
import bagus2x.sosmed.presentation.conversation.chatdetail.components.MessagesTopBar
import bagus2x.sosmed.presentation.conversation.messages.components.Message
import bagus2x.sosmed.presentation.conversation.messages.components.MessageBox
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import java.time.format.DateTimeFormatter

@Composable
fun MessagesScreen(
    navController: NavController,
    viewModel: MessagesViewModel
) {
    val messages = viewModel.messages.collectAsLazyPagingItems()
    val state by viewModel.state.collectAsStateWithLifecycle()
    MessagesScreen(
        messages = messages,
        stateProvider = { state },
        setDescription = viewModel::setDescription,
        send = viewModel::send,
        consumeSnackbar = viewModel::consumeSnackbar,
        navigateToChatDetailScreen = {
            navController.navigate(ChatDetailScreen())
        },
        navigateUp = navController::navigateUp
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun MessagesScreen(
    stateProvider: () -> MessagesState,
    messages: LazyPagingItems<Message>,
    setDescription: (String) -> Unit,
    send: () -> Unit,
    consumeSnackbar: () -> Unit,
    navigateToChatDetailScreen: () -> Unit,
    navigateUp: () -> Unit
) {
    val showSnackbar = LocalShowSnackbar.current
    val state = stateProvider()
    LaunchedEffect(Unit) {
        snapshotFlow { stateProvider() }.collectLatest { state ->
            if (state.snackbar.isNotBlank()) {
                showSnackbar(state.snackbar)
                consumeSnackbar()
            }
        }
    }
    val lazyListState = rememberLazyListState()
    Scaffold(
        topBar = {
            if (state.chatState.chat != null) {
                MessagesTopBar(
                    chat = state.chatState.chat,
                    onBackClicked = navigateUp,
                    onMoreVertClicked = {},
                    onChatClicked = navigateToChatDetailScreen
                )
            }
        },
        modifier = Modifier.systemBarsPadding(),
        bottomBar = {
            MessageBox(
                description = state.messageState.description,
                onDescriptionChange = setDescription,
                onGalleryClicked = { },
                onCameraClicked = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                loading = state.messageState.loading,
                onSendClicked = send
            )
        }
    ) {
        val authUser = LocalAuthenticatedUser.current
        LaunchedEffect(Unit) {
            snapshotFlow {
                if (messages.itemCount > 0) {
                    val message = messages[0]
                    if (message != null && message.sender.id == authUser?.id) message
                    else null
                } else {
                    null
                }
            }
                .filterNotNull()
                .distinctUntilChanged()
                .collectLatest {
                    lazyListState.animateScrollToItem(0)
                }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imeNestedScroll(),
            reverseLayout = true,
            state = lazyListState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
        ) {
            itemsIndexed(
                items = messages,
                key = { _, message -> message.id }
            ) { index, message ->
                if (message != null) {
                    val own = message.sender.id == authUser?.id
                    Column(
                        modifier = Modifier
                            .animateItemPlacement()
                            .fillMaxWidth(),
                    ) {
                        val date = rememberSaveable {
                            getDateIndicator(
                                prevMessage = runCatching { messages[index + 1] }.getOrNull(),
                                message = message,
                            )
                        }
                        if (date.isNotBlank()) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        Message(
                            message = message,
                            own = own,
                            modifier = Modifier.align(if (own) Alignment.End else Alignment.Start)
                        )
                    }
                }
            }
        }
        if (state.chatState.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

private val Formatter by lazy { DateTimeFormatter.ofPattern("dd MMM yyyy") }

private fun getDateIndicator(
    prevMessage: Message?,
    message: Message,
): String {
    return if (message.createdAt.toLocalDate() != prevMessage?.createdAt?.toLocalDate())
        Formatter.format(message.createdAt)
    else
        ""
}

