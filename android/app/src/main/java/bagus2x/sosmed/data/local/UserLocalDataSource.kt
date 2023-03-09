package bagus2x.sosmed.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bagus2x.sosmed.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(user: UserEntity)

    @Query("SELECT * FROM user WHERE id = :userId")
    abstract fun getUser(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM user WHERE id = :username")
    abstract fun getUser(username: String): Flow<UserEntity?>
}
