package bagus2x.sosmed.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import bagus2x.sosmed.domain.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Serializable
@Entity(tableName = "user")
data class UserEntity(
    @SerialName("id")
    @PrimaryKey
    @ColumnInfo("id")
    val id: Long,

    @SerialName("username")
    @ColumnInfo("username")
    val username: String,

    @SerialName("name")
    @ColumnInfo("name")
    val name: String,

    @SerialName("email")
    @ColumnInfo("email")
    val email: String,

    @SerialName("photo")
    @ColumnInfo("photo")
    val photo: String?,

    @SerialName("header")
    @ColumnInfo("header")
    val header: String?,

    @SerialName("bio")
    @ColumnInfo("bio")
    val bio: String?,

    @SerialName("location")
    @ColumnInfo("location")
    val location: String?,

    @SerialName("website")
    @ColumnInfo("website")
    val website: String?,

    @SerialName("verified")
    @ColumnInfo("verified")
    val verified: Boolean,

    @SerialName("dateOfBirth")
    @ColumnInfo("dateOfBirth")
    val dateOfBirth: String?,

    @SerialName("totalFollowers")
    @ColumnInfo("totalFollowers")
    val totalFollowers: Int,

    @SerialName("totalFollowing")
    @ColumnInfo("totalFollowing")
    val totalFollowing: Int,

    @SerialName("following")
    @ColumnInfo("following")
    val following: Boolean,

    @SerialName("createdAt")
    @ColumnInfo("created_at")
    val createdAt: Long,

    @SerialName("updatedAt")
    @ColumnInfo("updated_at")
    val updatedAt: Long
)

fun UserEntity.asDomainModel(): User {
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
