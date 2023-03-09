package bagus2x.sosmed.domain.model

import java.time.LocalDateTime

sealed class Chat {
    val chatId
        get() = when (this) {
            is Group -> id
            is Private -> id
        }

    data class Group(
        val id: Long,
        val creator: Profile,
        val name: String,
        val photo: String?,
        val recentMessages: List<Message>,
        val members: List<Profile>,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    ) : Chat()

    data class Private(
        val id: Long,
        val pair: Profile,
        val recentMessages: List<Message>,
        val createdAt: LocalDateTime
    ) : Chat()
}
