package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.local.entity.ProfileEntity
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Profile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDTO(
    @SerialName("id")
    val id: Long,
    @SerialName("photo")
    val photo: String?,
    @SerialName("username")
    val username: String,
    @SerialName("name")
    val name: String
) {

    fun asEntity(): ProfileEntity {
        return ProfileEntity(
            id = id,
            photo = photo,
            username = username,
            name = name
        )
    }

    fun asDomainModel(): Profile {
        return Profile(
            id = id,
            photo = photo,
            username = username,
            name = name
        )
    }
}
