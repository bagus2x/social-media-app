package bagus2x.sosmed.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bagus2x.sosmed.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ChatLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun save(chats: List<ChatEntity>)

    @Query("SELECT * FROM chat ORDER BY last_message_sent_at DESC, created_at DESC")
    abstract fun getChats(): PagingSource<Int, ChatEntity>

    @Query("SELECT * FROM chat WHERE id = :chatId")
    abstract fun getChat(chatId: Long): Flow<ChatEntity?>

    @Query("SELECT * FROM chat WHERE private_chat_id = :privateChatId")
    abstract fun getChat(privateChatId: String): Flow<ChatEntity?>

    @Query("DELETE FROM chat")
    abstract suspend fun deleteAllChats()
}
