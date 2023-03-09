package bagus2x.sosmed.data

import androidx.paging.*
import bagus2x.sosmed.data.common.networkBoundResource
import bagus2x.sosmed.data.local.ChatLocalDataSource
import bagus2x.sosmed.data.local.KeyLocalDataSource
import bagus2x.sosmed.data.local.SosmedDatabase
import bagus2x.sosmed.data.remote.ChatRemoteDataSource
import bagus2x.sosmed.domain.model.Chat
import bagus2x.sosmed.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl(
    private val keyLocalDataSource: KeyLocalDataSource,
    private val chatLocalDataSource: ChatLocalDataSource,
    private val chatRemoteDataSource: ChatRemoteDataSource,
    private val database: SosmedDatabase
) : ChatRepository {

    override suspend fun create(memberId: Long): Chat.Private {
        val chatDto = chatRemoteDataSource.create(
            memberIds = setOf(memberId),
            name = "",
            photo = null,
            type = "private"
        )
        val chatEntity = chatDto.asEntity()
        chatLocalDataSource.save(chatEntity)
        return chatEntity.asDomainModel() as Chat.Private
    }

    override suspend fun create(memberIds: Set<Long>, name: String, photo: String?): Chat.Group {
        val chatDTO = chatRemoteDataSource.create(
            memberIds = memberIds,
            name = name,
            photo = photo,
            type = "group"
        )
        val chatEntity = chatDTO.asEntity()
        chatLocalDataSource.save(chatEntity)
        return chatEntity.asDomainModel() as Chat.Group
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getChats(pageSize: Int): Flow<PagingData<Chat>> {
        val pager = Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { chatLocalDataSource.getChats() },
            remoteMediator = ChatRemoteMediator(
                keyLocalDataSource = keyLocalDataSource,
                chatLocalDataSource = chatLocalDataSource,
                chatRemoteDataSource = chatRemoteDataSource,
                label = "chats",
                database = database
            )
        )

        return pager.flow.map { it.map { entity -> entity.asDomainModel() } }
    }

    override fun getChat(chatId: Long): Flow<Chat?> {
        return networkBoundResource(
            local = { chatLocalDataSource.getChat(chatId) },
            remote = { chatRemoteDataSource.getChat(chatId) },
            update = { feed -> chatLocalDataSource.save(feed.asEntity()) }
        ).map { it?.asDomainModel() }
    }

    override fun getChat(privateChatId: String): Flow<Chat.Private?> {
        return networkBoundResource(
            local = { chatLocalDataSource.getChat(privateChatId) },
            remote = { chatRemoteDataSource.getChat(privateChatId) },
            update = { feed -> chatLocalDataSource.save(feed.asEntity()) }
        ).map { it?.asDomainModel() as? Chat.Private? }
    }
}
