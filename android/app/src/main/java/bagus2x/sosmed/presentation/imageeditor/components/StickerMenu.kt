package bagus2x.sosmed.presentation.imageeditor.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.media.ImageSticker
import bagus2x.sosmed.presentation.common.media.Rect

private val Stickers by lazy {
    listOf(
        ImageSticker(
            stickerRes = R.drawable.sticker_love_it,
            translationX = .5f,
            translationY = .5f,
            scale = 1f,
            rotation = 1f,
            rect = Rect.Empty
        ),
        ImageSticker(
            stickerRes = R.drawable.sticker_thoughts,
            translationX = .5f,
            translationY = .5f,
            scale = 1f,
            rotation = 1f,
            rect = Rect.Empty
        ),
    )
}

@Composable
fun StickerMenu(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onPick: (ImageSticker) -> Unit,
) {
    Column(
        modifier = modifier.background(Color.Black.copy(alpha = .1f))
    ) {
        TopAppBar(
            backgroundColor = Color.Black,
            contentColor = Color.White,
            title = {
                Text(text = stringResource(R.string.text_sticker))
            },
            navigationIcon = {
                IconButton(onClick = onClose) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close_outlined),
                        contentDescription = null
                    )
                }
            }
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = Stickers,
                key = { it.stickerRes }
            ) { sticker ->
                Image(
                    painter = painterResource(sticker.stickerRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1 / 1f)
                        .clickable { onPick(sticker) },
                    contentScale = ContentScale.Inside
                )
            }
        }
    }
}
