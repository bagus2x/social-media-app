package bagus2x.sosmed.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextFormatter
import bagus2x.sosmed.presentation.common.components.rememberTranslationState
import bagus2x.sosmed.presentation.common.components.textFormatter
import timber.log.Timber

@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.systemBarsPadding()) {
        val text = textFormatter(
            text = """
           hello *my name* is @tubagus @saifulloh. #GGMU http://google.com social media facebook.com
           _bagus_ hate ~you~
           `fun main() { println("hello world") }`
       """.trimIndent()
        )
        TextFormatter(
            text = text,
            onClick = {
                detectClickText {
                    Timber.i("HASIL offset $it")
                }
                detectClickUrl {
                    Timber.i("HASIL url $it")
                }
                detectClickHashtag {
                    Timber.i("HASIL hashtag $it")
                }
                detectClickMention {
                    Timber.i("HASIL mention $it")
                }
            }
        )
        Image(
            model = null, contentDescription = null, modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )
        val listState = remember {
            mutableStateListOf<@Composable () -> Unit>()
        }
        val translationState = rememberTranslationState(
            sourceText = "hello my name is tubagus",
            sourceLang = "en"
        )
        Text(text = translationState.text.value)
        Button(onClick = translationState::toggle) {
            Text(text = translationState.buttonText)
        }
        Box(
            modifier = Modifier.background(Color.Red)
        ) {
            Button(
                onClick = {
                    listState.add {
                        Text(text = "hello ")
                    }
                }
            ) {
                Text(text = "hello")
            }
        }
        LazyColumn {
            items(listState) {
                it()
            }
        }
    }
}
