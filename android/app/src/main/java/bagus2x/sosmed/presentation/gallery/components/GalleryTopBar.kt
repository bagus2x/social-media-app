package bagus2x.sosmed.presentation.gallery.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R

@Composable
fun GalleryTopBar(
    modifier: Modifier = Modifier,
    added: Int = 0,
    onCloseClicked: () -> Unit,
    onAddClicked: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = stringResource(R.string.text_gallery))
        },
        navigationIcon = {
            IconButton(onClick = onCloseClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_close_outlined),
                    contentDescription = null
                )
            }
        },
        actions = {
            TextButton(onClick = onAddClicked) {
                Text(
                    text = "Add $added",
                    modifier = Modifier.padding(end = 12.dp),
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground
    )
}

@Composable
fun GalleryTopBar(
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = stringResource(R.string.text_gallery))
        },
        navigationIcon = {
            IconButton(onClick = onCloseClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_close_outlined),
                    contentDescription = null
                )
            }
        },
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground
    )
}
