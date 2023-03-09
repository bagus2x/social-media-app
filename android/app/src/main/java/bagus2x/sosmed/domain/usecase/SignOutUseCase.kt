package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.repository.AuthRepository

class SignOutUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke() {
        authRepository.signOut()
    }
}
