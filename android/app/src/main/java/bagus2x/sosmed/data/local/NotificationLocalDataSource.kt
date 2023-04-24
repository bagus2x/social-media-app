package bagus2x.sosmed.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import bagus2x.sosmed.data.local.entity.NotificationEntity

@Dao
abstract class NotificationLocalDataSource {

    @Insert
    abstract fun save(notifications: List<NotificationEntity>)

    @Query("SELECT * FROM notification ORDER BY created_at DESC")
    abstract fun getNotifications(): PagingSource<Int, NotificationEntity>

    @Query("DELETE FROM notification")
    abstract fun deleteNotifications()
}
