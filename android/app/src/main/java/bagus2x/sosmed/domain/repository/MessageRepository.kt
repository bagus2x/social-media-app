package bagus2x.sosmed.domain.repository

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun send(chatId: Long, description: String, medias: List<Media>)

    fun getMessages(chatId: Long, pageSize: Int): Flow<PagingData<Message>>

    suspend fun connect(chatId: Long)

    suspend fun disconnect(chatId: Long)
}
