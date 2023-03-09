package bagus2x.sosmed.data.remote

import bagus2x.sosmed.data.common.ktor
import bagus2x.sosmed.data.remote.dto.UserDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.time.LocalDate

class UserRemoteDataSource(
    private val client: HttpClient
) {

    suspend fun getUser(userId: Long): UserDTO {
        return ktor(client) {
            get("$HTTP_BASE_URL/user/$userId") {
                accept(ContentType.Application.Json)
            }.body()
        }
    }

    suspend fun getUser(username: String): UserDTO {
        return ktor(client) {
            get("$HTTP_BASE_URL/user/$username") {
                accept(ContentType.Application.Json)
            }.body()
        }
    }

    suspend fun searchUsers(query: String, nextId: Long, limit: Int): List<UserDTO> {
        return ktor(client) {
            get("$HTTP_BASE_URL/search/users") {
                parameter("query", query)
                parameter("next_id", nextId)
                parameter("limit", limit)
                accept(ContentType.Application.Json)
            }.body()
        }
    }

    suspend fun follow(userId: Long) {
        return ktor(client) {
            patch("$HTTP_BASE_URL/user/$userId/following")
        }
    }

    suspend fun unfollow(userId: Long) {
        return ktor(client) {
            delete("$HTTP_BASE_URL/user/$userId/following")
        }
    }

    suspend fun getFollowers(userId: Long, nextId: Long, limit: Int): List<UserDTO> {
        return ktor(client) {
            get("$HTTP_BASE_URL/user/$userId/followers") {
                parameter("next_id", nextId)
                parameter("limit", limit)
            }.body()
        }
    }

    suspend fun getFollowing(userId: Long, nextId: Long, limit: Int): List<UserDTO> {
        return ktor(client) {
            get("$HTTP_BASE_URL/user/$userId/following") {
                parameter("next_id", nextId)
                parameter("limit", limit)
            }.body()
        }
    }

    suspend fun updateFcmToken(token: String) {
        return ktor(client) {
            patch("$HTTP_BASE_URL/user/fcm-token") {
                contentType(ContentType.Application.Json)
                val body = mapOf("fcmToken" to token)
                setBody(body)
            }.body()
        }
    }

    suspend fun update(
        name: String?,
        email: String?,
        password: String?,
        photo: String?,
        header: String?,
        bio: String?,
        location: String?,
        website: String?,
        dateOfBirth: LocalDate?
    ): UserDTO {
        return ktor(client) {
            patch("$HTTP_BASE_URL/user") {
                val body = mutableMapOf<String, String?>()
                if (!name.isNullOrBlank()) {
                    body["name"] = name
                }
                if (!email.isNullOrBlank()) {
                    body["email"] = email
                }
                if (!password.isNullOrBlank()) {
                    body["password"] = password
                }
                if (!photo.isNullOrBlank()) {
                    body["photo"] = photo
                }
                if (!header.isNullOrBlank()) {
                    body["header"] = header
                }
                if (!bio.isNullOrBlank()) {
                    body["bio"] = bio
                }
                if (!location.isNullOrBlank()) {
                    body["location"] = location
                }
                if (!website.isNullOrBlank()) {
                    body["website"] = website
                }
                if (!name.isNullOrBlank()) {
                    body["dateOfBirth"] = dateOfBirth?.toString()
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body()
        }
    }
}
