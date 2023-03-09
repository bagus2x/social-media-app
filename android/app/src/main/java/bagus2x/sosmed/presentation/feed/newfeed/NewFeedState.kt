package bagus2x.sosmed.presentation.feed.newfeed

import bagus2x.sosmed.presentation.common.media.DeviceMedia
import javax.annotation.concurrent.Immutable

@Immutable
data class NewFeedState(
    val description: String = "",
    val medias: List<DeviceMedia> = emptyList(),
    val selectedMedias: List<DeviceMedia> = emptyList(),
    val loading: Boolean = false,
    val created: Boolean = false,
    val snackbar: String = "",
)
