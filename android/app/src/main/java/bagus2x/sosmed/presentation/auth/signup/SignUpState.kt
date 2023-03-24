package bagus2x.sosmed.presentation.auth.signup

data class SignUpState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val authenticated: Boolean = false,
    val snackbar: String = ""
) {
    val isFilled get() = email.isNotBlank() && password.isNotBlank() && username.isNotBlank()
}
