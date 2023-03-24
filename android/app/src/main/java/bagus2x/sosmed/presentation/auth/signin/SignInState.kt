package bagus2x.sosmed.presentation.auth.signin

data class SignInState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val authenticated: Boolean = false,
    val snackbar: String = ""
) {
    val isFilled get() = email.isNotBlank() && password.isNotBlank()
}
