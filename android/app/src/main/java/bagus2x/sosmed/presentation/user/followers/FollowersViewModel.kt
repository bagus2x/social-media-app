package bagus2x.sosmed.presentation.user.followers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.domain.usecase.FollowUserUseCase
import bagus2x.sosmed.domain.usecase.GetFollowersUseCase
import bagus2x.sosmed.presentation.user.FollowersScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FollowersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFollowersUseCase: GetFollowersUseCase,
    private val followUserUseCase: FollowUserUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(FollowersState())
    private val userId = FollowersScreen.getUserId(savedStateHandle)
    val users = getFollowersUseCase(userId)
        .cachedIn(viewModelScope)
        .catch { e ->
            Timber.e(e)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

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
