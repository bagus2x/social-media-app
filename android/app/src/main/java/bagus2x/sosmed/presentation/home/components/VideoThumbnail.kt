package bagus2x.sosmed.presentation.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.PlayButton

@Composable
fun VideoThumbnail(
    thumbnailUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        Image(
            model = thumbnailUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        PlayButton(
            onClick = onClick,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
