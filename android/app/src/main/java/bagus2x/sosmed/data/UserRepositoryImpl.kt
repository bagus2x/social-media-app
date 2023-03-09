package bagus2x.sosmed.data

import androidx.paging.*
import bagus2x.sosmed.data.common.networkBoundResource
import bagus2x.sosmed.data.local.AuthLocalDataSource
import bagus2x.sosmed.data.local.UserLocalDataSource
import bagus2x.sosmed.data.local.entity.asDomainModel
import bagus2x.sosmed.data.remote.UserRemoteDataSource
import bagus2x.sosmed.data.remote.dto.UserDTO
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.time.LocalDate

class UserRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
) : UserRepository {

    override fun getUser(userId: Long): Flow<User?> {
        return networkBoundResource(
            local = { userLocalDataSource.getUser(userId) },
            remote = { userRemoteDataSource.getUser(userId) },
            update = { user ->
                userLocalDataSource.save(user.asEntity())
            }
        )
            .flowOn(dispatcher)
            .map { it?.asDomainModel() }
    }

    override fun getUser(username: String): Flow<User?> {
        return networkBoundResource(
            local = { userLocalDataSource.getUser(username) },
            remote = { userRemoteDataSource.getUser(username) },
            update = { user ->
                userLocalDataSource.save(user.asEntity())
            }
        )
            .flowOn(dispatcher)
            .map { it?.asDomainModel() }
    }

    override fun searchUsers(query: String, pageSize: Int): Flow<PagingData<User>> {
        val source = object : PagingSource<Long, UserDTO>() {

            override suspend fun load(
                params: LoadParams<Long>
            ): LoadResult<Long, UserDTO> {
                return try {
                    // Start refresh at page 1 if undefined.
                    val nextId = params.key ?: Long.MAX_VALUE
                    val res = userRemoteDataSource.searchUsers(query, nextId, params.loadSize)
                    LoadResult.Page(
                        data = res,
                        prevKey = null, // Only paging forward.
                        nextKey = res.lastOrNull()?.id
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Long, UserDTO>): Long? {
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

    override suspend fun follow(userId: Long) = withContext(dispatcher) {
        userRemoteDataSource.follow(userId)

        val user = userLocalDataSource.getUser(userId).firstOrNull()
        if (user != null) {
            userLocalDataSource.save(
                user.copy(
                    following = true,
                    totalFollowers = user.totalFollowers + 1
                )
            )
        }

        val authUserId = authLocalDataSource.getAuth().filterNotNull().first().profile.id
        val authUser = userLocalDataSource.getUser(userId = authUserId).firstOrNull()
        if (authUser != null) {
            userLocalDataSource.save(user = authUser.copy(totalFollowing = authUser.totalFollowing + 1))
        }
    }

    override suspend fun unfollow(userId: Long) = withContext(dispatcher) {
        userRemoteDataSource.unfollow(userId)
        val user = userLocalDataSource.getUser(userId).firstOrNull()
        if (user != null) {
            userLocalDataSource.save(
                user.copy(
                    following = false,
                    totalFollowers = user.totalFollowers - 1
                )
            )
        }

        val authUserId = authLocalDataSource.getAuth().filterNotNull().first().profile.id
        val authUser = userLocalDataSource.getUser(userId = authUserId).firstOrNull()
        if (authUser != null) {
            userLocalDataSource.save(user = authUser.copy(totalFollowing = authUser.totalFollowing - 1))
        }
    }

    override fun getFollowers(userId: Long, pageSize: Int): Flow<PagingData<User>> {
        val remoteSource = object : PagingSource<Long, UserDTO>() {

            override suspend fun load(
                params: LoadParams<Long>
            ): LoadResult<Long, UserDTO> {
                return try {
                    val nextId = params.key ?: Long.MAX_VALUE
                    val res = userRemoteDataSource.getFollowers(userId, nextId, params.loadSize)
                    LoadResult.Page(
                        data = res,
                        prevKey = null,
                        nextKey = res.lastOrNull()?.id
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Long, UserDTO>): Long? {
                return state.anchorPosition?.let { anchorPosition ->
                    val anchorPage = state.closestPageToPosition(anchorPosition)
                    anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
                }
            }
        }

        return Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { remoteSource }
        ).flow
            .flowOn(dispatcher)
            .map { pagingData -> pagingData.map { it.asDomainModel() } }
    }

    override suspend fun update(
        name: String?,
        email: String?,
        password: String?,
        photo: String?,
        header: String?,
        bio: String?,
        location: String?,
        website: String?,
        dateOfBirth: LocalDate?
    ) {
        val user = userRemoteDataSource.update(
            name = name,
            email = email,
            password = password,
            photo = photo,
            header = header,
            bio = bio,
            location = location,
            website = website,
            dateOfBirth = dateOfBirth
        )
        userLocalDataSource.save(user.asEntity())
    }

    override fun getFollowing(userId: Long, pageSize: Int): Flow<PagingData<User>> {
        val source = object : PagingSource<Long, UserDTO>() {

            override suspend fun load(
                params: LoadParams<Long>
            ): LoadResult<Long, UserDTO> {
                return try {
                    val nextId = params.key ?: Long.MAX_VALUE
                    val res = userRemoteDataSource.getFollowing(userId, nextId, params.loadSize)
                    LoadResult.Page(
                        data = res,
                        prevKey = null,
                        nextKey = res.lastOrNull()?.id
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Long, UserDTO>): Long? {
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
}
