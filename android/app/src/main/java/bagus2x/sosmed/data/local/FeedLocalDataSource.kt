package bagus2x.sosmed.data.local

import androidx.paging.PagingSource
import androidx.room.*
import bagus2x.sosmed.data.local.entity.FeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FeedLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(feed: FeedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(feeds: List<FeedEntity>)

    @Query("SELECT * FROM feed ORDER BY created_at DESC")
    abstract fun getFeeds(): PagingSource<Int, FeedEntity>

    @Query("SELECT * FROM feed WHERE id = :feedId")
    abstract fun getFeed(feedId: Long): Flow<FeedEntity?>

    @Query("DELETE FROM feed")
    abstract suspend fun deleteFeeds()
}
