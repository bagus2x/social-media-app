package bagus2x.sosmed.presentation.imageeditor.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import bagus2x.sosmed.presentation.common.components.Image

@Composable
fun ImageEditor(
    state: ImageEditorState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Image(
                model = state.image.file,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                contentScale = ContentScale.FillWidth,
                colorFilter = state.filter.value.colorFilter
            )
            for ((id, drawableRes, _, _) in state.stickers) {
                Image(
                    painter = painterResource(drawableRes),
                    contentDescription = null,
                    modifier = Modifier
                        .width(100.dp)
                        .stickerGesture(id, state),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

private fun Modifier.stickerGesture(
    id: String,
    state: ImageEditorState
): Modifier {
    return this
        .offset {
            IntOffset(
                x = ((state.translationX[id] ?: .5f)).roundToInt(),
                y = ((state.translationY[id] ?: .5f)).roundToInt()
            )
        }
        .graphicsLayer {
            scaleX = state.scale[id] ?: 1f
            scaleY = state.scale[id] ?: 1f
            rotationZ = state.rotation[id] ?: 0f
        }
        .pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { _, pan, gestureZoom, gestureRotate ->
                    state.rotation[id] = (state.rotation[id] ?: 0f) + gestureRotate
                    state.scale[id] = (state.scale[id] ?: 1f) * gestureZoom
                    val x = pan.x * (state.scale[id] ?: 1f)
                    val y = pan.y * (state.scale[id] ?: 1f)
                    val angleRad = (state.rotation[id] ?: 1f) * PI / 180.0
                    state.translationX[id] = ((state.translationX[id] ?: .5f) + (x * cos(angleRad) - y * sin(angleRad)).toFloat())
                    state.translationY[id] = ((state.translationY[id] ?: .5f) + (x * sin(angleRad) + y * cos(angleRad)).toFloat())
                }
            )
        }
        .onGloballyPositioned {
            val parentSize = it.parentLayoutCoordinates!!.size
            val topLeft = Offset(
                x = it.positionInParent().x / parentSize.width,
                y = it.positionInParent().y / parentSize.height
            )
            val bottomRight = Offset(
                x = (it.positionInParent().x + it.size.width) / parentSize.width,
                y = (it.positionInParent().y + it.size.height) / parentSize.height
            )
            state.leftTop[id] = topLeft
            state.rightBottom[id] = bottomRight
        }
}
