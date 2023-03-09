package bagus2x.sosmed.data.remote

import bagus2x.sosmed.data.common.ktor
import bagus2x.sosmed.data.remote.dto.FeedDTO
import bagus2x.sosmed.data.remote.dto.MediaDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

const val HTTP_BASE_URL = "http://192.168.1.109:8080"

class FeedRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun save(description: String, medias: List<MediaDTO>, language: String?): FeedDTO {
        return ktor(client) {
            post("$HTTP_BASE_URL/feed") {
                @kotlinx.serialization.Serializable
                data class Body(
                    val description: String,
                    val medias: List<MediaDTO>,
                    val language: String?
                )
                contentType(ContentType.Application.Json)
                val body = Body(description, medias, language)
                setBody(body)
            }.body()
        }
    }

    suspend fun getFeeds(nextId: Long, limit: Int): List<FeedDTO> {
        return ktor(client) {
            get("$HTTP_BASE_URL/feeds") {
                parameter("next_id", nextId)
                parameter("limit", limit)
                accept(ContentType.Application.Json)
            }.body()
        }
    }

    suspend fun getFeeds(authorId: Long, nextId: Long, limit: Int): List<FeedDTO> {
        return ktor(client) {
            get("$HTTP_BASE_URL/user/$authorId/feeds") {
                parameter("next_id", nextId)
                parameter("limit", limit)
                accept(ContentType.Application.Json)
            }.body()
        }
    }

    suspend fun searchFeeds(query: String, nextId: Long, limit: Int): List<FeedDTO> {
        return ktor(client) {
            get("$HTTP_BASE_URL/search/feeds") {
                parameter("query", query)
                parameter("next_id", nextId)
                parameter("limit", limit)
                accept(ContentType.Application.Json)
            }.body()
        }
    }

    suspend fun getFeed(feedId: Long): FeedDTO {
        return ktor(client) {
            get("$HTTP_BASE_URL/feed/$feedId") {
                accept(ContentType.Application.Json)
            }.body()
        }
    }

    suspend fun favorite(feedId: Long): Unit = ktor(client) {
        patch("$HTTP_BASE_URL/feed/$feedId/favorite").body()
    }

    suspend fun unfavorite(feedId: Long): Unit = ktor(client) {
        delete("$HTTP_BASE_URL/feed/$feedId/favorite").body()
    }
}
