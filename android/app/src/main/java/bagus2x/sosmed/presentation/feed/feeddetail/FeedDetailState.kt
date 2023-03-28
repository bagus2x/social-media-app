package bagus2x.sosmed.presentation.feed.feeddetail

import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Profile
import bagus2x.sosmed.presentation.common.media.DeviceMedia

data class FeedDetailState(
    val feedState: FeedState = FeedState(),
    val profileState: ProfileState = ProfileState(),
    val commentState: CommentState = CommentState(),
    val snackbar: String = ""
)

data class FeedState(
    val feed: Feed? = null,
    val loading: Boolean = feed == null
)

data class ProfileState(
    val profile: Profile? = null,
    val loading: Boolean = false
)

data class CommentState(
    val commentToBeReplied: Comment? = null,
    val description: String = "",
    val medias: List<DeviceMedia> = emptyList(),
    val loading: Boolean = false
)
