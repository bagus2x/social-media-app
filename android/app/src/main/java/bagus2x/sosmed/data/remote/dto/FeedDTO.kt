package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.common.Misc
import bagus2x.sosmed.data.local.entity.FeedEntity
import bagus2x.sosmed.domain.model.Feed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedDTO(
    @SerialName("id")
    val id: Long,
    @SerialName("author")
    val author: ProfileDTO,
    @SerialName("description")
    val description: String,
    @SerialName("medias")
    val medias: List<MediaDTO>,
    @SerialName("favorite")
    val favorite: Boolean,
    @SerialName("totalFavorites")
    val totalFavorites: Int,
    @SerialName("totalComments")
    val totalComments: Int,
    @SerialName("totalReposts")
    val totalReposts: Int,
    @SerialName("language")
    val language: String?,
    @SerialName("createdAt")
    val createdAt: Long,
    @SerialName("updatedAt")
    val updatedAt: Long
) {

    fun asEntity(): FeedEntity {
        return FeedEntity(
            id = id,
            author = author.asEntity(),
            medias = medias.map(MediaDTO::asEntity),
            description = description,
            favorite = favorite,
            totalFavorites = totalFavorites,
            totalComments = totalComments,
            totalReposts = totalReposts,
            language = language,
            updatedAt = updatedAt,
            createdAt = createdAt,
        )
    }

    fun asDomainModel(): Feed {
        return Feed(
            id = id,
            author = author.asDomainModel(),
            medias = medias.map(MediaDTO::asDomainModel),
            description = description,
            favorite = favorite,
            totalFavorites = totalFavorites,
            totalComments = totalComments,
            totalReposts = totalReposts,
            language = language,
            updatedAt = Misc.epochMillisToLocalDate(updatedAt),
            createdAt = Misc.epochMillisToLocalDate(updatedAt)
        )
    }
}
