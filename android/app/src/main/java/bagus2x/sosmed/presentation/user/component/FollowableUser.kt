package bagus2x.sosmed.presentation.user.component

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextFormatter

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun FollowableUser(
    user: User,
    onUserClicked: () -> Unit,
    onFollowClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onUserClicked,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                model = user.photo ?: Misc.getAvatar(user.username),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.body2
                        )
                    }
                    var isFollowing by remember(user) { mutableStateOf(user.following) }
                    AnimatedContent(
                        targetState = isFollowing,
                        transitionSpec = { fadeIn() with fadeOut() }
                    ) {
                        if (isFollowing) {
                            OutlinedButton(
                                onClick = {
                                    isFollowing = false
                                    onFollowClicked()
                                },
                            ) {
                                Text(text = stringResource(R.string.text_unfollow))
                            }
                        } else {
                            Button(
                                onClick = {
                                    isFollowing = true
                                    onFollowClicked()
                                },
                            ) {
                                Text(text = stringResource(R.string.text_follow))
                            }
                        }
                    }
                }
                if (!user.bio.isNullOrBlank()) {
                    TextFormatter(
                        text = user.bio,
                        style = MaterialTheme.typography.caption.copy(
                            color = MaterialTheme.colors.onBackground.copy(
                                alpha = ContentAlpha.medium
                            )
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        onClick = { detectClickText { onUserClicked() } }
                    )
                }
            }
        }
    }
}
