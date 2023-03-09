package bagus2x.sosmed.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bagus2x.sosmed.data.local.entity.MessageEntity

@Dao
abstract class MessageLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(messages: List<MessageEntity>)

    @Query("SELECT * FROM message WHERE chat_id = :chatId ORDER BY created_at DESC")
    abstract fun getMessages(chatId: Long): PagingSource<Int, MessageEntity>

    @Query("DELETE FROM message WHERE chat_id = :chatId")
    abstract fun deleteByChatId(chatId: Long)
}
