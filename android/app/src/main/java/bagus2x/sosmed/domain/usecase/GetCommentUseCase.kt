package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow

class GetCommentUseCase(
    private val commentRepository: CommentRepository
) {

    operator fun invoke(id: Long): Flow<Comment?> {
        return commentRepository.getComment(id)
    }
}
