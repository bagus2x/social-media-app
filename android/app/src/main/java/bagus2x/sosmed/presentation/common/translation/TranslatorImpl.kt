package bagus2x.sosmed.presentation.common.translation

import android.os.Parcelable
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Parcelize
class TranslatorImpl : Translator, Parcelable {
    override val targetLanguage: String?
        get() {
            val targetLangTag = Locale.getDefault().toLanguageTag()
            return TranslateLanguage
                .fromLanguageTag(
                    targetLangTag
                        .split("-")
                        .getOrNull(0)
                        ?: return null
                )
        }

    override suspend fun sourceLanguage(text: String): String? {
        return suspendCancellableCoroutine { cont ->
            LanguageIdentifier
                .identifyLanguage(text)
                .addOnSuccessListener { cont.resume(if (it == "und") null else it) }
                .addOnFailureListener { cont.resumeWithException(it) }
        }
    }

    override suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): String = suspendCancellableCoroutine { cont ->
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()

        val translator = Translation.getClient(options)
        val downloadOptions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downloadOptions)
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { cont.resume(it) }
                    .addOnFailureListener { cont.resumeWithException(it) }
            }
            .addOnFailureListener { cont.resumeWithException(it) }

        cont.invokeOnCancellation { translator.close() }
    }

    override suspend fun translate(text: String): String? {
        val sourceLanguage = sourceLanguage(text) ?: return null
        val targetLanguage = targetLanguage ?: return null
        return runCatching { translate(text, sourceLanguage, targetLanguage) }.getOrNull()
    }

    companion object {
        private val LanguageIdentifier = LanguageIdentification.getClient()
    }
}
