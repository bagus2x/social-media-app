package bagus2x.sosmed.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.domain.usecase.GetAuthUseCase
import bagus2x.sosmed.domain.usecase.GetUserUseCase
import bagus2x.sosmed.presentation.common.connectivity.NetworkTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel @Inject constructor(
    getAuthUseCase: GetAuthUseCase,
    getUserUseCase: GetUserUseCase,
    networkTracker: NetworkTracker
) : ViewModel() {
    private val auth = getAuthUseCase()
        .map { auth ->
            if (auth != null) {
                AuthState.Authenticated(auth)
            } else {
                AuthState.Unauthenticated
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AuthState.Loading
        )
    private val authUser = auth
        .filterIsInstance<AuthState.Authenticated>()
        .flatMapLatest { getUserUseCase(userId = it.auth.profile.id) }
    private val networkState = networkTracker.flow
    val state = combine(auth, authUser, networkState) { authState, authUser, networkState ->
        MainState(authState, authUser, networkState)
    }
        .catch { e ->
            Timber.e(e)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MainState()
        )
}
