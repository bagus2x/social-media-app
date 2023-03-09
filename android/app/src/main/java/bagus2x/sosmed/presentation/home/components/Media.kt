package bagus2x.sosmed.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import bagus2x.sosmed.domain.model.Media
import coil.compose.AsyncImage

@Composable
fun Media(
    media: Media,
    onImageClicked: (Media.Image) -> Unit,
    onVideoClicked: (Media.Video) -> Unit,
    modifier: Modifier = Modifier
) {
    when (media) {
        is Media.Image -> {
            AsyncImage(
                model = media.imageUrl,
                contentDescription = null,
                modifier = modifier.clickable { onImageClicked(media) },
                contentScale = ContentScale.Crop
            )
        }
        is Media.Video -> {
            VideoThumbnail(
                thumbnailUrl = media.thumbnailUrl,
                contentDescription = null,
                modifier = modifier,
                onClick = { onVideoClicked(media) }
            )
        }
    }
}
