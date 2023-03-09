package bagus2x.sosmed.presentation.imageeditor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.media.ImageFilter
import bagus2x.sosmed.presentation.common.media.ImageSticker
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditorActionBar(
    modifier: Modifier = Modifier,
    selected: DeviceMedia.Image,
    onChangeFilter: (ImageFilter) -> Unit,
    onAddSticker: (ImageSticker) -> Unit
) {
    Surface(
        modifier = modifier,
        color = Color.Black,
        contentColor = Color.White
    ) {
        var filterOptionsVisible by remember { mutableStateOf(false) }
        val density = LocalDensity.current
        val popupOffset = remember {
            IntOffset(
                x = 0,
                y = with(density) { -(114.dp).toPx().roundToInt() }
            )
        }
        if (filterOptionsVisible) {
            Popup(
                offset = popupOffset
            ) {
                FilterMenu(
                    selected = selected,
                    onChange = onChangeFilter,
                    modifier = Modifier
                        .background(Color.Black.copy(.5f))
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                        .fillMaxWidth()
                )
            }
        }
        var stickerDialogVisible by remember { mutableStateOf(false) }
        if (stickerDialogVisible) {
            Dialog(
                onDismissRequest = { stickerDialogVisible = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                StickerMenu(
                    modifier = Modifier.fillMaxSize(),
                    onClose = { stickerDialogVisible = false },
                    onPick = { sticker ->
                        onAddSticker(sticker.copy())
                        stickerDialogVisible = false
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    filterOptionsVisible = !filterOptionsVisible
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings_outlined),
                    contentDescription = null
                )
            }
            IconButton(
                onClick = {
                    filterOptionsVisible = false
                    stickerDialogVisible = true
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_smileys_outlined),
                    contentDescription = null
                )
            }
        }
    }
}
