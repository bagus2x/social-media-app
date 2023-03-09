package bagus2x.sosmed.data.remote

import bagus2x.sosmed.data.common.ktor
import bagus2x.sosmed.data.remote.dto.AuthDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun signUp(email: String, username: String, password: String): AuthDTO {
        return ktor(client) {
            post("$HTTP_BASE_URL/auth/signup") {
                contentType(ContentType.Application.Json)
                val body = mapOf(
                    "email" to email,
                    "username" to username,
                    "password" to password
                )
                setBody(body)
            }.body()
        }
    }

    suspend fun signIn(username: String, password: String): AuthDTO {
        return ktor(client) {
            post("$HTTP_BASE_URL/auth/signin") {
                contentType(ContentType.Application.Json)
                val body = mapOf(
                    "username" to username,
                    "password" to password
                )
                setBody(body)
            }.body()
        }
    }
}
