package bagus2x.sosmed.data.remote

import bagus2x.sosmed.data.common.ktor
import bagus2x.sosmed.data.remote.dto.ChatDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ChatRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun create(
        memberIds: Set<Long>,
        name: String,
        photo: String?,
        type: String
    ): ChatDTO {
        return ktor(client) {
            post("$HTTP_BASE_URL/chat") {
                contentType(ContentType.Application.Json)
                @Serializable
                data class Body(
                    val members: Set<Long>,
                    val name: String,
                    val photo: String?,
                    val type: String
                )

                val body = Body(memberIds, name, photo, type).let(Json::encodeToString)
                setBody(body)
            }.body()
        }
    }

    suspend fun getChats(nextId: Long, limit: Int): List<ChatDTO> {
        return ktor(client) {
            get("$HTTP_BASE_URL/chats") {
                url {
                    parameters.append("next_id", "$nextId")
                    parameters.append("limit", "$limit")
                }
            }.body()
        }
    }

    suspend fun getChat(chatId: Long): ChatDTO {
        return ktor(client) {
            get("$HTTP_BASE_URL/chat/$chatId").body()
        }
    }

    suspend fun getChat(privateChatId: String): ChatDTO.Private {
        return ktor(client) {
            get("$HTTP_BASE_URL/chat/$privateChatId").body()
        }
    }
}
