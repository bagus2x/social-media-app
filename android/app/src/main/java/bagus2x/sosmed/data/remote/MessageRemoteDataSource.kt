package bagus2x.sosmed.data.remote

import bagus2x.sosmed.data.common.Misc.HTTP_BASE_URL
import bagus2x.sosmed.data.common.Misc.WS_BASE_URL
import bagus2x.sosmed.data.common.ktor
import bagus2x.sosmed.data.remote.dto.MediaDTO
import bagus2x.sosmed.data.remote.dto.MessageDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class MessageRemoteDataSource(
    private val client: HttpClient
) {
    private val wsSession = ConcurrentHashMap<Long, DefaultClientWebSocketSession>()
    suspend fun send(chatId: Long, description: String, medias: List<MediaDTO>): MessageDTO {
        return ktor(client) {
            post("$HTTP_BASE_URL/chat/$chatId/message") {
                contentType(ContentType.Application.Json)
                @kotlinx.serialization.Serializable
                data class Body(
                    val chatId: Long, val description: String, val medias: List<MediaDTO>
                )

                val body = Body(chatId, description, medias).let { Json.encodeToString(it) }
                setBody(body)
            }.body()
        }
    }

    suspend fun getMessages(chatId: Long, nextId: Long, limit: Int): List<MessageDTO> {
        return ktor(client) {
            get("$HTTP_BASE_URL/chat/$chatId/messages") {
                url {
                    parameters.append("next_id", "$nextId")
                    parameters.append("limit", "$limit")
                }
            }.body()
        }
    }

    suspend fun connect(chatId: Long, observer: suspend (message: MessageDTO) -> Unit) {
        client.webSocket("$WS_BASE_URL/chat/$chatId/socket") {
            wsSession[chatId] = this
            launch {
                while (true) {
                    delay(5000)
                    send(Frame.Pong(ByteReadPacket.Empty))
                }
            }
            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    val message = Json.decodeFromString<MessageDTO>(frame.readText())
                    observer(message)
                }
            }
        }
    }

    suspend fun disconnect(chatId: Long) {
        wsSession[chatId]?.close()
    }
}
