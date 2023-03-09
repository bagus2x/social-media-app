package bagus2x.sosmed.presentation.conversation.messages.components

import android.text.format.DateFormat.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.domain.model.Message
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextFormatter
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Message(
    message: Message,
    own: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = if (own) Arrangement.End else Arrangement.Start,
            modifier = Modifier.align(if (own) Alignment.End else Alignment.Start),
        ) {
            if (!own) {
                Image(
                    model = message.sender.photo ?: Misc.getAvatar(message.sender.username),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = modifier.width(16.dp))
            }
            val color =
                if (own)
                    MaterialTheme
                        .colors
                        .primary
                else
                    MaterialTheme
                        .colors
                        .onBackground.copy(alpha = .5f)
                        .compositeOver(MaterialTheme.colors.primary)
            val shape = if (own) SentMessageShape else ReceivedMessageShape
            Surface(
                onClick = { onClick?.invoke() },
                shape = shape,
                color = color,
                contentColor = Color.White
            ) {
                val style = MaterialTheme.typography.body2.copy(color = LocalContentColor.current)
                val spanStyle = style.toSpanStyle().copy(fontWeight = FontWeight.Bold)
                TextFormatter(
                    text = message.description,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = style,
                    hashtagSpanStyle = spanStyle,
                    mentionSpanStyle = spanStyle,
                    urlSpanStyle = spanStyle,
                )
            }
        }
        val context = LocalContext.current
        val formattedDate = rememberSaveable {
            val pattern = if (is24HourFormat(context)) "HH:mm" else "hh:mm a"
            val dateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
            dateTimeFormatter.format(message.createdAt)
        }
        Text(
            text = if (!own) "${message.sender.username} • $formattedDate" else formattedDate,
            style = MaterialTheme.typography.overline,
            modifier = Modifier.align(if (own) Alignment.End else Alignment.Start)
        )
    }
}

private val SentMessageShape = RoundedCornerShape(32.dp, 32.dp, 4.dp, 32.dp)
private val ReceivedMessageShape = RoundedCornerShape(32.dp, 32.dp, 32.dp, 4.dp)
