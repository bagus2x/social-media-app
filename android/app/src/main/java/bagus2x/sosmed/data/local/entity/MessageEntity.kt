package bagus2x.sosmed.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import bagus2x.sosmed.domain.model.Message
import java.time.Instant
import java.time.ZoneId

@Entity("message")
@kotlinx.serialization.Serializable
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("chat_id")
    val chatId: Long,
    @Embedded("sender_")
    val sender: ProfileEntity,
    @ColumnInfo("description")
    val description: String,
    @ColumnInfo("medias")
    val medias: List<MediaEntity>,
    @ColumnInfo("created_at")
    val createdAt: Long
) {

    fun asDomainModel(): Message {
        return Message(
            id = id,
            chatId = chatId,
            sender = sender.asDomainModel(),
            description = description,
            medias = medias.map { it.asDomainModel() },
            createdAt = Instant
                .ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        )
    }
}
