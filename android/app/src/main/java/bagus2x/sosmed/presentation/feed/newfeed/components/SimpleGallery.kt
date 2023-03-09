package bagus2x.sosmed.presentation.feed.newfeed.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.gallery.components.DeviceMedia

@Composable
fun SimpleGallery(
    onCameraClicked: () -> Unit,
    onGalleryClicked: () -> Unit,
    onItemClicked: (DeviceMedia) -> Unit,
    options: List<DeviceMedia>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Box(
                modifier = modifier
                    .size(88.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onCameraClicked)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onBackground.copy(alpha = .05f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera_outlined),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }
        }
        items(
            items = options,
            key = { it.id }
        ) { fileMedia ->
            DeviceMedia(
                deviceMedia = fileMedia,
                modifier = Modifier.size(88.dp),
                onClick = { onItemClicked(fileMedia) }
            )
        }
        item {
            Box(
                modifier = modifier
                    .size(88.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onGalleryClicked)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.onBackground.copy(alpha = .05f),
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_gallery_outlined),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}
