package bagus2x.sosmed.presentation.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.items
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.gallery.components.DeviceMedia
import bagus2x.sosmed.presentation.gallery.components.GalleryTopBar

@Composable
fun GalleryScreen(
    stateProvider: () -> GalleryState,
    options: LazyPagingItems<DeviceMedia>,
    onSelectDeviceMedia: (DeviceMedia) -> Unit,
    onUnselectDeviceMedia: (DeviceMedia) -> Unit,
    onCloseClicked: () -> Unit,
    onAddClicked: () -> Unit
) {
    val state = stateProvider()
    Scaffold(
        topBar = {
            if (state.multiple) {
                GalleryTopBar(
                    added = state.selectedMedias.size,
                    onCloseClicked = onCloseClicked,
                    onAddClicked = onAddClicked
                )
            } else {
                GalleryTopBar(onCloseClicked = onCloseClicked)
            }
        },
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(
                items = options,
                key = { it.id },
            ) { deviceMedia ->
                val isSelected = remember(state.selectedMedias) {
                    state.selectedMedias.any { it.id == deviceMedia?.id }
                }
                DeviceMedia(
                    selected = isSelected,
                    enabled = state.isEnabled || isSelected,
                    deviceMedia = deviceMedia ?: return@items,
                    onClick = {
                        if (isSelected) {
                            onUnselectDeviceMedia(deviceMedia)
                        } else {
                            onSelectDeviceMedia(deviceMedia)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
            item {
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}
