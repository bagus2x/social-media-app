package bagus2x.sosmed.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.domain.usecase.GetAuthUseCase
import bagus2x.sosmed.domain.usecase.GetUserUseCase
import bagus2x.sosmed.presentation.common.connectivity.NetworkTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel @Inject constructor(
    private val getAuthUseCase: GetAuthUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val networkTracker: NetworkTracker
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        loadAuthState()
        loadAuthUser()
        loadNetworkState()
    }

    private fun loadAuthState() {
        viewModelScope.launch {
            getAuthUseCase()
                .catch { e ->
                    _state.update { state -> state.copy(snackbar = e.message ?: "") }
                    Timber.e(e)
                }
                .collectLatest { auth ->
                    if (auth != null) {
                        _state.update { state -> state.copy(authState = AuthState.Authenticated(auth)) }
                    } else {
                        _state.update { state -> state.copy(authState = AuthState.Unauthenticated) }
                    }
                }
        }
    }

    private fun loadAuthUser() {
        viewModelScope.launch {
            getAuthUseCase()
                .filterNotNull()
                .flatMapLatest { getUserUseCase(userId = it.profile.id) }
                .catch { e ->
                    _state.update { state -> state.copy(snackbar = e.message ?: "") }
                    Timber.e(e)
                }
                .collectLatest { user ->
                    _state.update { state -> state.copy(authUser = user) }
                }
        }
    }

    private fun loadNetworkState() {
        viewModelScope.launch {
            networkTracker.flow
                .catch { e ->
                    _state.update { state -> state.copy(snackbar = e.message ?: "") }
                    Timber.e(e)
                }
                .collectLatest { networkState ->
                    _state.update { state -> state.copy(networkState = networkState) }
                }
        }
    }
}
