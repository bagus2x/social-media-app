package bagus2x.sosmed.presentation.feed.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextFormatter
import bagus2x.sosmed.presentation.common.noRippleClickable
import bagus2x.sosmed.presentation.common.theme.Pink500
import bagus2x.sosmed.presentation.feed.comment.ShowMoreButton

@Composable
fun Comment(
    comment: Comment,
    indentation: Int,
    modifier: Modifier = Modifier,
    showMoreVisible: Boolean,
    showMoreText: String = stringResource(R.string.text_show_all_comments),
    onCommentClicked: () -> Unit,
    onReplyClicked: () -> Unit,
    onShowMoreClicked: () -> Unit,
    onUrlClicked: (String) -> Unit,
    onHashtagClicked: (String) -> Unit,
    onMentionClicked: (String) -> Unit,
) {
    val density = LocalDensity.current
    val color = Color(0xFFDDDDDD)
    val strokeWidth = with(density) { 1.dp.toPx() }
    Column(
        modifier = modifier
            .drawTree(
                color = color,
                strokeWidth = strokeWidth,
                hasChild = comment.totalReplies > 0,
                hasParent = comment.parentId != null,
                indentation = indentation
            )
            .padding(
                start = (indentation * 32 + 16).dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 8.dp
            )
            .noRippleClickable(onClick = onCommentClicked)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val author = comment.author
            Image(
                model = author.photo ?: Misc.getAvatar(author.username),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = author.username,
                        style = MaterialTheme.typography.caption,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = Misc.formatRelative(comment.createdAt),
                        style = MaterialTheme.typography.caption,
                    )
                    Pinned(pinned = comment.pinned)
                }
                TextFormatter(
                    text = comment.description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 4,
                    onClick = {
                        detectClickHashtag(onUrlClicked)
                        detectClickMention(onMentionClicked)
                        detectClickHashtag(onHashtagClicked)
                        detectClickText { onCommentClicked() }
                    }
                )
                ActionButton(
                    favorite = comment.favorite,
                    totalFavorites = comment.totalFavorites,
                    onFavoriteClicked = { },
                    totalComments = comment.totalReplies,
                    onCommentClicked = onReplyClicked
                )
            }
        }
        ShowMoreButton(
            text = showMoreText,
            onClick = onShowMoreClicked,
            visible = showMoreVisible,
            modifier = Modifier
                .padding(start = 36.dp, top = 8.dp)
                .drawBehind {
                    drawArc(
                        color = color,
                        startAngle = 180f,
                        sweepAngle = -90f,
                        useCenter = false,
                        topLeft = Offset(
                            x = (-20).dp.toPx(),
                            y = (-16).dp.toPx()
                        ),
                        size = Size(
                            width = 36.dp.toPx(),
                            height = (32.dp.toPx() + size.height) / 2
                        ),
                        style = Stroke(
                            width = strokeWidth
                        )
                    )
                }
        )
    }
}

@Composable
private fun ActionButton(
    favorite: Boolean,
    totalFavorites: Int,
    onFavoriteClicked: () -> Unit,
    totalComments: Int,
    onCommentClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var isFavorite by remember(favorite) { mutableStateOf(favorite) }
            FavoriteButton(favorite = isFavorite, onClick = {
                isFavorite = !isFavorite
                onFavoriteClicked()
            })
            Text(
                text = "$totalFavorites",
                style = MaterialTheme.typography.overline
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_comments_outlined),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
                    .noRippleClickable(onClick = onCommentClicked)
            )
            Text(
                text = "$totalComments",
                style = MaterialTheme.typography.overline
            )
        }
        Text(
            text = "See translation",
            style = MaterialTheme.typography.overline
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun FavoriteButton(
    favorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(targetState = favorite, transitionSpec = {
        scaleIn() with scaleOut()
    }) { isFavorite ->
        if (isFavorite) {
            Icon(
                painter = painterResource(R.drawable.ic_heart_filled),
                contentDescription = null,
                tint = Pink500.copy(alpha = LocalContentAlpha.current),
                modifier = modifier
                    .size(16.dp)
                    .noRippleClickable(onClick = onClick)
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_heart_outlined),
                contentDescription = null,
                modifier = modifier
                    .size(16.dp)
                    .noRippleClickable(onClick = onClick)
            )
        }
    }
}

private fun Modifier.drawTree(
    color: Color,
    indentation: Int,
    strokeWidth: Float,
    hasParent: Boolean,
    hasChild: Boolean
): Modifier {
    return this.drawBehind {
        repeat(indentation) {
            val x = (it * 32 + 32).dp.toPx()
            drawLine(
                color = color,
                start = Offset(
                    x = x,
                    y = 0f
                ),
                end = Offset(
                    x = x,
                    y = size.height
                ),
                strokeWidth = strokeWidth
            )
        }
        if (hasParent) {
            val x = (indentation * 32).dp.toPx()
            drawArc(
                color = color,
                startAngle = 180f,
                sweepAngle = -90f,
                useCenter = false,
                topLeft = Offset(
                    x = x,
                    y = (-16).dp.toPx()
                ),
                size = Size(
                    width = 32.dp.toPx(),
                    height = 80.dp.toPx() / 2
                ),
                style = Stroke(
                    width = strokeWidth
                )
            )
        }
        if (hasChild) {
            val x = (indentation * 32 + 32).dp.toPx()
            drawLine(
                color = color,
                start = Offset(
                    x = x,
                    y = (8 + 32).dp.toPx()
                ),
                end = Offset(
                    x = x,
                    y = size.height
                ),
                strokeWidth = strokeWidth
            )
        }
    }
}

@Composable
private fun Pinned(
    pinned: Boolean,
    modifier: Modifier = Modifier
) {
    if (!pinned) {
        return
    }
    Surface(
        color = MaterialTheme.colors.onBackground.copy(alpha = .08f),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.text_pinned),
            color = MaterialTheme.colors.onBackground.copy(alpha = .5f),
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.caption
        )
    }
}
