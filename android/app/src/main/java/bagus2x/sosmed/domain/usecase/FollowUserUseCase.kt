package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.repository.UserRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class FollowUserUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(userId: Long) {
        val user = userRepository.getUser(userId).filterNotNull().first()
        if (user.following) {
            userRepository.unfollow(userId)
        } else {
            userRepository.follow(userId)
        }
    }
}
