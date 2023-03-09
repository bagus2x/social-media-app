package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.local.entity.CommentEntity
import bagus2x.sosmed.data.local.entity.ProfileEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDTO(
    @SerialName("id")
    val id: Long,

    @SerialName("feedId")
    val feedId: Long,

    @SerialName("parentId")
    val parentId: Long?,

    @SerialName("path")
    val path: String,

    @SerialName("author")
    val author: ProfileDTO,

    @SerialName("description")
    val description: String,

    @SerialName("medias")
    val medias: List<MediaDTO>,

    @SerialName("pinned")
    val pinned: Boolean = false,

    @SerialName("favorite")
    val favorite: Boolean = false,

    @SerialName("totalFavorites")
    val totalFavorites: Int,

    @SerialName("totalReplies")
    val totalReplies: Int,

    @SerialName("createdAt")
    val createdAt: Long,
) {

    fun asEntity(): CommentEntity {
        return CommentEntity(
            id = id,
            feedId = feedId,
            parentId = parentId,
            path = path,
            author = ProfileEntity(
                id = author.id,
                photo = author.photo,
                username = author.username,
                name = author.name
            ),
            description = description,
            medias = medias.map { it.asEntity() },
            pinned = pinned,
            favorite = favorite,
            totalFavorites = totalFavorites,
            totalReplies = totalReplies,
            createdAt = createdAt
        )
    }
}
