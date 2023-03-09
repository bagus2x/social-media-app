package bagus2x.sosmed.data

import androidx.room.withTransaction
import bagus2x.sosmed.data.common.networkBoundResource
import bagus2x.sosmed.data.local.SosmedDatabase
import bagus2x.sosmed.data.local.entity.TrendingLocalDataSource
import bagus2x.sosmed.data.remote.TrendingRemoteDataSource
import bagus2x.sosmed.domain.model.Trending
import bagus2x.sosmed.domain.repository.TrendingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

class TrendingRepositoryImpl(
    private val trendingLocalDataSource: TrendingLocalDataSource,
    private val trendingRemoteDataSource: TrendingRemoteDataSource,
    private val database: SosmedDatabase
) : TrendingRepository {

    override fun getTrending(duration: Duration): Flow<List<Trending>> {
        return networkBoundResource(
            local = {
                trendingLocalDataSource.getTrending()
            },
            remote = {
                trendingRemoteDataSource.getTrending(duration.inWholeMilliseconds)
            },
            update = { trending ->
                database.withTransaction {
                    trendingLocalDataSource.deleteAll()
                    trendingLocalDataSource.save(trending.map { it.asEntity() })
                }
            }
        ).map { it.map { trendingEntity -> trendingEntity.asDomainModel() } }
    }
}
