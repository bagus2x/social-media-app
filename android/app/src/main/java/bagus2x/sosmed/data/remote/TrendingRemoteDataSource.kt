package bagus2x.sosmed.data.remote

import bagus2x.sosmed.data.common.ktor
import bagus2x.sosmed.data.remote.dto.TrendingDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class TrendingRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getTrending(durationMillis: Long): List<TrendingDTO> {
        return ktor(client) {
            get("$HTTP_BASE_URL/tag/trending/$durationMillis").body()
        }
    }
}
