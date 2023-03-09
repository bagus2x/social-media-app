package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.Auth
import bagus2x.sosmed.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetAuthUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Flow<Auth?> {
        return authRepository.getAuth()
    }
}
