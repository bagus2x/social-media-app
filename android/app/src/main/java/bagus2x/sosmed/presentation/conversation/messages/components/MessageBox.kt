package bagus2x.sosmed.presentation.conversation.messages.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.LocalAuthenticatedUser
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextField

@Composable
fun MessageBox(
    modifier: Modifier = Modifier,
    description: String,
    onDescriptionChange: (String) -> Unit,
    authUser: User? = LocalAuthenticatedUser.current,
    loading: Boolean = false,
    onGalleryClicked: () -> Unit,
    onCameraClicked: () -> Unit,
    onSendClicked: () -> Unit,
) {
    Column(modifier = modifier) {
        Divider()
        Spacer(modifier = Modifier.height(12.dp))
        if (authUser != null) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    model = authUser.photo ?: Misc.getAvatar(authUser.username),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                TextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier
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
                        Text(text = "Send message as @${authUser.username}")
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
