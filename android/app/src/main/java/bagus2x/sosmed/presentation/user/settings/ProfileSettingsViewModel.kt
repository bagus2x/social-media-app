package bagus2x.sosmed.presentation.user.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileSettingsViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileSettingsState())
    val state = _state.asStateFlow()

    fun signOut() {
        viewModelScope.launch {
            _state.update { state -> state.copy(loading = true) }
            try {
                signOutUseCase()
                _state.update { state -> state.copy(loading = false) }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        loading = false,
                        snackbar = e.message ?: "Failed to sign out"
                    )
                }
                Timber.e(e)
            }
        }
    }
}
