package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.local.entity.AuthEntity
import bagus2x.sosmed.domain.model.Auth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthDTO(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("profile")
    val profile: ProfileDTO
) {

    fun asEntity(): AuthEntity {
        return AuthEntity(
            profile = profile.asEntity(),
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}
