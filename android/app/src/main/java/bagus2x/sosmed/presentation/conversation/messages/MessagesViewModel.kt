package bagus2x.sosmed.presentation.conversation.messages

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.usecase.GetChatUseCase
import bagus2x.sosmed.domain.usecase.ObserveMessagesUseCase
import bagus2x.sosmed.domain.usecase.SendMessageUseCase
import bagus2x.sosmed.presentation.common.connectivity.NetworkTracker
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.uploader.FileUploader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val getChatUseCase: GetChatUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val fileUploader: FileUploader,
    private val networkTracker: NetworkTracker
) : ViewModel() {
    private val _state = MutableStateFlow(MessagesState())
    val state = _state.asStateFlow()
    private val chatId = requireNotNull(savedStateHandle.get<Long>("chat_id"))
    val messages = observeMessagesUseCase(chatId, 20)
        .catch { e ->
            Timber.e(e)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    fun setDescription(description: String) = _state.update { state ->
        state.copy(messageState = state.messageState.copy(description = description))
    }

    init {
        loadChat()
        connectChat()
    }

    fun consumeSnackbar() = _state.update { state ->
        state.copy(snackbar = "")
    }

    private fun loadChat() {
        viewModelScope.launch {
            getChatUseCase(chatId)
                .onStart {
                    _state.update { state -> state.copy(chatState = state.chatState.copy(loading = true)) }
                }
                .filterNotNull()
                .catch { e ->
                    _state.update { state ->
                        state.copy(
                            chatState = state.chatState.copy(loading = false),
                            snackbar = e.message ?: "Failed to load chat"
                        )
                    }
                    Timber.e(e)
                }
                .collectLatest { chat ->
                    _state.update { state ->
                        state.copy(
                            chatState = state.chatState.copy(
                                chat = chat,
                                loading = false
                            )
                        )
                    }
                }
        }
    }

    private fun connectChat() {
        viewModelScope.launch {
            networkTracker.flow.collectLatest { status ->
                when (status) {
                    is NetworkTracker.Available -> {
                        try {
                            observeMessagesUseCase.connect(chatId)
                        } catch (e: Exception) {
                            _state.update { state ->
                                state.copy(snackbar = e.message ?: "Failed to connect")
                            }
                            Timber.e(e)
                        }
                    }
                    is NetworkTracker.Unavailable -> {
                        try {
                            observeMessagesUseCase.disconnect(chatId)
                        } catch (e: Exception) {
                            _state.update { state ->
                                state.copy(snackbar = e.message ?: "Failed to disconnect")
                            }
                            Timber.e(e)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun disconnectChat() {
        viewModelScope.launch {
            try {
                observeMessagesUseCase.disconnect(chatId)
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(snackbar = e.message ?: "Failed to disconnect")
                }
                Timber.e(e)
            }
        }
    }

    fun send() {
        if (!state.value.messageState.isFulfilled) {
            return
        }
        viewModelScope.launch {
            _state.update { state -> state.copy(messageState = state.messageState.copy(loading = true)) }
            try {
                sendMessageUseCase(
                    chatId = chatId,
                    description = state.value.messageState.description,
                    medias = state.value.messageState.medias.map { media ->
                        when (media) {
                            is DeviceMedia.Video -> {
                                val (thumbnail, video) = fileUploader.upload(media)
                                Media.Video(
                                    thumbnailUrl = thumbnail.contentUrl,
                                    videoUrl = video.contentUrl
                                )
                            }
                            is DeviceMedia.Image -> {
                                Media.Image(imageUrl = fileUploader.upload(media).contentUrl)
                            }
                        }
                    },
                )
                _state.update { state ->
                    state.copy(
                        messageState = state.messageState.copy(
                            loading = false,
                            description = ""
                        )
                    )
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        messageState = state.messageState.copy(loading = false),
                        snackbar = e.message ?: "Failed to send message"
                    )
                }
                Timber.e(e)
            }
        }
    }

    override fun onCleared() {
        disconnectChat()
        super.onCleared()
    }
}
