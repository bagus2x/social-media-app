package bagus2x.sosmed.presentation.conversation.newchat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.conversation.ChatScreen
import bagus2x.sosmed.presentation.conversation.MessagesScreen
import bagus2x.sosmed.presentation.conversation.NewChatScreen
import bagus2x.sosmed.presentation.conversation.NewGroupChatScreen
import bagus2x.sosmed.presentation.conversation.newchat.components.User
import bagus2x.sosmed.presentation.explore.SearchScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NewChatScreen(
    navController: NavController,
    viewModel: NewChatViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val users = viewModel.users.collectAsLazyPagingItems()
    NewChatScreen(
        stateProvider = { state },
        users = users,
        selectUser = viewModel::selectUser,
        unselectUser = viewModel::unselectUser,
        navigateUp = navController::navigateUp,
        consumeSnackbar = viewModel::consumeSnackbar,
        createPrivateChat = viewModel::createPrivateChat,
        navigateToNewGroupChatScreen = {
            navController.navigate(NewGroupChatScreen())
        },
        navigateToMessagesScreen = { chat ->
            navController.navigate(MessagesScreen(chat.chatId)) {
                popUpTo(ChatScreen())
            }
        },
        navigateToSearchScreen = {
            navController.navigate(SearchScreen()) {
                popUpTo(NewChatScreen()) {
                    inclusive = true
                }
            }
        }
    )
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun NewChatScreen(
    stateProvider: () -> NewChatState,
    users: LazyPagingItems<User>,
    selectUser: (User) -> Unit,
    unselectUser: (User) -> Unit,
    navigateUp: () -> Unit,
    consumeSnackbar: () -> Unit,
    createPrivateChat: (User) -> Unit,
    navigateToNewGroupChatScreen: () -> Unit,
    navigateToMessagesScreen: (Chat) -> Unit,
    navigateToSearchScreen: () -> Unit
) {
    val showSnackbar = LocalShowSnackbar.current
    val state = stateProvider()
    LaunchedEffect(Unit) {
        snapshotFlow { stateProvider() }.collectLatest { state ->
            if (state.createdChat != null) {
                // Private chat created
                navigateToMessagesScreen(state.createdChat)
            }
            if (state.snackbar.isNotBlank()) {
                showSnackbar(state.snackbar)
                consumeSnackbar()
            }
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { users.loadState to users.itemSnapshotList }.collectLatest { (loadState, snapshot) ->
            if (loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached) {
                if (snapshot.size == 0) {
                    showSnackbar("Find your friend first to start conversations")
                    delay(1000)
                    navigateToSearchScreen()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                elevation = 1.dp,
                title = {
                    Text(text = "Add new chat")
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left_outlined),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (state.isGroupChat) {
                        Button(
                            onClick = navigateToNewGroupChatScreen,
                            modifier = Modifier.padding(end = 12.dp),
                            enabled = state.membersWithoutAuthUser.isNotEmpty()
                        ) {
                            Text(text = stringResource(R.string.text_next))
                        }
                    }
                }
            )
        },
        modifier = Modifier.systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.isGroupChat) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.members.forEach { user ->
                        Chip(
                            onClick = { unselectUser(user) },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = MaterialTheme.colors.primary.copy(alpha = .2f)
                            ),
                            enabled = user.id == state.authUser?.id && state.members.size == 1 || user.id != state.authUser?.id && state.members.size > 1
                        ) {
                            Text(text = if (user == state.authUser) stringResource(R.string.text_me) else user.username)
                        }
                    }
                }
            } else {
                TextButton(
                    onClick = { state.authUser?.let(selectUser) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_people_outlined),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Group Chat")
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(
                    items = users,
                    key = User::id
                ) { user ->
                    if (user != null) {
                        val isSelected = user in state.members && state.isGroupChat
                        User(
                            user = user,
                            selected = isSelected,
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement(),
                            onClick = {
                                if (state.isPrivateChat) {
                                    createPrivateChat(user)
                                } else {
                                    if (isSelected) unselectUser(user)
                                    else selectUser(user)
                                }
                            },
                            onLongClick = {
                                state.authUser?.let { selectUser(it) }
                                selectUser(user)
                            }
                        )
                    }
                }
            }
        }
    }
}
