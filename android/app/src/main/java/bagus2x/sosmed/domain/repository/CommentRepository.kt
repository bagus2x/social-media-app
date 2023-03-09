package bagus2x.sosmed.domain.repository

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.model.Media
import kotlinx.coroutines.flow.Flow

interface CommentRepository {

    fun getRootComments(feedId: Long): Flow<PagingData<Comment>>

    fun getChildComments(parentId: Long): Flow<PagingData<Comment>>

    fun getComment(commentId: Long): Flow<Comment?>

    suspend fun create(
        feedId: Long,
        parentId: Long?,
        medias: List<Media>,
        description: String
    )

    suspend fun loadReplies(parentId: Long, pageSize: Int)
}
