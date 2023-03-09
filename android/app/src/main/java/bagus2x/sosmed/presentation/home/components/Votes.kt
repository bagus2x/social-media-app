package bagus2x.sosmed.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Votes(
    voted: Boolean,
    question: String,
    choices: Map<String, Int>,
    remainingTime: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalVoters = remember(choices) { choices.values.sum() }
    BoxWithConstraints(modifier = modifier) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.body1
            )
            val density = LocalDensity.current
            val highestVoters = remember(choices) {
                val highest = choices.values.maxOrNull() ?: return@remember 0
                val count = choices.values.count { it == highest }
                if (count > 1) {
                    return@remember 0
                }
                highest
            }
            var isVoted by rememberSaveable(voted) { mutableStateOf(voted) }
            for ((label, voters) in choices) {
                val isHighest = highestVoters != 0 && voters >= highestVoters
                val barColor = MaterialTheme.colors.primary
                val barVisible by animateFloatAsState(
                    targetValue = if (isVoted) 1f else 0f,
                    animationSpec = tween(durationMillis = 1000)
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    onClick = {
                        onClick(label)
                        isVoted = true
                    },
                    shape = BarShape
                ) {
                    Row(
                        modifier = Modifier
                            .drawBehind {
                                val radius = CornerRadius(with(density) { BarRadius.toPx() })
                                drawRoundRect(
                                    color = barColor,
                                    alpha = .1f,
                                    cornerRadius = radius,
                                )
                                drawRoundRect(
                                    color = barColor,
                                    alpha = if (isHighest) .3f else .2f,
                                    size = size.copy(width = barVisible * voters.toFloat() / totalVoters * size.width),
                                    cornerRadius = radius,
                                )
                            }
                            .padding(ButtonDefaults.ContentPadding)
                    ) {
                        val style =
                            if (isHighest && isVoted)
                                MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
                            else
                                MaterialTheme.typography.body1
                        Text(
                            text = label,
                            style = style
                        )
                        if (isVoted) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = percentage(voters, totalVoters),
                                style = style
                            )
                        }
                    }
                }
            }
            Text(
                text = "$totalVoters Votes • $remainingTime",
                style = MaterialTheme.typography.caption
            )
        }
    }
}

private var BarRadius = 8.dp
private var BarShape = RoundedCornerShape(BarRadius)

private val DecimalFormat = DecimalFormat("0.#")
private fun percentage(voters: Int, totalVoters: Int): String {
    return "${DecimalFormat.format((voters.toDouble() / totalVoters) * 100)}%"
}
