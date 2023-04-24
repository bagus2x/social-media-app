package bagus2x.sosmed.domain.usecase

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Notification
import bagus2x.sosmed.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetNotificationUsecase(
    private val notificationRepository: NotificationRepository
) {

    operator fun invoke(pageSize: Int = 10): Flow<PagingData<Notification>> {
        return notificationRepository.getNotifications(pageSize)
    }
}
