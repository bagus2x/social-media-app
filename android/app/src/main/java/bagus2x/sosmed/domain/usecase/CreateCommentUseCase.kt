package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.repository.CommentRepository

class CreateCommentUseCase(
    private val commentRepository: CommentRepository
) {

    suspend operator fun invoke(
        feedId: Long,
        parentId: Long?,
        medias: List<Media>,
        description: String
    ) {
        commentRepository.create(feedId, parentId, medias, description)
    }
}
