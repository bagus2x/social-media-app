package bagus2x.sosmed.data.remote

import bagus2x.sosmed.data.common.Misc.HTTP_BASE_URL
import bagus2x.sosmed.data.common.ktor
import bagus2x.sosmed.data.remote.dto.CommentDTO
import bagus2x.sosmed.data.remote.dto.MediaDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

class CommentRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getRootComments(
        feedId: Long,
        nextId: Long,
        limit: Int
    ): List<CommentDTO> = ktor(client) {
        get("$HTTP_BASE_URL/feed/$feedId/comments") {
            url {
                parameters.append("next_id", "$nextId")
                parameters.append("limit", "$limit")
            }
        }.body()
    }

    suspend fun getChildComments(
        parentId: Long,
        nextId: Long,
        limit: Int
    ): List<CommentDTO> = ktor(client) {
        get("$HTTP_BASE_URL/comment/$parentId/replies") {
            url {
                parameters.append("next_id", "$nextId")
                parameters.append("limit", "$limit")
            }
        }.body()
    }

    suspend fun getComment(commentId: Long): CommentDTO = ktor(client) {
        get("$HTTP_BASE_URL/comment/$commentId").body()
    }

    suspend fun create(
        feedId: Long,
        parentId: Long?,
        medias: List<MediaDTO>,
        description: String
    ): CommentDTO = ktor(client) {
        post("$HTTP_BASE_URL/comment") {
            @Serializable
            data class Body(
                val feedId: Long,
                val parentId: Long?,
                val medias: List<MediaDTO>,
                val description: String
            )
            contentType(ContentType.Application.Json)
            val body = Body(feedId, parentId, medias, description)
            setBody(body)
        }.body()
    }
}
