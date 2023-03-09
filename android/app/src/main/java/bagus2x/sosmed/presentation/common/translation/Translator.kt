package bagus2x.sosmed.presentation.common.translation

interface Translator {

    val targetLanguage: String?

    suspend fun sourceLanguage(text: String): String?

    suspend fun translate(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): String

    suspend fun translate(text: String): String?
}
