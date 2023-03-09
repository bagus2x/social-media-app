package bagus2x.sosmed.domain.repository

import bagus2x.sosmed.domain.model.Trending
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

interface TrendingRepository {

    fun getTrending(duration: Duration): Flow<List<Trending>>
}
