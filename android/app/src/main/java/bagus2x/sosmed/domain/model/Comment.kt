package bagus2x.sosmed.domain.model

import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val feedId: Long,
    val parentId: Long?,
    val path: String,
    val author: Profile,
    val description: String,
    val medias: List<Media>,
    val pinned: Boolean,
    val favorite: Boolean,
    val totalFavorites: Int,
    val totalReplies: Int,
    val totalLoadedReplies: Int,
    val createdAt: LocalDateTime
) {
    val pathSize: Int
        get() = path.split(".").size
}
