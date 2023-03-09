package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.domain.repository.ChatRepository

class CreateChatUseCase(
    private val chatRepository: ChatRepository
) {

    suspend operator fun invoke(memberId: Long): Chat.Private {
        return chatRepository.create(memberId)
    }

    suspend operator fun invoke(memberIds: Set<Long>, name: String, photo: String?): Chat.Group {
        require(memberIds.isNotEmpty())
        return chatRepository.create(memberIds, name, photo)
    }
}
