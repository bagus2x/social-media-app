package bagus2x.sosmed.data

import androidx.paging.*
import bagus2x.sosmed.data.local.KeyLocalDataSource
import bagus2x.sosmed.data.local.MessageLocalDataSource
import bagus2x.sosmed.data.local.SosmedDatabase
import bagus2x.sosmed.data.remote.MessageRemoteDataSource
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.model.Message
import bagus2x.sosmed.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class MessageRepositoryImpl(
    private val keyLocalDataSource: KeyLocalDataSource,
    private val messageLocalDataSource: MessageLocalDataSource,
    private val messageRemoteDataSource: MessageRemoteDataSource,
    private val database: SosmedDatabase,
    private val dispatcher: CoroutineDispatcher
) : MessageRepository {

    override suspend fun send(chatId: Long, description: String, medias: List<Media>) {
        val dto = messageRemoteDataSource.send(
            chatId = chatId,
            description = description,
            medias = emptyList()
        )
        messageLocalDataSource.save(dto.asEntity())
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getMessages(chatId: Long, pageSize: Int): Flow<PagingData<Message>> {
        val pager = Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { messageLocalDataSource.getMessages(chatId) },
            remoteMediator = MessageRemoteMediator(
                keyLocalDataSource = keyLocalDataSource,
                messageLocalDataSource = messageLocalDataSource,
                messageRemoteDataSource = messageRemoteDataSource,
                chatId = chatId,
                label = "messages_in_chat_id_$chatId",
                database = database
            )
        )
        return pager.flow.map { it.map { entity -> entity.asDomainModel() } }
    }

    override suspend fun connect(chatId: Long) = withContext(dispatcher) {
        messageRemoteDataSource.connect(chatId) { message ->
            messageLocalDataSource.save(message.asEntity())
        }
    }

    override suspend fun disconnect(chatId: Long) = withContext(dispatcher) {
        messageRemoteDataSource.disconnect(chatId)
    }
}
