package bagus2x.sosmed.presentation.auth

import androidx.hilt.navigation.compose.hiltViewModel
import bagus2x.sosmed.presentation.auth.signin.SignInScreen
import bagus2x.sosmed.presentation.auth.signup.SignUpScreen
import bagus2x.sosmed.presentation.common.Destination

object SignUpScreen : Destination(
    authority = "sign_up",
    screen = { _, navHostController ->
        SignUpScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

object SignInScreen : Destination(
    authority = "sign_in",
    screen = { _, navHostController ->
        SignInScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    }
)

