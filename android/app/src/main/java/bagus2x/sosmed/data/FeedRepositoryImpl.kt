package bagus2x.sosmed.data

import androidx.paging.*
import bagus2x.sosmed.data.common.networkBoundResource
import bagus2x.sosmed.data.local.FeedLocalDataSource
import bagus2x.sosmed.data.local.KeyLocalDataSource
import bagus2x.sosmed.data.local.SosmedDatabase
import bagus2x.sosmed.data.remote.FeedRemoteDataSource
import bagus2x.sosmed.data.remote.dto.FeedDTO
import bagus2x.sosmed.data.remote.dto.asDTO
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.repository.FeedRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class FeedRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val keyLocalDataSource: KeyLocalDataSource,
    private val feedLocalDataSource: FeedLocalDataSource,
    private val feedRemoteDataSource: FeedRemoteDataSource,
    private val database: SosmedDatabase
) : FeedRepository {

    override suspend fun save(description: String, medias: List<Media>, language: String?): Feed {
        return withContext(dispatcher) {
            val feedDTO = feedRemoteDataSource.save(
                description = description,
                medias = medias.map { it.asDTO() },
                language = language
            )
            val feedEntity = feedDTO.asEntity()
            feedLocalDataSource.save(feedEntity)
            feedEntity.asDomainModel()
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getFeeds(pageSize: Int): Flow<PagingData<Feed>> {
        val pager = Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { feedLocalDataSource.getFeeds() },
            remoteMediator = FeedRemoteMediator(
                keyLocalDataSource = keyLocalDataSource,
                feedLocalDataSource = feedLocalDataSource,
                feedRemoteDataSource = feedRemoteDataSource,
                label = "feeds",
                database = database
            )
        )
        return pager
            .flow
            .map { it.map { dto -> dto.asDomainModel() } }
            .flowOn(dispatcher)
    }

    override fun getFeeds(authorId: Long, pageSize: Int): Flow<PagingData<Feed>> {
        val source = object : PagingSource<Long, FeedDTO>() {

            override suspend fun load(
                params: LoadParams<Long>
            ): LoadResult<Long, FeedDTO> {
                return try {
                    // Start refresh at page 1 if undefined.
                    val nextId = params.key ?: Long.MAX_VALUE
                    val res = feedRemoteDataSource.getFeeds(authorId, nextId, params.loadSize)
                    LoadResult.Page(
                        data = res,
                        prevKey = null, // Only paging forward.
                        nextKey = res.lastOrNull()?.id
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Long, FeedDTO>): Long? {
                return state.anchorPosition?.let { anchorPosition ->
                    val anchorPage = state.closestPageToPosition(anchorPosition)
                    anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
                }
            }
        }

        return Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { source }
        ).flow
            .flowOn(dispatcher)
            .map { pagingData -> pagingData.map { it.asDomainModel() } }
    }

    override fun searchFeeds(query: String, pageSize: Int): Flow<PagingData<Feed>> {
        val source = object : PagingSource<Long, FeedDTO>() {

            override suspend fun load(
                params: LoadParams<Long>
            ): LoadResult<Long, FeedDTO> {
                return try {
                    // Start refresh at page 1 if undefined.
                    val nextId = params.key ?: Long.MAX_VALUE
                    val res = feedRemoteDataSource.searchFeeds(query, nextId, params.loadSize)
                    LoadResult.Page(
                        data = res,
                        prevKey = null, // Only paging forward.
                        nextKey = res.lastOrNull()?.id
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Long, FeedDTO>): Long? {
                return state.anchorPosition?.let { anchorPosition ->
                    val anchorPage = state.closestPageToPosition(anchorPosition)
                    anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
                }
            }
        }

        return Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { source }
        ).flow
            .flowOn(dispatcher)
            .map { pagingData -> pagingData.map { it.asDomainModel() } }
    }

    override fun getFeed(feedId: Long): Flow<Feed?> {
        return networkBoundResource(
            local = { feedLocalDataSource.getFeed(feedId) },
            remote = { feedRemoteDataSource.getFeed(feedId) },
            update = { feed -> feedLocalDataSource.save(feed.asEntity()) }
        ).map { it?.asDomainModel() }
    }

    override suspend fun favorite(feedId: Long) {
        val feed = feedLocalDataSource.getFeed(feedId).filterNotNull().first()
        feedLocalDataSource.save(
            feed = feed.copy(
                favorite = true,
                totalFavorites = feed.totalFavorites + 1
            )
        )
        feedRemoteDataSource.favorite(feedId)
    }

    override suspend fun unfavorite(feedId: Long) {
        val feed = feedLocalDataSource.getFeed(feedId).filterNotNull().first()
        feedLocalDataSource.save(
            feed = feed.copy(
                favorite = false,
                totalFavorites = feed.totalFavorites - 1
            )
        )
        feedRemoteDataSource.unfavorite(feedId)
    }
}
