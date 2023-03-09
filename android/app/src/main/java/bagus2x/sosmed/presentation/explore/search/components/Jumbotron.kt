package bagus2x.sosmed.presentation.explore.search.components

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
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            model = R.drawable.ilustration_not_found,
            contentDescription = null,
            modifier = Modifier.width(240.dp)
        )
        Text(
            text = """No result for "$query"""",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Try searching for something else, or check your search settings to see if they're protecting you from potentially sensitive content",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
        )
    }
}
