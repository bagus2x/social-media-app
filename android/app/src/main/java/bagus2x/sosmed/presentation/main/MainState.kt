package bagus2x.sosmed.presentation.main

import androidx.compose.runtime.Immutable
import bagus2x.sosmed.domain.model.Auth
import bagus2x.sosmed.domain.model.User
import bagus2x.sosmed.presentation.common.connectivity.NetworkTracker

@Immutable
data class MainState(
    val authState: AuthState = AuthState.Loading,
    val authUser: User? = null,
    val networkState: NetworkTracker.State = NetworkTracker.Init,
    val snackbar: String = ""
)

sealed class AuthState {

    object Loading : AuthState()

    data class Authenticated(val auth: Auth) : AuthState()

    object Unauthenticated : AuthState()
}
