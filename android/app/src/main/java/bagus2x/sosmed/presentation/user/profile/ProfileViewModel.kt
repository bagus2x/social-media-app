package bagus2x.sosmed.presentation.user.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.domain.usecase.FollowUserUseCase
import bagus2x.sosmed.domain.usecase.GetFeedsUseCase
import bagus2x.sosmed.domain.usecase.GetUserUseCase
import bagus2x.sosmed.presentation.user.ProfileScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFeedsUseCase: GetFeedsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val followUserUseCase: FollowUserUseCase,
) : ViewModel() {
    val userId = ProfileScreen.getUserId(savedStateHandle)
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()
    val feeds = getFeedsUseCase(authorId = userId, pageSize = 20)
        .catch { e ->
            Timber.e(e)
            _state.update { state -> state.copy(snackbar = e.message ?: "Failed to load feeds") }
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                getUserUseCase(userId).filterNotNull().collectLatest { user ->
                    _state.update { state -> state.copy(user = user) }
                }
            } catch (e: Exception) {
                _state.update { state -> state.copy(snackbar = e.message ?: "Failed to load user") }
            }
        }
    }

    fun followUser(user: User) {
        viewModelScope.launch {
            try {
                followUserUseCase(userId = user.id)
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(snackbar = e.message ?: "Failed to follow user")
                }
            }
        }
    }
}
