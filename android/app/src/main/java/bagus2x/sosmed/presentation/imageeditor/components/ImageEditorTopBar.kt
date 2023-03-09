package bagus2x.sosmed.presentation.imageeditor.components

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R

@Composable
fun ImageEditorTopBar(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    onSaveClicked: () -> Unit
) {
    TopAppBar(
        modifier = modifier.statusBarsPadding(),
        title = {
            Text(text = stringResource(R.string.text_edit_photo))
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_close_outlined),
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = onSaveClicked
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_complete_outlined),
                    contentDescription = null
                )
            }
        },
        backgroundColor = Color.Transparent,
        contentColor = Color.White,
        elevation = 0.dp
    )
}
