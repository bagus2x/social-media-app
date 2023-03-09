package bagus2x.sosmed.domain.usecase

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SearchUsersUseCase(
    private val userRepository: UserRepository
) {

    operator fun invoke(query: String, pageSize: Int = 20): Flow<PagingData<User>> {
        return userRepository.searchUsers(query, pageSize)
    }
}
