package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.local.entity.MessageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDTO(
    @SerialName("id")
    val id: Long,
    @SerialName("chatId")
    val chatId: Long,
    @SerialName("sender")
    val sender: ProfileDTO,
    @SerialName("description")
    val description: String,
    @SerialName("medias")
    val medias: List<MediaDTO>,
    @SerialName("createdAt")
    val createdAt: Long
) {

    fun asEntity(): MessageEntity {
        return MessageEntity(
            id = id,
            chatId = chatId,
            sender = sender.asEntity(),
            description = description,
            medias = medias.map { it.asEntity() },
            createdAt = createdAt
        )
    }
}
