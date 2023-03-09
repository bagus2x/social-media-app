package bagus2x.sosmed.presentation.feed.newfeed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.components.Image
import kotlin.time.Duration.Companion.milliseconds

private val DurationBackgroundShape = RoundedCornerShape(8.dp)

@Composable
fun SelectedVideo(
    deviceMedia: DeviceMedia.Video,
    onCloseClicked: () -> Unit,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clickable(onClick = onItemClicked)
            .clip(MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onBackground.copy(alpha = .05f),
                shape = MaterialTheme.shapes.small
            )
    ) {
        Image(
            model = deviceMedia.file,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Icon(
            painter = painterResource(R.drawable.ic_close_outlined),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(.5F))
                .clickable(onClick = onCloseClicked),
            tint = Color.White
        )
        Surface(
            color = Color.Black.copy(alpha = .5f),
            contentColor = Color.White,
            shape = DurationBackgroundShape,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Text(
                text = deviceMedia.duration.milliseconds.toComponents { minutes, seconds, _ ->
                    String.format("%02d : %02d", minutes, seconds)
                },
                style = MaterialTheme.typography.caption,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 2.5.dp, vertical = 2.dp)
            )
        }
    }
}
