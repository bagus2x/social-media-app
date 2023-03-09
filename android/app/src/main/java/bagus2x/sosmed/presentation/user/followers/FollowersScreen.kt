package bagus2x.sosmed.presentation.user.followers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.isEmptyAndNotLoading
import bagus2x.sosmed.presentation.user.ProfileScreen
import bagus2x.sosmed.presentation.user.component.FollowableUser
import bagus2x.sosmed.presentation.user.followers.components.Jumbotron

@Composable
fun FollowersScreen(
    navController: NavController,
    viewModel: FollowersViewModel
) {
    val users = viewModel.users.collectAsLazyPagingItems()
    FollowersScreen(
        users = users,
        navigateUp = navController::navigateUp,
        followUser = viewModel::followUser,
        navigateToProfileScreen = { user ->
            navController.navigate(ProfileScreen(user.id))
        },
    )
}

@Composable
fun FollowersScreen(
    users: LazyPagingItems<User>,
    navigateUp: () -> Unit,
    followUser: (User) -> Unit,
    navigateToProfileScreen: (User) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Followers")
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_left_outlined),
                            contentDescription = null
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 2.dp
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = users,
                key = User::id
            ) { user ->
                if (user != null) {
                    FollowableUser(
                        user = user,
                        onUserClicked = { navigateToProfileScreen(user) },
                        onFollowClicked = { followUser(user) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (users.isEmptyAndNotLoading()) {
            Jumbotron(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp, vertical = 64.dp)
            )
        }
    }
}
