package bagus2x.sosmed.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    fun setUsername(username: String) {
        _state.update { it.copy(username = username) }
    }

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
                signUpUseCase(
                    username = state.value.username,
                    email = state.value.email,
                    password = state.value.password
                )
                _state.update { state -> state.copy(authenticated = true) }
            } catch (e: Exception) {
                _state.update { it.copy(snackbar = e.message ?: "Failed to sign up") }
                Timber.e(e)
            }
            _state.update { it.copy(loading = false) }
        }
    }
}
