package bagus2x.sosmed.domain.model

data class Auth(
    val profile: Profile,
    val accessToken: String,
    val refreshToken: String
)
