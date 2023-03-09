package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.repository.MessageRepository

class SendMessageUseCase(
    private val messageRepository: MessageRepository
) {

    suspend operator fun invoke(chatId: Long, description: String, medias:List<Media>) {
        require(description.isNotBlank() || medias.isNotEmpty())
        messageRepository.send(chatId, description, medias)
    }
}
