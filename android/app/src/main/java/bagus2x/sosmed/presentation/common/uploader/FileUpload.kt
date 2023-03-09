package bagus2x.sosmed.presentation.common.uploader

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileUpload(
    @SerialName("content")
    val contentUrl: String,
    @SerialName("name")
    val name: String,
    @SerialName("size")
    val size: Int,
    @SerialName("type")
    val type: String
)
