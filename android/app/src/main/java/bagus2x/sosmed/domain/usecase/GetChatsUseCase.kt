package bagus2x.sosmed.domain.usecase

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatsUseCase(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(pageSize: Int = 20): Flow<PagingData<Chat>> {
        return chatRepository.getChats(pageSize)
    }
}
