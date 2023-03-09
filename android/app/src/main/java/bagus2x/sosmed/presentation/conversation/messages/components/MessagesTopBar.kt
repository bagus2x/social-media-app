package bagus2x.sosmed.presentation.conversation.chatdetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.noRippleClickable

@Composable
fun MessagesTopBar(
    chat: Chat,
    onBackClicked: () -> Unit,
    onMoreVertClicked: () -> Unit,
    onChatClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left_outlined),
                    contentDescription = null
                )
            }
        },
        title = {
            Image(
                model = chat.photo,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clickable(onClick = onChatClicked)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.noRippleClickable(onClick = onChatClicked)) {
                Text(
                    text = chat.name,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Online",
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            IconButton(onClick = onMoreVertClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_more_outlined),
                    contentDescription = null,
                    modifier = Modifier.rotate(90f)
                )
            }
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 2.dp,
        modifier = modifier
    )
}

private val Chat.photo: String
    get() = when (this) {
        is Chat.Group -> photo ?: Misc.getIcon("group-$id")
        is Chat.Private -> pair.photo ?: Misc.getAvatar(name)
    }

private val Chat.name: String
    get() = when (this) {
        is Chat.Group -> name.ifBlank { members.joinToString(", ") { it.username } }
        is Chat.Private -> pair.username
    }
