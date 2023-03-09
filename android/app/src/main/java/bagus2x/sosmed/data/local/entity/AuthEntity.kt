package bagus2x.sosmed.data.local.entity

import bagus2x.sosmed.domain.model.Auth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthEntity(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("profile")
    val profile: ProfileEntity
)

fun AuthEntity.asDomainModel(): Auth {
    return Auth(
        profile = profile.asDomainModel(),
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}
