package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.local.entity.ChatEntity
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(ChatResponseSerializer::class)
sealed class ChatDTO {

    @Serializable
    data class Group(
        val id: Long,
        val creator: ProfileDTO,
        val name: String,
        val photo: String?,
        val recentMessages: List<MessageDTO>,
        val type: String,
        val members: List<ProfileDTO>,
        val createdAt: Long,
        val updatedAt: Long,
        val lastMessageSentAt: Long?
    ) : ChatDTO()

    @Serializable
    data class Private(
        val id: Long,
        val privateChatId: String?,
        val pair: ProfileDTO,
        val recentMessages: List<MessageDTO>,
        val type: String,
        val createdAt: Long,
        val lastMessageSentAt: Long?
    ) : ChatDTO()

    fun asEntity(): ChatEntity {
        return when (this) {
            is Group -> ChatEntity(
                id = id,
                privateChatId = null,
                creator = creator.asEntity(),
                name = name,
                photo = photo,
                recentMessages = recentMessages.map { it.asEntity() },
                type = type,
                members = members.map { it.asEntity() },
                createdAt = createdAt,
                updatedAt = updatedAt,
                lastMessageSentAt = lastMessageSentAt,
                pair = null
            )
            is Private -> ChatEntity(
                id = id,
                privateChatId = privateChatId,
                creator = null,
                name = null,
                photo = null,
                recentMessages = recentMessages.map { it.asEntity() },
                type = type,
                members = emptyList(),
                createdAt = createdAt,
                updatedAt = null,
                lastMessageSentAt = lastMessageSentAt,
                pair = pair.asEntity(),
            )
        }
    }
}

object ChatResponseSerializer :
    JsonContentPolymorphicSerializer<ChatDTO>(ChatDTO::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out ChatDTO> {
        return when (element.jsonObject["type"]?.jsonPrimitive?.content) {
            "group" -> ChatDTO.Group.serializer()
            "private" -> ChatDTO.Private.serializer()
            else -> error("ERROR: No Serializer found. Serialization failed.")
        }
    }
}
