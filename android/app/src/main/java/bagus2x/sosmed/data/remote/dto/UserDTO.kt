package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.local.entity.UserEntity
import bagus2x.sosmed.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Serializable
data class UserDTO(
    @SerialName("id")
    val id: Long,
    @SerialName("username")
    val username: String,
    @SerialName("name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("photo")
    val photo: String?,
    @SerialName("header")
    val header: String?,
    @SerialName("bio")
    val bio: String?,
    @SerialName("location")
    val location: String?,
    @SerialName("website")
    val website: String?,
    @SerialName("verified")
    val verified: Boolean,
    @SerialName("dateOfBirth")
    val dateOfBirth: String?,
    @SerialName("totalFollowers")
    val totalFollowers: Int,
    @SerialName("totalFollowing")
    val totalFollowing: Int,
    @SerialName("following")
    val following: Boolean,
    @SerialName("createdAt")
    val createdAt: Long,
    @SerialName("updatedAt")
    val updatedAt: Long
) {

    fun asDomainModel(): User {
        return User(
            id = id,
            username = username,
            name = name,
            photo = photo,
            header = header,
            email = email,
            bio = bio,
            location = location,
            website = website,
            verified = verified,
            dateOfBirth = dateOfBirth?.let(LocalDate::parse),
            totalFollowers = totalFollowers,
            totalFollowing = totalFollowing,
            following = following,
            updatedAt = Instant
                .ofEpochMilli(updatedAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime(),
            createdAt = Instant
                .ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime(),
        )
    }

    fun asEntity(): UserEntity {
        return UserEntity(
            id = id,
            username = username,
            name = name,
            photo = photo,
            header = header,
            email = email,
            bio = bio,
            location = location,
            website = website,
            verified = verified,
            dateOfBirth = dateOfBirth,
            totalFollowers = totalFollowers,
            totalFollowing = totalFollowing,
            following = following,
            updatedAt = updatedAt,
            createdAt = createdAt,
        )
    }
}
