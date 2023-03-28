package bagus2x.sosmed.presentation.feed.newfeed

import bagus2x.sosmed.domain.model.Profile
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import javax.annotation.concurrent.Immutable

@Immutable
data class NewFeedState(
    val feedState: FeedState = FeedState(),
    val profileState: ProfileState = ProfileState(),
    val snackbar: String = ""
)

data class ProfileState(
    val profile: Profile? = null,
    val loading: Boolean = false
)

data class FeedState(
    val description: String = "",
    val medias: List<DeviceMedia> = emptyList(),
    val selectedMedias: List<DeviceMedia> = emptyList(),
    val loading: Boolean = false,
    val created: Boolean = false
)
