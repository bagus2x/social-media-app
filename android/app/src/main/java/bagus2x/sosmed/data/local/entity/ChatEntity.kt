package bagus2x.sosmed.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import bagus2x.sosmed.domain.model.Chat
import java.time.Instant
import java.time.ZoneId

@Entity(tableName = "chat")
data class ChatEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("private_chat_id")
    val privateChatId: String?,
    @Embedded("creator_")
    val creator: ProfileEntity?,
    @ColumnInfo("name")
    val name: String?,
    @ColumnInfo("photo")
    val photo: String?,
    @ColumnInfo("recent_messages")
    val recentMessages: List<MessageEntity>,
    @ColumnInfo("type")
    val type: String,
    @ColumnInfo("members")
    val members: List<ProfileEntity>,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long?,
    @ColumnInfo("last_message_sent_at")
    val lastMessageSentAt: Long?,
    @Embedded("pair_")
    val pair: ProfileEntity?,
) {

    fun asDomainModel(): Chat {
        return when (type) {
            "group" -> Chat.Group(
                id = id,
                creator = requireNotNull(creator?.asDomainModel()) { "Creator should not be null" },
                name = requireNotNull(name) { "Creator should not be null" },
                photo = photo,
                recentMessages = recentMessages.map { it.asDomainModel() },
                members = members.map { it.asDomainModel() },
                createdAt = Instant
                    .ofEpochMilli(createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime(),
                updatedAt = Instant
                    .ofEpochMilli(requireNotNull(updatedAt) { "Updated at should not be null" })
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
            )
            "private" -> Chat.Private(
                id = id,
                pair = requireNotNull(pair?.asDomainModel()) { "Pair should not be null" },
                recentMessages = recentMessages.map { it.asDomainModel() },
                createdAt = Instant
                    .ofEpochMilli(createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime(),
            )
            else -> throw IllegalStateException("Type $type is not recognized")
        }
    }
}
