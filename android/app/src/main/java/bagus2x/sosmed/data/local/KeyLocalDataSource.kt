package bagus2x.sosmed.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bagus2x.sosmed.data.local.entity.KeyEntity

@Dao
abstract class KeyLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrReplace(remoteKey: KeyEntity)

    @Query("SELECT * FROM `key` WHERE label = :label")
    abstract suspend fun remoteKeyByLabel(label: String): KeyEntity?

    @Query("DELETE FROM `key` WHERE label = :label")
    abstract suspend fun deleteByLabel(label: String)
}
