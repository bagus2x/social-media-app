package bagus2x.sosmed.domain.usecase

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow

class GetChildCommentsUseCase(
    private val commentRepository: CommentRepository
) {

    operator fun invoke(parentId: Long): Flow<PagingData<Comment>> {
        return commentRepository.getChildComments(parentId)
    }
}
