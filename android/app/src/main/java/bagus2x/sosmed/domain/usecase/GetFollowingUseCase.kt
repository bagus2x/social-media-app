package bagus2x.sosmed.domain.usecase

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.domain.repository.AuthRepository
import bagus2x.sosmed.domain.repository.UserRepository
import kotlinx.coroutines.flow.*

class GetFollowingUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(userId: Long? = null, pageSize: Int = 20): Flow<PagingData<User>> {
        return flow {
            val id = userId ?: authRepository.getAuth().filterNotNull().first().profile.id
            emitAll(userRepository.getFollowing(userId = id, pageSize = pageSize))
        }
    }
}
