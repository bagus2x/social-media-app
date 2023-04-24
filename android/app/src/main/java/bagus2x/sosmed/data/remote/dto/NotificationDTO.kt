package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.local.entity.NotificationEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationDTO(
    @SerialName("id")
    val id: Long,

    @SerialName("issuer")
    val issuer: ProfileDTO?,

    @SerialName("icon")
    val icon: String?,

    @SerialName("description")
    val description: String,

    @SerialName("medias")
    val medias: List<MediaDTO>,

    @SerialName("dataId")
    val dataId: Long?,

    @SerialName("type")
    val type: String,

    @SerialName("seen")
    val seen: Boolean,

    @SerialName("createdAt")
    val createdAt: Long,
) {

    fun asEntity(): NotificationEntity {
        return NotificationEntity(
            id = id,
            issuer = issuer?.asEntity(),
            icon = icon,
            description = description,
            medias = medias.map(MediaDTO::asEntity),
            dataId = dataId,
            type = type,
            seen = seen,
            createdAt = createdAt
        )
    }
}
