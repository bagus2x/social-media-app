package bagus2x.sosmed.presentation.gallery.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.theme.AppColor
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

@Composable
fun DeviceMedia(
    deviceMedia: DeviceMedia,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                onClick = onClick,
                enabled = enabled
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onBackground.copy(alpha = .05f),
                shape = MaterialTheme.shapes.small
            )
    ) {
        when (deviceMedia) {
            is DeviceMedia.Image -> {
                Image(
                    model = deviceMedia.file,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            is DeviceMedia.Video -> {
                Image(
                    model = deviceMedia.file,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = modifier.fillMaxSize()
                )
                Surface(
                    color = MaterialTheme.colors.onBackground.copy(alpha = .2f),
                    contentColor = MaterialTheme.colors.background,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = deviceMedia.duration.milliseconds.toString(DurationUnit.SECONDS),
                        style = MaterialTheme.typography.caption.copy(fontSize = 9.sp),
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
        if (!enabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.surface.copy(alpha = .5f))
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.onSurface.copy(alpha = .5f))
            )
            Icon(
                painter = painterResource(R.drawable.ic_complete_outlined),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(AppColor.Green500),
                tint = Color.White,
            )
        }
    }
}
