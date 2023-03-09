package bagus2x.sosmed.presentation.feed.comment

import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.presentation.common.media.DeviceMedia

data class CommentState(
    val parentComment: Comment? = null,
    val commentToBeReplied: Comment? = null,
    val description: String = "",
    val medias: List<DeviceMedia> = emptyList(),
    val loading: Boolean = parentComment == null,
    val snackbar: String = ""
)
