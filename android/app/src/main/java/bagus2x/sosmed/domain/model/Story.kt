package bagus2x.sosmed.domain.model

import java.time.LocalDateTime

data class Story(
    val id: Long,
    val author: Author,
    val medias: List<Media>,
    val createdAt: LocalDateTime,
) {

    val unseen: Boolean
        get() = medias.any { media ->
            when (media) {
                is Media.Image -> media.unseen
                is Media.Video -> media.unseen
            }
        }

    data class Author(
        val userId: Long,
        val photo: String?,
        val username: String,
    )
}
