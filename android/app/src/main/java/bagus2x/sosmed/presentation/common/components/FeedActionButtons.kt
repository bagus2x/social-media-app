package bagus2x.sosmed.presentation.common.components

import android.icu.text.CompactDecimalFormat
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.noRippleClickable
import bagus2x.sosmed.presentation.common.theme.AppColor
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FeedActionButtons(
    favorite: Boolean,
    totalFavorites: Int,
    onFavoriteClicked: () -> Unit,
    totalComments: Int,
    onCommentClicked: () -> Unit,
    totalReposts: Int,
    onRepostClicked: () -> Unit,
    onSendClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .animateContentSize()
                .noRippleClickable(onClick = onFavoriteClicked),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FavoriteButton(
                favorite = favorite,
                onClick = onFavoriteClicked
            )
            AnimatedContent(targetState = totalFavorites, transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> height } + fadeIn() with slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() with slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            }) { favorite ->
                if (totalFavorites > 0) {
                    Text(
                        text = rememberSaveable {
                            CompactDecimalFormat.getInstance(
                                Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT
                            ).format(favorite)
                        },
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }
        Row(
            modifier = Modifier.noRippleClickable(onClick = onCommentClicked),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCommentClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_comments_outlined),
                    contentDescription = null
                )
            }
            if (totalComments != 0) {
                Text(
                    text = rememberSaveable(totalComments) {
                        CompactDecimalFormat.getInstance(
                            Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT
                        ).format(totalComments)
                    },
                    style = MaterialTheme.typography.caption,
                )
            }
        }
        Row(
            modifier = Modifier.noRippleClickable(onClick = onRepostClicked),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRepostClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_repeat_outlined),
                    contentDescription = null
                )
            }
            if (totalReposts != 0) {
                Text(
                    text = rememberSaveable {
                        CompactDecimalFormat.getInstance(
                            Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT
                        ).format(totalReposts)
                    },
                    style = MaterialTheme.typography.caption,
                )
            }
        }
        IconButton(onClick = onSendClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_send_outlined),
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FavoriteButton(
    favorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier, iconSize: Dp = 24.dp
) {
    AnimatedContent(targetState = favorite, transitionSpec = {
        scaleIn() with scaleOut()
    }) { isFavorite ->
        if (isFavorite) {
            IconButton(
                onClick = onClick, modifier = modifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_heart_filled),
                    contentDescription = null,
                    tint = AppColor.Pink500.copy(alpha = LocalContentAlpha.current),
                    modifier = Modifier.size(iconSize)
                )
            }
        } else {
            IconButton(
                onClick = onClick, modifier = modifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_heart_outlined),
                    contentDescription = null,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}
