package bagus2x.sosmed.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class User(
    val id: Long,
    val username: String,
    val name: String,
    val email: String,
    val photo: String?,
    val header: String?,
    val bio: String?,
    val location: String?,
    val website: String?,
    val verified: Boolean,
    val dateOfBirth: LocalDate?,
    val totalFollowers: Int,
    val totalFollowing: Int,
    val following: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {

    fun asProfile(): Profile {
        return Profile(
            id = id,
            photo = photo,
            username = username,
            name = name
        )
    }
}
