package bagus2x.sosmed.domain.usecase

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow

class SearchFeedsUseCase(
    private val feedRepository: FeedRepository
) {

    operator fun invoke(query: String, pageSize: Int = 20): Flow<PagingData<Feed>> {
        return feedRepository.searchFeeds(query, pageSize)
    }
}
