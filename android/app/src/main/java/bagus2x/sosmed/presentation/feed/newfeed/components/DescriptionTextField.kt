package bagus2x.sosmed.presentation.feed.newfeed.components

import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import bagus2x.sosmed.presentation.common.theme.Red100

@Composable
fun DescriptionTextField(
    text: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextField(
        value = text,
        visualTransformation = {
            TransformedText(
                buildAnnotatedString {
                    append(text)
                    if (text.length > 500) {
                        addStyle(
                            SpanStyle(
                                background = Red100
                            ),
                            500,
                            text.length
                        )
                    }
                },
                OffsetMapping.Identity
            )
        },
        onValueChange = onChange,
        placeholder = {
            Text(text = "What's happening?")
        },
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),
        enabled = enabled
    )
}
