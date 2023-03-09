package bagus2x.sosmed.domain.model

data class Trending(
    val id: Long = 0,
    val name: String,
    val type: String,
    val country: String?,
    val count: Int
)
