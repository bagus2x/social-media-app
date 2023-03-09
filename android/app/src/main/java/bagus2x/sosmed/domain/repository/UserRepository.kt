package bagus2x.sosmed.domain.repository

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface UserRepository {

    fun getUser(userId: Long): Flow<User?>

    fun getUser(username: String): Flow<User?>

    fun searchUsers(query: String, pageSize: Int): Flow<PagingData<User>>

    suspend fun follow(userId: Long)

    suspend fun unfollow(userId: Long)

    fun getFollowing(userId: Long, pageSize: Int): Flow<PagingData<User>>

    fun getFollowers(userId: Long, pageSize: Int): Flow<PagingData<User>>

    suspend fun update(
        name: String?,
        email: String?,
        password: String?,
        photo: String?,
        header: String?,
        bio: String?,
        location: String?,
        website: String?,
        dateOfBirth: LocalDate?
    )
}
