package bagus2x.sosmed.presentation.conversation.chat.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Chat(
    chat: Chat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                model = chat.photo,
                contentDescription = chat.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    text = chat.name,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val recentMessage = chat.recentMessages
                if (recentMessage != null) {
                    Text(
                        text = recentMessage,
                        style = MaterialTheme.typography.body2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
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

private val Chat.recentMessages: String?
    get() = when (this) {
        is Chat.Group -> recentMessages.firstOrNull()?.description
        is Chat.Private -> recentMessages.firstOrNull()?.description
    }
