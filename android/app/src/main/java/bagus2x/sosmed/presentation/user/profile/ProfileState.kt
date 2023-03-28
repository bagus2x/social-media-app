package bagus2x.sosmed.presentation.user.profile

import bagus2x.sosmed.domain.model.User

data class ProfileState(
    val user: User? = null,
    val own: Boolean = false,
    val snackbar: String = "",
) {
    val isLoading get() = user == null
}
