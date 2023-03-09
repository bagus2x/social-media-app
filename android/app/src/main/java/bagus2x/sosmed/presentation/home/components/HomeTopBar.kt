package bagus2x.sosmed.presentation.home.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onAddClicked: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
    ) {
        Text(
            text = "[ Sosmed ]",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onAddClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_add_outlined),
                contentDescription = null
            )
        }
    }
}
