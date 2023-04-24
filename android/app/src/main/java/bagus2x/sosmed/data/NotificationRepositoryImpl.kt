package bagus2x.sosmed.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import bagus2x.sosmed.data.local.KeyLocalDataSource
import bagus2x.sosmed.data.local.NotificationLocalDataSource
import bagus2x.sosmed.data.local.SosmedDatabase
import bagus2x.sosmed.data.remote.NotificationRemoteDataSource
import bagus2x.sosmed.domain.model.Notification
import bagus2x.sosmed.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(
    private val keyLocalDataSource: KeyLocalDataSource,
    private val notificationLocalDataSource: NotificationLocalDataSource,
    private val notificationRemoteDataSource: NotificationRemoteDataSource,
    private val database: SosmedDatabase
) : NotificationRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getNotifications(pageSize: Int): Flow<PagingData<Notification>> {
        val pager = Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { notificationLocalDataSource.getNotifications() },
            remoteMediator = NotificationRemoteMediator(
                keyLocalDataSource = keyLocalDataSource,
                notificationLocalDataSource = notificationLocalDataSource,
                notificationRemoteDataSource = notificationRemoteDataSource,
                label = "notifications",
                database = database
            )
        )
        return pager.flow.map { it.map { entity -> entity.asDomainModel() } }
    }
}
