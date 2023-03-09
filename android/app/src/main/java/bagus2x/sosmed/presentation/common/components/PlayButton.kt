package bagus2x.sosmed.presentation.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R

@Composable
fun PlayButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    playing: Boolean = false
) {
    Surface(
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick, enabled = enabled),
        shape = CircleShape,
        color = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        border = BorderStroke(2.dp, Color.White)
    ) {
        Icon(
            painter =
            if (!playing)
                painterResource(R.drawable.ic_play_filled)
            else
                painterResource(R.drawable.ic_pause_filled),
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp)
        )
    }
}
