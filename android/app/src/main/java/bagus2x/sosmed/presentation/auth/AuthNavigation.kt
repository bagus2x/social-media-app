package bagus2x.sosmed.presentation.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.hilt.navigation.compose.hiltViewModel
import bagus2x.sosmed.presentation.auth.signin.SignInScreen
import bagus2x.sosmed.presentation.auth.signup.SignUpScreen
import bagus2x.sosmed.presentation.common.Destination

@OptIn(ExperimentalAnimationApi::class)
object SignUpScreen : Destination(
    authority = "sign_up",
    screen = { _, navHostController ->
        SignUpScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    },
    enterTransition = {
        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))
    },
    exitTransition = {
        scaleOut() + fadeOut()
    },
    popEnterTransition = {
        scaleIn() + fadeIn()
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(500))
    }
)

@OptIn(ExperimentalAnimationApi::class)
object SignInScreen : Destination(
    authority = "sign_in",
    screen = { _, navHostController ->
        SignInScreen(
            navController = navHostController,
            viewModel = hiltViewModel()
        )
    },
    enterTransition = {
        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(500))
    },
    exitTransition = {
        scaleOut() + fadeOut()
    },
    popEnterTransition = {
        scaleIn() + fadeIn()
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(500))
    }
)

