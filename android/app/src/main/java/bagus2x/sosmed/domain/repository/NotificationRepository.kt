package bagus2x.sosmed.domain.repository

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun getNotifications(pageSize: Int): Flow<PagingData<Notification>>
}
