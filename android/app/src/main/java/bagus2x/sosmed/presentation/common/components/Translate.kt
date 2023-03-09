package bagus2x.sosmed.presentation.common.components

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import bagus2x.sosmed.presentation.common.LocalTranslator
import bagus2x.sosmed.presentation.common.translation.Translator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TranslationState(
    val sourceText: String,
    val sourceLang: String?,
    val initial: Text = Source(sourceText),
    private val delay: Duration = 500.milliseconds,
    private val scope: CoroutineScope,
    private val translator: Translator,
) {
    var text by mutableStateOf(initial)
    val isTranslatable: Boolean
        get() = sourceLang != null && sourceLang != translator.targetLanguage
    val buttonText by derivedStateOf {
        when (text) {
            is Loading -> "Loading"
            is Source -> "See translation"
            is Translated -> "See original"
        }
    }
    private val l by derivedStateOf {
        Timber.i("HASIL text $text")
        text
    }

    private fun translate() {
        scope.launch {
            if (text is Translated) {
                return@launch
            }
            text = Loading(sourceText)
            delay(delay)
            text = try {
                val targetLang = requireNotNull(translator.targetLanguage) { "targetLang null" }
                val sourceLang = requireNotNull(sourceLang) { "source lang null" }
                val translated = translator.translate(sourceText, sourceLang, targetLang)
                Translated(translated)
            } catch (e: Exception) {
                Timber.e(e)
                Source(sourceText)
            }
        }
    }

    private fun undo() {
        text = Source(sourceText)
    }

    fun toggle() {
        if (text is Translated) {
            undo()
            return
        }
        if (text is Source) {
            translate()
        }
    }

    sealed class Text(open val value: String)

    data class Source(override val value: String) : Text(value)

    data class Loading(override val value: String) : Text(value)

    data class Translated(override val value: String) : Text(value)
}

fun saver(translator: Translator, scope: CoroutineScope) = mapSaver<TranslationState>(
    save = {
        mapOf(
            "source_text" to it.sourceText,
            "source_lang" to it.sourceLang,
            "initial" to it.text.value,
            "translated" to (it.text is TranslationState.Translated)
        )
    },
    restore = {
        TranslationState(
            sourceText = it["source_text"] as String,
            sourceLang = it["source_lang"] as String?,
            initial = if (it["translated"] as Boolean)
                TranslationState.Translated(it["initial"] as String)
            else TranslationState.Source(it["initial"] as String),
            translator = translator,
            scope = scope
        )
    }
)

@Composable
fun rememberTranslationState(sourceText: String, sourceLang: String?): TranslationState {
    val translator = LocalTranslator.current
    val scope = rememberCoroutineScope()
    return rememberSaveable(saver = saver(translator, scope)) {
        TranslationState(
            sourceText = sourceText,
            sourceLang = sourceLang,
            translator = translator,
            scope = scope
        )
    }
}
