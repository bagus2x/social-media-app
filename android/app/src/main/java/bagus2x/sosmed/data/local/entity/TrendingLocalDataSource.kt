package bagus2x.sosmed.data.local.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TrendingLocalDataSource {

    @Insert
    abstract suspend fun save(trending: List<TrendingEntity>)

    @Query("SELECT * FROM trending")
    abstract fun getTrending(): Flow<List<TrendingEntity>>

    @Query("DELETE FROM trending")
    abstract suspend fun deleteAll()
}
