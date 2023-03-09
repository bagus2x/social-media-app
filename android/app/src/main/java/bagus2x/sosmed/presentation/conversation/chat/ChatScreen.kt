package bagus2x.sosmed.presentation.conversation.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import bagus2x.sosmed.R.drawable
import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.isEmpty
import bagus2x.sosmed.presentation.conversation.MessagesScreen
import bagus2x.sosmed.presentation.conversation.NewChatScreen
import bagus2x.sosmed.presentation.conversation.chat.components.Chat
import bagus2x.sosmed.presentation.conversation.chat.components.Jumbotron

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel
) {
    val chats = viewModel.chats.collectAsLazyPagingItems()
    ChatScreen(
        chats = chats,
        navigateToMessagesScreen = { chat ->
            navController.navigate(MessagesScreen(chat.chatId))
        },
        navigateToNewChatScreen = {
            navController.navigate(NewChatScreen())
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    chats: LazyPagingItems<Chat>,
    navigateToMessagesScreen: (Chat) -> Unit,
    navigateToNewChatScreen: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                elevation = 2.dp,
                title = {
                    Text(text = "Chats")
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(drawable.ic_search_outlined),
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToNewChatScreen,
                modifier = Modifier.padding(bottom = 56.dp),
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(
                    painter = painterResource(drawable.ic_add_outlined),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.systemBarsPadding(),
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = chats,
                key = Chat::chatId
            ) { chat ->
                if (chat != null) {
                    Chat(
                        chat = chat,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        onClick = { navigateToMessagesScreen(chat) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
        if (chats.isEmpty()) {
            Jumbotron(
                onClick = navigateToNewChatScreen,
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 64.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
