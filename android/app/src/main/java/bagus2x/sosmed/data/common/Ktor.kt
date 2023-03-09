package bagus2x.sosmed.data.common

import bagus2x.sosmed.data.local.AuthLocalDataSource
import bagus2x.sosmed.data.remote.dto.ErrorDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json

suspend inline fun <T> ktor(client: HttpClient, block: HttpClient.() -> T): T {
    return try {
        client.tryClearToken()
        client.block()
    } catch (e: ClientRequestException) {
        val error = e.response.body<ErrorDTO>()
        error(error.message)
    } catch (e: ServerResponseException) {
        val error = e.response.body<ErrorDTO>()
        error(error.message)
    }
}

fun HttpClient.tryClearToken() {
    plugin(Auth).providers
        .filterIsInstance<BearerAuthProvider>()
        .firstOrNull()
        ?.clearToken()
}

fun HttpClient(authLocalDataSource: AuthLocalDataSource) = HttpClient(CIO) {
    expectSuccess = true
    install(WebSockets)
    install(Logging) {
        logger = Logger.ANDROID
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                encodeDefaults = true
            }
        )
    }
    install(Auth) {
        bearer {
            loadTokens {
                val token = authLocalDataSource.getAuth().firstOrNull()
                val (accessToken, _, _) = token ?: return@loadTokens null
                BearerTokens(accessToken, accessToken)
            }
        }
    }
}
