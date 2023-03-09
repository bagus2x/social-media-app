package bagus2x.sosmed.data

import androidx.paging.*
import bagus2x.sosmed.data.common.networkBoundResource
import bagus2x.sosmed.data.local.CommentLocalDataSource
import bagus2x.sosmed.data.local.FeedLocalDataSource
import bagus2x.sosmed.data.local.KeyLocalDataSource
import bagus2x.sosmed.data.local.SosmedDatabase
import bagus2x.sosmed.data.local.entity.asDomainModel
import bagus2x.sosmed.data.remote.CommentRemoteDataSource
import bagus2x.sosmed.data.remote.dto.asDTO
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.repository.CommentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class CommentRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val keyLocalDataSource: KeyLocalDataSource,
    private val commentLocalDataSource: CommentLocalDataSource,
    private val commentRemoteDataSource: CommentRemoteDataSource,
    private val feedLocalDataSource: FeedLocalDataSource,
    private val database: SosmedDatabase,
) : CommentRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getRootComments(feedId: Long): Flow<PagingData<Comment>> {
        val pager = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = { commentLocalDataSource.getRootComments(feedId) },
            remoteMediator = RootCommentsRemoteMediator(
                keyLocalDataSource = keyLocalDataSource,
                commentLocalDataSource = commentLocalDataSource,
                commentRemoteDataSource = commentRemoteDataSource,
                database = database,
                feedId = feedId,
                label = "comment-root-$feedId"
            )
        )
        return pager
            .flow
            .flowOn(dispatcher)
            .map { data ->
                data.map { comment ->
                    val totalLoadedReplies = commentLocalDataSource.countLoadedReplies(comment.id)
                    comment.asDomainModel(totalLoadedReplies)
                }
            }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getChildComments(parentId: Long): Flow<PagingData<Comment>> {
        val pager = Pager(
            config = PagingConfig(20),
            pagingSourceFactory = { commentLocalDataSource.getChildComments(parentId) },
            remoteMediator = ChildCommentsRemoteMediator(
                keyLocalDataSource = keyLocalDataSource,
                commentLocalDataSource = commentLocalDataSource,
                commentRemoteDataSource = commentRemoteDataSource,
                database = database,
                parentId = parentId,
                label = "comment-child-$parentId"
            )
        )
        return pager
            .flow
            .flowOn(dispatcher)
            .map { data ->
                data.map { comment ->
                    val totalLoadedReplies = commentLocalDataSource.countLoadedReplies(comment.id)
                    comment.asDomainModel(totalLoadedReplies)
                }
            }
    }

    override fun getComment(commentId: Long): Flow<Comment?> {
        return networkBoundResource(
            local = {
                commentLocalDataSource.getComment(commentId)
            },
            remote = {
                commentRemoteDataSource.getComment(commentId)
            },
            update = { commentDTO ->
                commentLocalDataSource.save(comment = commentDTO.asEntity())
            }
        ).map { comment ->
            if (comment != null) {
                val totalLoadedReplies = commentLocalDataSource.countLoadedReplies(commentId)
                comment.asDomainModel(totalLoadedReplies)
            } else {
                null
            }
        }
    }

    override suspend fun create(
        feedId: Long,
        parentId: Long?,
        medias: List<Media>,
        description: String
    ) = withContext(dispatcher) {
        val comment = commentRemoteDataSource.create(
            feedId = feedId,
            parentId = parentId,
            medias = medias.map { it.asDTO() },
            description = description
        )
        val feed = feedLocalDataSource.getFeed(feedId).filterNotNull().first()
        feedLocalDataSource.save(feed.copy(totalComments = feed.totalComments + 1))

        if (parentId != null) {
            val parent = commentLocalDataSource.getComment(parentId).filterNotNull().first()
            commentLocalDataSource.save(parent.copy(totalReplies = parent.totalReplies + 1))
        }

        commentLocalDataSource.save(comment.asEntity())
    }

    override suspend fun loadReplies(parentId: Long, pageSize: Int) {
        val comments = commentRemoteDataSource.getChildComments(
            parentId = parentId,
            nextId = Long.MAX_VALUE,
            limit = pageSize
        )
        commentLocalDataSource.save(comments.map { it.asEntity() })
    }
}
