package bagus2x.sosmed.presentation.feed.comment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.uploader.FileUploader
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.usecase.CreateCommentUseCase
import bagus2x.sosmed.domain.usecase.GetChildCommentsUseCase
import bagus2x.sosmed.domain.usecase.GetCommentUseCase
import bagus2x.sosmed.domain.usecase.LoadRepliesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    savedState: SavedStateHandle,
    getCommentUseCase: GetCommentUseCase,
    getChildCommentsUseCase: GetChildCommentsUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val loadRepliesUseCase: LoadRepliesUseCase,
    private val fileUploader: FileUploader
) : ViewModel() {
    private val parentId = requireNotNull(savedState.get<Long>("parent_id"))
    private val _state = MutableStateFlow(CommentState())
    val state = _state.asStateFlow()
    val comments = getChildCommentsUseCase(parentId)
        .catch { e ->
            _state.update { state -> state.copy(snackbar = e.message ?: "") }
            Timber.e(e)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    init {
        viewModelScope.launch {
            try {
                getCommentUseCase(id = parentId)
                    .filterNotNull()
                    .collect { comment ->
                        _state.update { state -> state.copy(parentComment = comment) }
                    }
            } catch (e: Exception) {
                _state.update { state -> state.copy(snackbar = e.message ?: "") }
                Timber.e(e)
            }
        }
    }

    fun snackbarConsumed() = _state.update { state ->
        state.copy(snackbar = "")
    }

    fun setDescription(description: String) = _state.update { state ->
        state.copy(description = description)
    }

    fun setMedias(medias: List<DeviceMedia>) = _state.update { state ->
        state.copy(medias = medias)
    }

    fun setCommentToBeReplied(comment: Comment) = _state.update { state ->
        state.copy(commentToBeReplied = comment)
    }

    fun cancelComment() = _state.update { state ->
        state.copy(commentToBeReplied = null)
    }

    fun loadReplies(comment: Comment) {
        viewModelScope.launch {
            _state.update { state -> state.copy(loading = true) }
            try {
                loadRepliesUseCase(parentId = comment.id, pageSize = 30)
                _state.update { state -> state.copy(loading = false) }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        loading = false,
                        snackbar = e.message ?: "Failed to load replies"
                    )
                }
                Timber.e(e)
            }
        }
    }

    fun sendComment() {
        val (parentComment, commentToBeReplied, description, medias) = state.value
        viewModelScope.launch {
            if (parentComment == null || description.isBlank()) {
                return@launch
            }
            _state.update { state -> state.copy(loading = true) }
            try {
                createCommentUseCase(
                    feedId = parentComment.feedId,
                    parentId = commentToBeReplied?.id ?: parentComment.id,
                    description = description,
                    medias = medias.map { upload(it) }
                )
                _state.update { state -> state.copy(description = "") }
            } catch (e: Exception) {
                _state.update { state -> state.copy(snackbar = e.message ?: "") }
                Timber.e(e)
            }
            _state.update { state -> state.copy(loading = false) }
        }
    }

    private suspend fun upload(media: DeviceMedia): Media {
        return when (media) {
            is DeviceMedia.Image -> {
                val uploadedImage = fileUploader.upload(media)
                Media.Image(imageUrl = uploadedImage.contentUrl)
            }
            is DeviceMedia.Video -> {
                val (uploadedThumbnail, uploadedVideo) = fileUploader.upload(media)
                Media.Video(
                    thumbnailUrl = uploadedThumbnail.contentUrl,
                    videoUrl = uploadedVideo.contentUrl
                )
            }
        }
    }
}
