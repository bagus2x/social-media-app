package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.data.local.AuthLocalDataSource
import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class GetChatUseCase(
    private val chatRepository: ChatRepository,
    private val authLocalDataSource: AuthLocalDataSource
) {

    operator fun invoke(chatId: Long): Flow<Chat?> {
        return chatRepository.getChat(chatId)
    }

    operator fun invoke(privateChatId: String): Flow<Chat.Private?> {
        return chatRepository.getChat(privateChatId)
    }

    suspend fun privateChatId(pairId: Long): String {
        val authUserId = authLocalDataSource.getAuth().filterNotNull().first().profile.id
        return if (authUserId < pairId) {
            "$authUserId-$pairId"
        } else {
            "$pairId-$authUserId"
        }
    }
}
