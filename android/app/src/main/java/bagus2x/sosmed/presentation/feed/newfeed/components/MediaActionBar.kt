package bagus2x.sosmed.presentation.feed.newfeed.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.theme.AppColor

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MediaActionBar(
    modifier: Modifier = Modifier,
    textLength: Int,
    onGalleryClicked: () -> Unit,
    onVotesClicked: () -> Unit,
    onLocationClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .border(1.dp, color = MaterialTheme.colors.onBackground.copy(.05f))
            .background(MaterialTheme.colors.background)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onGalleryClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_gallery_outlined),
                contentDescription = null
            )
        }
        IconButton(onClick = onVotesClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_ranking_outlined),
                contentDescription = null
            )
        }
        IconButton(onClick = onLocationClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_location_outlined),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .padding(end = 16.dp)
                .widthIn(24.dp)
                .wrapContentSize()
        ) {
            val progressColor by animateColorAsState(
                targetValue = when {
                    textLength > 500 -> AppColor.Red500
                    textLength > 450 -> AppColor.Yellow500
                    else -> AppColor.LightBlue500
                }
            )
            val scale by animateFloatAsState(
                targetValue = if (textLength > 450) 1.15f else 1f
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = textLength < 500,
                modifier = Modifier.align(Alignment.Center),
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                TextLengthIndicator(
                    progress = textLength / 500f,
                    color = progressColor,
                    modifier = Modifier.scale(scale)
                )
            }
            val textColor by animateColorAsState(
                targetValue = when {
                    textLength > 500 -> AppColor.Red500
                    else -> MaterialTheme.colors.onBackground
                }
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = textLength > 450,
                modifier = Modifier.align(Alignment.Center),
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                Text(
                    text = "${500 - textLength}",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.align(Alignment.Center),
                    color = textColor,
                    overflow = TextOverflow.Visible
                )
            }
        }
    }
}
