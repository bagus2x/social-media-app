package bagus2x.sosmed.domain.model

import java.time.LocalDateTime

data class Notification(
    val id: Long,
    val icon: String?,
    val description: String,
    val medias: List<Media>,
    val type: Type,
    val createdAt: LocalDateTime
) {

    sealed class Type {

        data class FeedLiked(
            val issuer: Profile,
            val feedId: Long
        ) : Type()

        data class FeedCommented(
            val issuer: Profile,
            val commentId: Long
        ) : Type()

        data class CommentReplied(
            val issuer: Profile,
            val commentId: Long
        ) : Type()

        data class UserFollowing(
            val issuer: Profile
        ) : Type()

        object Other : Type()
    }
}
