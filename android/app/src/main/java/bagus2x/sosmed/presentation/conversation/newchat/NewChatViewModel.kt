package bagus2x.sosmed.presentation.conversation.newchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.domain.usecase.CreateChatUseCase
import bagus2x.sosmed.domain.usecase.GetFollowingUseCase
import bagus2x.sosmed.domain.usecase.GetUserUseCase
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.uploader.FileUploader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewChatViewModel @Inject constructor(
    getFollowingUseCase: GetFollowingUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val createChatUseCase: CreateChatUseCase,
    private val fileUploader: FileUploader
) : ViewModel() {
    private val _state = MutableStateFlow(NewChatState())
    val state = _state.asStateFlow()
    val users = getFollowingUseCase()
        .cachedIn(viewModelScope)
        .catch { e ->
            Timber.e(e)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    fun consumeSnackbar() = _state.update { state ->
        state.copy(snackbar = "")
    }

    init {
        viewModelScope.launch {
            try {
                getUserUseCase()
                    .filterNotNull()
                    .collectLatest { user ->
                        _state.update { state -> state.copy(authUser = user) }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load authenticated user")
            }
        }
    }

    fun selectUser(user: User) = _state.update { state ->
        val selected = state.members
            .toMutableSet()
            .apply { add(user) }
        state.copy(members = selected)
    }

    fun unselectUser(user: User) = _state.update { state ->
        val selected = state.members
            .toMutableSet()
            .apply { remove(user) }
        state.copy(members = selected)
    }

    fun setName(name: String) = _state.update { state ->
        state.copy(name = name)
    }

    fun setPhoto(photo: DeviceMedia.Image?) = _state.update { state ->
        state.copy(photo = photo)
    }

    fun createGroupChat() {
        val state = state.value
        if (!state.isFulfilled) {
            return
        }
        viewModelScope.launch {
            _state.update { state -> state.copy(loading = true) }
            try {
                if (state.isGroupChat) {
                    val chat = createChatUseCase(
                        memberIds = state.membersWithoutAuthUser.map { it.id }.toSet(),
                        name = state.name,
                        photo = state.photo?.let { fileUploader.upload(it).contentUrl }
                    )
                    _state.update { state -> state.copy(createdChat = chat, loading = false) }
                }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        snackbar = e.message ?: "Failed to create group chat",
                        loading = false
                    )
                }
                Timber.e(e)
            }
        }
    }

    fun createPrivateChat(user: User) {
        viewModelScope.launch {
            _state.update { state -> state.copy(loading = true) }
            try {
                val chat = createChatUseCase(memberId = user.id)
                _state.update { state -> state.copy(createdChat = chat, loading = false) }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        snackbar = e.message ?: "Failed to create group chat",
                        loading = false
                    )
                }
                Timber.e(e)
            }
        }
    }
}
