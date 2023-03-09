package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.domain.repository.AuthRepository
import bagus2x.sosmed.domain.repository.UserRepository
import kotlinx.coroutines.flow.*

class GetUserUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) {

    operator fun invoke(userId: Long? = null): Flow<User?> {
        return flow {
            val id = userId ?: authRepository.getAuth().filterNotNull().first().profile.id
            emitAll(userRepository.getUser(userId = id))
        }
    }
}
