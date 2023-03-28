package bagus2x.sosmed.presentation.feed.feeddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.uploader.FileUploader
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getRootCommentsUseCase: GetRootCommentsUseCase,
    private val getFeedUseCase: GetFeedUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val createCommentUseCase: CreateCommentUseCase,
    private val loadRepliesUseCase: LoadRepliesUseCase,
    private val favoriteFeedUseCase: FavoriteFeedUseCase,
    private val fileUploader: FileUploader
) : ViewModel() {
    private val feedId = requireNotNull(savedStateHandle.get<Long>("feed_id"))

    private val _state = MutableStateFlow(FeedDetailState())
    val state = _state.asStateFlow()

    val comments = getRootCommentsUseCase(feedId)
        .catch { e ->
            _state.update { state -> state.copy(snackbar = "") }
            Timber.e(e)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    init {
        loadFeed()
        loadProfile()
    }

    private fun loadFeed() {
        viewModelScope.launch {
            getFeedUseCase(feedId)
                .catch { e ->
                    Timber.e(e)
                }
                .filterNotNull()
                .collect { feed ->
                    _state.update { state ->
                        state.copy(
                            feedState = state.feedState.copy(feed = feed)
                        )
                    }
                }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getUserUseCase()
                .onStart {
                    _state.update { state ->
                        state.copy(
                            profileState = state.profileState.copy(loading = true)
                        )
                    }
                }
                .filterNotNull()
                .catch { e ->
                    _state.update { state ->
                        state.copy(
                            profileState = state.profileState.copy(loading = false),
                            snackbar = e.message ?: "Failed to load user"
                        )
                    }
                    Timber.e(e)
                }
                .collectLatest { user ->
                    _state.update { state ->
                        state.copy(
                            profileState = state.profileState.copy(
                                profile = user.asProfile(),
                                loading = false
                            )
                        )
                    }
                }
        }
    }

    fun snackbarConsumed() = _state.update { state ->
        state.copy(snackbar = "")
    }

    fun setCommentToBeReplied(comment: Comment) = _state.update { state ->
        state.copy(commentState = state.commentState.copy(commentToBeReplied = comment))
    }

    fun setDescription(description: String) = _state.update { state ->
        state.copy(commentState = state.commentState.copy(description = description))
    }

    fun cancelComment() = _state.update { state ->
        state.copy(commentState = state.commentState.copy(commentToBeReplied = null))
    }

    fun loadReplies(comment: Comment) {
        viewModelScope.launch {
            _state.update { state -> state.copy(commentState = state.commentState.copy(loading = true)) }
            try {
                loadRepliesUseCase(parentId = comment.id, pageSize = 30)
                _state.update { state -> state.copy(commentState = state.commentState.copy(loading = false)) }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        commentState = state.commentState.copy(loading = false),
                        snackbar = e.message ?: "Failed to load replies"
                    )
                }
                Timber.e(e)
            }
        }
    }

    fun createComment() {
        viewModelScope.launch {
            if (state.value.commentState.description.isBlank()) {
                return@launch
            }
            _state.update { state -> state.copy(commentState = state.commentState.copy(loading = true)) }
            try {
                createCommentUseCase(
                    feedId = state.value.commentState.commentToBeReplied?.feedId ?: feedId,
                    parentId = state.value.commentState.commentToBeReplied?.id,
                    medias = state.value.commentState.medias.map { upload(it) },
                    description = state.value.commentState.description
                )
                _state.update { state ->
                    state.copy(
                        commentState = state.commentState.copy(
                            description = "",
                            commentToBeReplied = null
                        ),
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
                _state.update { state -> state.copy(snackbar = e.message ?: "") }
            }
            _state.update { state -> state.copy(commentState = state.commentState.copy(loading = false)) }
        }
    }

    fun favoriteFeed(feed: Feed) {
        viewModelScope.launch {
            _state.update { state -> state.copy(feedState = state.feedState.copy(loading = true)) }
            try {
                favoriteFeedUseCase(feedId = feed.id)
            } catch (e: Exception) {
                _state.update { state -> state.copy(snackbar = e.message ?: "") }
                Timber.e(e)
            }
            _state.update { state -> state.copy(feedState = state.feedState.copy(loading = false)) }
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
