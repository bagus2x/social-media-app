package bagus2x.sosmed.presentation.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

data class OpenGraphResult(
    val title: String,
    val description: String,
    val image: String,
    val url: String
)

suspend fun getOpenGraph(url: String): OpenGraphResult = withContext(Dispatchers.IO) {
    val doc = Jsoup.connect(url)
        .userAgent("Mozilla")
        .get()

    val title = doc.select("meta[property=og:title]").first()?.attr("content")
    val description = doc.select("meta[property=og:description]").first()?.attr("content")
    val image = doc.select("meta[property=og:image]").first()?.attr("content")

    OpenGraphResult(
        title = requireNotNull(title) { "Title must be not null" },
        description = requireNotNull(description) { "Description must be not null" },
        image = requireNotNull(image) { "Image must be not null" },
        url = url
    )
}

