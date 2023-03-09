package bagus2x.sosmed.presentation.gallery

import androidx.compose.runtime.Immutable
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.gallery.contract.MediaType

@Immutable
data class GalleryState(
    val selectedMedias: List<DeviceMedia> = emptyList(),
    val shouldLoad: Boolean = false,
    val type: MediaType = MediaType.ImageAndVideo,
    val multiple: Boolean = false,
    val max: Int = 1
) {

    val isEnabled: Boolean
        get() = selectedMedias.size < max
}
