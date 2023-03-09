package bagus2x.sosmed.data.remote.dto

import bagus2x.sosmed.data.local.entity.TrendingEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendingDTO(
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("country")
    val country: String?,
    @SerialName("count")
    val count: Int
) {

    fun asEntity(): TrendingEntity {
        return TrendingEntity(
            name = name,
            type = type,
            country = country,
            count = count
        )
    }
}
