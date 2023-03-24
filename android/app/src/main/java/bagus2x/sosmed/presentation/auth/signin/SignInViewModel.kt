package bagus2x.sosmed.presentation.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.domain.usecase.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun setEmail(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun setPassword(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun snackbarConsumed() {
        _state.update { it.copy(snackbar = "") }
    }

    fun signIn() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                signInUseCase(
                    email = state.value.email,
                    password = state.value.password
                )
                _state.update { state -> state.copy(authenticated = true) }
            } catch (e: Exception) {
                _state.update { it.copy(snackbar = e.message ?: "Failed to sign in") }
                Timber.e(e)
            }
            _state.update { it.copy(loading = false) }
        }
    }
}
