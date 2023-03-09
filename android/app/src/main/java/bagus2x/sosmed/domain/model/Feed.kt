package bagus2x.sosmed.domain.model

import java.time.LocalDateTime

data class Feed(
    val id: Long,
    val author: Profile,
    val medias: List<Media>,
    val description: String,
    val favorite: Boolean,
    val totalFavorites: Int,
    val totalComments: Int,
    val totalReposts: Int,
    val language: String?,
    val updatedAt: LocalDateTime,
    val createdAt: LocalDateTime
) {

    data class Author(
        val id: Long,
        val photo: String?,
        val username: String,
        val name: String?
    )
}
