package bagus2x.sosmed.presentation.feed.newfeed.components

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation

@Composable
fun SelectedImage(
    image: DeviceMedia.Image,
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
        val context = LocalContext.current
        Image(
            model = ImageRequest
                .Builder(context)
                .data(image.file)
                .transformations(StickerFilterTransformation(image, context))
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
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
    }
}

private class StickerFilterTransformation(
    private val image: DeviceMedia.Image,
    private val context: Context,
    override val cacheKey: String = image.stickers.joinToString { it.id }
) : Transformation {

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return image.saveAsBitmap(input, context)
    }
}
