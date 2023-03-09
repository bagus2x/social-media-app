package bagus2x.sosmed.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.model.Profile

@kotlinx.serialization.Serializable
data class ProfileEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("photo")
    val photo: String?,
    @ColumnInfo("username")
    val username: String,
    @ColumnInfo("name")
    val name: String
) {

    fun asDomainModel(): Profile {
        return Profile(
            id = id,
            photo = photo,
            username = username,
            name = name
        )
    }
}

@kotlinx.serialization.Serializable
data class MediaEntity(
    @ColumnInfo("image_url")
    val imageUrl: String,
    @ColumnInfo("video_url")
    val videoUrl: String = "",
    @ColumnInfo("type")
    val type: String
) {

    fun asDomainModel(): Media {
        return when (type) {
            "image" -> Media.Image(imageUrl = imageUrl)
            "video" -> Media.Video(thumbnailUrl = imageUrl, videoUrl = videoUrl)
            else -> throw IllegalStateException("Type $type is not recognized")
        }
    }
}

fun Media.asEntitiy(): MediaEntity {
    return when (this) {
        is Media.Image -> MediaEntity(imageUrl = imageUrl, type = "image")
        is Media.Video -> MediaEntity(imageUrl = thumbnailUrl, videoUrl = videoUrl, type = "video")
    }
}
