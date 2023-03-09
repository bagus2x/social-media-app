package bagus2x.sosmed.presentation.conversation.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Jumbotron(
    onClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Welcome to your inbox!",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Drop a line, share Feeds and more with private conversations between you and others on Medsos",
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
        )
        Button(
            onClick = onClick,
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Text(text = "Write a message?")
        }
    }
}
