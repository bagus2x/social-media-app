package bagus2x.sosmed.presentation.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bagus2x.sosmed.domain.model.Story
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.theme.AppColor

@Composable
fun Story(
    story: Story,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.unseenAnimation(enabled = story.unseen)
        ) {
            Image(
                model = story.author.photo ?: Misc.getAvatar(story.author.username),
                contentDescription = null,
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.background,
                        shape = CircleShape
                    )
            )
        }
        Text(
            text = story.author.username,
            style = MaterialTheme.typography.caption.copy(fontSize = 11.sp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun Modifier.unseenAnimation(enabled: Boolean): Modifier = composed {
    if (!enabled) {
        return@composed this
    }
    val infiniteTransition = rememberInfiniteTransition()
    val degreesAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = LinearEasing
            )
        )
    )
    val colors = remember { listOf(AppColor.Pink200, AppColor.LightBlue500, AppColor.Pink200) }
    drawBehind {
        rotate(degrees = degreesAnimation) {
            drawCircle(
                brush = Brush.horizontalGradient(colors)
            )
        }
    }
}
