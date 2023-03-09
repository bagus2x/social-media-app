package bagus2x.sosmed.domain.usecase

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow

class GetRootCommentsUseCase(
    private val commentRepository: CommentRepository
) {

    operator fun invoke(feedId: Long): Flow<PagingData<Comment>> {
        return commentRepository.getRootComments(feedId)
    }
}
