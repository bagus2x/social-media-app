package bagus2x.sosmed.presentation.feed.newfeed.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.presentation.common.media.DeviceMedia

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectedMedias(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    medias: List<DeviceMedia>,
    onCloseClicked: (DeviceMedia) -> Unit,
    onItemClicked: (DeviceMedia) -> Unit
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        state = state
    ) {
        items(
            items = medias,
            key = { it.id }
        ) { media ->
            when (media) {
                is DeviceMedia.Image -> {
                    SelectedImage(
                        image = media,
                        onCloseClicked = { onCloseClicked(media) },
                        onItemClicked = { onItemClicked(media) },
                        modifier = Modifier
                            .width(240.dp)
                            .animateItemPlacement()
                    )
                }
                is DeviceMedia.Video -> {
                    SelectedVideo(
                        deviceMedia = media,
                        onCloseClicked = { onCloseClicked(media) },
                        onItemClicked = { onItemClicked(media) },
                        modifier = Modifier
                            .size(200.dp)
                            .animateItemPlacement()
                    )
                }
            }
        }
    }
}
