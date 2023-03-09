package bagus2x.sosmed.presentation.imageeditor

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.imageeditor.components.EditorActionBar
import bagus2x.sosmed.presentation.imageeditor.components.ImageEditor
import bagus2x.sosmed.presentation.imageeditor.components.ImageEditorTopBar
import bagus2x.sosmed.presentation.imageeditor.components.rememberImageEditorState
import java.util.*

@Composable
fun ImageEditorScreen(
    image: DeviceMedia.Image,
    navigateUp: () -> Unit,
    save: (DeviceMedia.Image) -> Unit
) {
    val imagePreviewState = rememberImageEditorState(image)
    Scaffold(
        topBar = {
            ImageEditorTopBar(
                onBackClicked = navigateUp,
                onSaveClicked = {
                    val edited = imagePreviewState.asImage()
                    save(edited)
                }
            )
        },
        backgroundColor = Color.Black
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            ImageEditor(
                state = imagePreviewState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            EditorActionBar(
                modifier = Modifier.fillMaxWidth(),
                onChangeFilter = { filter ->
                    imagePreviewState.filter.value = filter
                },
                onAddSticker = { sticker ->
                    val new = sticker.copy(id = UUID.randomUUID().toString())
                    imagePreviewState.stickers.add(new)
                },
                selected = imagePreviewState.image
            )
        }
    }
}


