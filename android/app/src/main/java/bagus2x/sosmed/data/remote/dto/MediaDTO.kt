package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.local.entity.MediaEntity
import bagus2x.sosmed.domain.model.Media
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaDTO(
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("videoUrl")
    val videoUrl: String = "",
    @SerialName("type")
    val type: String
) {

    fun asEntity(): MediaEntity {
        return MediaEntity(
            imageUrl = imageUrl,
            videoUrl = videoUrl,
            type = type
        )
    }

    fun asDomainModel(): Media {
        return when (type) {
            "image" -> Media.Image(imageUrl)
            "video" -> Media.Video(thumbnailUrl = imageUrl, videoUrl = videoUrl)
            else -> error("type is not correct: $type")
        }
    }
}

fun Media.asDTO(): MediaDTO {
    return when (this) {
        is Media.Image -> MediaDTO(imageUrl = imageUrl, type = "image")
        is Media.Video -> MediaDTO(imageUrl = thumbnailUrl, videoUrl = videoUrl, type = "video")
    }
}
