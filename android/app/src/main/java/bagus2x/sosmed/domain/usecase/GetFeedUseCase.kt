package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow

class GetFeedUseCase(
    private val feedRepository: FeedRepository
) {

    operator fun invoke(feedId: Long): Flow<Feed?> {
        return feedRepository.getFeed(feedId)
    }
}
