package bagus2x.sosmed.presentation.conversation.messages.components

import android.animation.ArgbEvaluator
import android.text.format.DateFormat.is24HourFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.domain.model.Message
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextFormatter
import bagus2x.sosmed.presentation.common.theme.AppColor
import java.time.format.DateTimeFormatter

@Composable
fun Message(
    message: Message,
    own: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    columnHeight: Dp,
    date: String? = null
) {
    var topOffset by remember { mutableStateOf(0f) }
    var bottomOffset by remember { mutableStateOf(0f) }
    Column(
        modifier = modifier.onGloballyPositioned { layoutCoordinates ->
            topOffset = layoutCoordinates.boundsInParent().top
            bottomOffset = layoutCoordinates.boundsInParent().bottom
        },
    ) {
        if (!date.isNullOrBlank()) {
            Text(
                text = date,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Column(
            modifier = Modifier.align(if (own) Alignment.End else Alignment.Start),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val shape = if (own) SentMessageShape else ReceivedMessageShape
            val density = LocalDensity.current
            val columnHeightPx = with(density) { columnHeight.toPx() }
            Box(
                modifier = Modifier
                    .clip(shape)
                    .let { if (onClick != null) it.clickable(onClick = onClick) else it }
                    .drawBehind {
                        if (!own) {
                            drawRect(
                                color = Color.Black
                                    .copy(alpha = .5f)
                                    .compositeOver(AppColor.LightBlue500)
                            )
                        } else {
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(
                                            ArgbEvaluator.evaluate(
                                                topOffset / columnHeightPx,
                                                TopColor,
                                                BottomColor
                                            ) as Int
                                        ),
                                        Color(
                                            ArgbEvaluator.evaluate(
                                                bottomOffset / columnHeightPx,
                                                TopColor,
                                                BottomColor
                                            ) as Int
                                        )
                                    )
                                )
                            )
                        }
                    }
            ) {
                val style = MaterialTheme.typography.body2.copy(color = Color.White)
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
            val context = LocalContext.current
            val formattedDate = rememberSaveable {
                val pattern = if (is24HourFormat(context)) "HH:mm" else "hh:mm a"
                val dateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
                dateTimeFormatter.format(message.createdAt)
            }
            Row(
                modifier = Modifier.align(if (own) Alignment.End else Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!own) {
                    Image(
                        model = message.sender.photo ?: Misc.getAvatar(message.sender.username),
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = if (!own) "${message.sender.username} • $formattedDate" else formattedDate,
                    style = MaterialTheme.typography.overline,
                )
            }
        }
    }
}

private val SentMessageShape = RoundedCornerShape(32.dp, 32.dp, 4.dp, 32.dp)
private val ReceivedMessageShape = RoundedCornerShape(32.dp, 32.dp, 32.dp, 4.dp)
private val ArgbEvaluator = ArgbEvaluator()
private val TopColor = AppColor.Purple500.toArgb()
private val BottomColor = AppColor.LightBlue500.toArgb()
