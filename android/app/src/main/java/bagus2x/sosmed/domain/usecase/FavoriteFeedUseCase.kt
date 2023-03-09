package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class FavoriteFeedUseCase(
    private val feedRepository: FeedRepository
) {

    suspend operator fun invoke(feedId: Long) {
        val feed = feedRepository.getFeed(feedId).filterNotNull().first()
        if (feed.favorite) {
            feedRepository.unfavorite(feedId)
        } else {
            feedRepository.favorite(feedId)
        }
    }
}
