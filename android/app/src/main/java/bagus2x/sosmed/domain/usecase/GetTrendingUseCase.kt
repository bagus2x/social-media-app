package bagus2x.sosmed.domain.usecase

import bagus2x.sosmed.domain.model.Trending
import bagus2x.sosmed.domain.repository.TrendingRepository
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

class GetTrendingUseCase(
    private val trendingRepository: TrendingRepository
) {

    operator fun invoke(duration: Duration): Flow<List<Trending>> {
        return trendingRepository.getTrending(duration)
    }
}
