package bagus2x.sosmed.data.local

import androidx.paging.PagingSource
import androidx.room.*
import bagus2x.sosmed.data.local.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CommentLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(comments: List<CommentEntity>)

    @Query(QUERY_GET_ROOT_COMMENTS)
    abstract fun getRootComments(feedId: Long): PagingSource<Int, CommentEntity>

    @Query(QUERY_GET_CHILD_COMMENTS)
    abstract fun getChildComments(parentId: Long): PagingSource<Int, CommentEntity>

    @Query("SELECT * FROM comment WHERE id = :id")
    abstract fun getComment(id: Long): Flow<CommentEntity?>

    @Query("DELETE FROM comment WHERE feed_id = :feedId")
    abstract suspend fun deleteByFeedId(feedId: Long)

    @Query("SELECT COUNT(*) FROM comment WHERE parent_id = :parentId")
    abstract suspend fun countLoadedReplies(parentId: Long): Int

    companion object {
        private const val QUERY_GET_CHILD_COMMENTS = """
            WITH RECURSIVE nested_comment AS (
                SELECT 
                    *
                FROM 
                    comment
                WHERE 
                    parent_id = :parentId
                UNION ALL
                SELECT 
                    c.*
                FROM 
                    comment c
                INNER JOIN 
                    nested_comment nc
                ON 
                    c.parent_id = nc.id
                 ORDER BY created_at DESC
            )
            SELECT * FROM nested_comment
        """
        private const val QUERY_GET_ROOT_COMMENTS = """
            WITH RECURSIVE nested_comment AS (
                SELECT 
                    *
                FROM 
                    comment
                WHERE 
                    parent_id IS NULL AND feed_id = :feedId
                UNION ALL
                SELECT 
                    c.*
                FROM 
                    comment c
                INNER JOIN 
                    nested_comment nc
                ON 
                    c.parent_id = nc.id
                 ORDER BY created_at DESC
            )
            SELECT * FROM nested_comment
        """
    }
}
