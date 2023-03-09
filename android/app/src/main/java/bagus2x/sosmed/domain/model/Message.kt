package bagus2x.sosmed.domain.model

import java.time.LocalDateTime

data class Message(
    val id: Long,
    val chatId: Long,
    val sender: Profile,
    val description: String,
    val medias: List<Media>,
    val createdAt: LocalDateTime
)
