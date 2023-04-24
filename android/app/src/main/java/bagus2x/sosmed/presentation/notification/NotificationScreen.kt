package bagus2x.sosmed.presentation.notification

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Notification
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.feed.FeedDetailScreen
import bagus2x.sosmed.presentation.notification.components.Notification
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notifications = viewModel.notifications.collectAsLazyPagingItems()
    NotificationScreen(
        stateProvider = { state },
        notifications = notifications,
        snackbarConsumed = viewModel::snackbarConsumed,
        navigateToFeedDetail = { feedId ->
            navController.navigate(FeedDetailScreen(feedId))
        }
    )
}

@Composable
fun NotificationScreen(
    stateProvider: () -> NotificationState,
    notifications: LazyPagingItems<Notification>,
    snackbarConsumed: () -> Unit,
    navigateToFeedDetail: (Long) -> Unit
) {
    val showSnackbar = LocalShowSnackbar.current
    LaunchedEffect(Unit) {
        snapshotFlow { stateProvider() }.collectLatest { state ->
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
                elevation = 2.dp,
                title = {
                    Text(text = stringResource(R.string.text_notification))
                }
            )
        },
        modifier = Modifier.systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = notifications,
                key = Notification::id
            ) { notification ->
                if (notification != null) {
                    Notification(
                        notification = notification,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (notification.type is Notification.Type.FeedLiked) {
                                navigateToFeedDetail(notification.type.feedId)
                            }
                        }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

