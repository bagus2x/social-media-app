package bagus2x.sosmed.presentation.conversation.newchat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.components.TextField
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.conversation.ChatScreen
import bagus2x.sosmed.presentation.conversation.MessagesScreen
import bagus2x.sosmed.presentation.gallery.contract.MediaType
import bagus2x.sosmed.presentation.gallery.contract.SelectSingleMedia
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NewGroupChatScreen(
    navController: NavController,
    viewModel: NewChatViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val gallery = rememberLauncherForActivityResult(
        contract = SelectSingleMedia(),
        onResult = { media ->
            viewModel.setPhoto(media as? DeviceMedia.Image)
        }
    )
    NewGroupChatScreen(
        stateProvider = { state },
        setName = viewModel::setName,
        setPhoto = viewModel::setPhoto,
        createGroupChat = viewModel::createGroupChat,
        consumeSnackbar = viewModel::consumeSnackbar,
        navigateToMessagesScreen = { chat ->
            navController.navigate(MessagesScreen(chat.chatId)) {
                popUpTo(ChatScreen())
            }
        },
        navigateToGalleryScreen = {
            gallery.launch(MediaType.Image)
        },
        navigateUp = navController::navigateUp
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewGroupChatScreen(
    stateProvider: () -> NewChatState,
    setName: (String) -> Unit,
    setPhoto: (DeviceMedia.Image?) -> Unit,
    createGroupChat: () -> Unit,
    consumeSnackbar: () -> Unit,
    navigateToMessagesScreen: (Chat) -> Unit,
    navigateToGalleryScreen: () -> Unit,
    navigateUp: () -> Unit
) {
    val state = stateProvider()
    val showSnackbar = LocalShowSnackbar.current
    LaunchedEffect(Unit) {
        snapshotFlow { stateProvider() }.collectLatest { state ->
            if (state.createdChat != null) {
                // Group chat created
                navigateToMessagesScreen(state.createdChat)
            }
            if (state.snackbar.isNotBlank()) {
                showSnackbar(state.snackbar)
                consumeSnackbar()
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.background,
                elevation = 1.dp
            ) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close_outlined),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = createGroupChat,
                    modifier = Modifier.padding(end = 12.dp),
                    enabled = state.isFulfilled && !state.loading
                ) {
                    Text(text = stringResource(R.string.text_create))
                }
            }
        },
        modifier = Modifier.systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box {
                Image(
                    model = state.photo?.file,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { navigateToGalleryScreen() },
                )
                Surface(
                    onClick = { setPhoto(null) },
                    color = MaterialTheme.colors.background,
                    shape = CircleShape,
                    elevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete_outlined),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp),
                    )
                }
            }
            TextField(
                value = state.name,
                onValueChange = setName,
                modifier = Modifier
                    .widthIn(320.dp)
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = "Enter group name")
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent
                ),
                maxLines = 1,
                singleLine = true
            )
        }
        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
