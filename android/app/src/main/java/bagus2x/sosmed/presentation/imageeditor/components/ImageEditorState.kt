package bagus2x.sosmed.presentation.imageeditor.components

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.media.Rect
import java.util.*

@Stable
class ImageEditorState(
    val image: DeviceMedia.Image
) {
    // Sticker properties
    val stickers = image.stickers.toMutableStateList()
    val translationX = mutableStateMapOf(
        pairs = stickers.map { it.id to it.translationX }.toTypedArray()
    )
    val translationY = mutableStateMapOf(
        pairs = stickers.map { it.id to it.translationY }.toTypedArray()
    )
    val scale = mutableStateMapOf(
        pairs = stickers.map { it.id to it.scale }.toTypedArray()
    )
    val rotation = mutableStateMapOf(
        pairs = stickers.map { it.id to it.rotation }.toTypedArray()
    )
    val leftTop = mutableStateMapOf<String, Offset>()
    val rightBottom = mutableStateMapOf<String, Offset>()
    // Filter properties
    val filter = mutableStateOf(image.filter)

    // Create new object to apply stickers and filters
    fun asImage(): DeviceMedia.Image {
        return image.copy(
            stickers = stickers.map { old ->
                old.copy(
                    translationX = translationX[old.id] ?: old.translationX,
                    translationY = translationY[old.id] ?: old.translationY,
                    scale = scale[old.id] ?: old.scale,
                    rotation = rotation[old.id] ?: old.rotation,
                    rect = Rect(
                        leftTop[old.id]?.x ?: 0f,
                        leftTop[old.id]?.y ?: 0f,
                        rightBottom[old.id]?.x ?: 0f,
                        rightBottom[old.id]?.y ?: 0f,
                    ),
                    id = UUID.randomUUID().toString()
                )
            },
            filter = filter.value
        )
    }
}

@Composable
fun rememberImageEditorState(image: DeviceMedia.Image): ImageEditorState {
    return remember(image.id) { ImageEditorState(image) }
}
