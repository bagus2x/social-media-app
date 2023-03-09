package bagus2x.sosmed.presentation.feed.newfeed.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R

@Composable
fun NewFeedTopBar(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    onPostClicked: () -> Unit,
    buttonEnabled: Boolean
) {
    TopAppBar(
        modifier = modifier,
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_close_outlined),
                    contentDescription = null
                )
            }
        },
        actions = {
            Button(
                onClick = onPostClicked,
                modifier = Modifier.padding(end = 12.dp),
                enabled = buttonEnabled
            ) {
                Text(text = stringResource(R.string.text_pst))
            }
        },
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground
    )
}
