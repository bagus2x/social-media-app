package bagus2x.sosmed.domain.usecase

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.repository.AuthRepository
import bagus2x.sosmed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.*

class GetFeedsUseCase(
    private val feedRepository: FeedRepository,
    private val authRepository: AuthRepository
) {

    operator fun invoke(pageSize: Int): Flow<PagingData<Feed>> {
        return feedRepository.getFeeds(pageSize)
    }

    operator fun invoke(authorId: Long?, pageSize: Int): Flow<PagingData<Feed>> {
        return flow {
            val id = authorId ?: authRepository.getAuth().filterNotNull().first().profile.id
            emitAll(feedRepository.getFeeds(authorId = id, pageSize = pageSize))
        }
    }
}
