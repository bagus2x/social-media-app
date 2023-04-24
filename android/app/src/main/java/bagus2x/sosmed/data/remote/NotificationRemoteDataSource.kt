package bagus2x.sosmed.data.remote

import bagus2x.sosmed.data.common.Misc
import bagus2x.sosmed.data.common.ktor
import bagus2x.sosmed.data.remote.dto.NotificationDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType

class NotificationRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getNotifications(nextId: Long, limit: Int): List<NotificationDTO> {
        return ktor(client) {
            get("${Misc.HTTP_BASE_URL}/notifications"){
                parameter("next_id", nextId)
                parameter("limit", limit)
                accept(ContentType.Application.Json)
            }.body()
        }
    }
}
