package bagus2x.sosmed.presentation.user.following.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.components.Image

@Composable
fun Jumbotron(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            model = R.drawable.ilustration_relationship,
            contentDescription = null,
            modifier = Modifier.width(200.dp)
        )
        Text(
            text = "Looking for followers?",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "When someone follows this account, they'll show up here. Creating Feed and interacting with others helps boosts followers",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
        )
    }
}
