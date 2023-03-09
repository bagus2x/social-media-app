package bagus2x.sosmed.domain.usecase

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Message
import bagus2x.sosmed.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow

class ObserveMessagesUseCase(
    private val messageRepository: MessageRepository
) {

    operator fun invoke(chatId: Long, pageSize: Int): Flow<PagingData<Message>> {
        return messageRepository.getMessages(chatId, pageSize)
    }

    suspend fun connect(chatId: Long) {
        messageRepository.connect(chatId)
    }

    suspend fun disconnect(chatId: Long) {
        messageRepository.disconnect(chatId)
    }
}
