package bagus2x.sosmed.domain.repository

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    // Create private chat
    suspend fun create(memberId: Long): Chat.Private

    // Create group chat
    suspend fun create(memberIds: Set<Long>, name: String, photo: String?): Chat.Group

    fun getChats(pageSize: Int): Flow<PagingData<Chat>>

    fun getChat(chatId: Long): Flow<Chat?>

    fun getChat(privateChatId: String): Flow<Chat.Private?>
}
