package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.repository.CommentRepository

class LoadRepliesUseCase(
    private val commentRepository: CommentRepository
) {

    suspend operator fun invoke(parentId: Long, pageSize: Int) {
        commentRepository.loadReplies(parentId, pageSize)
    }
}
