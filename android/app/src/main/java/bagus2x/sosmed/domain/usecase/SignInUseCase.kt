package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.repository.AuthRepository

class SignInUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(email: String, password: String) {
        require(email.isNotBlank())
        require(password.isNotBlank())
        authRepository.signIn(email, password)
    }
}
