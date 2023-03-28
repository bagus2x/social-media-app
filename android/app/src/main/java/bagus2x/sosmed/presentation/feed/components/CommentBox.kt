package bagus2x.sosmed.presentation.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.model.Profile
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextField

@Composable
fun CommentBox(
    modifier: Modifier = Modifier,
    profile: Profile?,
    focusRequester: FocusRequester = remember { FocusRequester() },
    description: String,
    setDescription: (String) -> Unit,
    commentToBeReplied: Comment?,
    cancelComment: () -> Unit,
    loading: Boolean = false,
    onGalleryClicked: () -> Unit,
    onCameraClicked: () -> Unit,
    onSendClicked: () -> Unit,
) {
    Column(modifier = modifier) {
        if (commentToBeReplied != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.onBackground.copy(alpha = .05f))
                    .padding(
                        start = 16.dp,
                        top = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Replying @${commentToBeReplied.author.username}",
                    style = MaterialTheme.typography.body2
                )
                IconButton(onClick = cancelComment) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close_outlined),
                        contentDescription = null
                    )
                }
            }
        }
        Divider()
        Spacer(modifier = Modifier.height(12.dp))
        if (profile != null) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    model = profile.photo ?: Misc.getAvatar(profile.username),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                TextField(
                    value = description,
                    onValueChange = setDescription,
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .weight(1f)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (-11).sp
                                    .toPx()
                                    .toInt()
                            )
                        },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.body2,
                    label = {
                        Text(text = "Comment as @${profile.username}")
                    },
                    contentPadding = PaddingValues(0.dp),
                    minHeight = 0.dp
                )
            }
        }
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 16.dp)
        ) {
            IconButton(onClick = onGalleryClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_gallery_outlined),
                    contentDescription = null
                )
            }
            IconButton(onClick = onCameraClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera_outlined),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box {
                Button(
                    onClick = onSendClicked,
                    contentPadding = PaddingValues(0.dp),
                    elevation = ButtonDefaults.elevation(0.dp),
                    enabled = !loading && description.isNotEmpty()
                ) {
                    Text(
                        text = stringResource(R.string.text_send),
                        modifier = Modifier.alpha(if (loading) 0f else 1f)
                    )
                }
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp),
                    )
                }
            }
        }
    }
}
