package bagus2x.sosmed.presentation.common

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.*
import bagus2x.sosmed.presentation.auth.SignInScreen
import bagus2x.sosmed.presentation.main.AuthState
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet as bottomSheetComposable

@OptIn(ExperimentalAnimationApi::class)
abstract class Destination constructor(
    val authority: String,
    private val arguments: List<NamedNavArgument> = emptyList(),
    private val deepLinks: List<NavDeepLink> = emptyList(),
    private val enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    private val exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    private val popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    private val popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    private val screen: @Composable (NavBackStackEntry, NavHostController) -> Unit
) {

    val route = buildString {
        append(authority)
        val queries = arguments.map { "${it.name}={${it.name}}" }
        if (queries.isNotEmpty()) {
            append("?${queries.joinToString("&")}")
        }
    }

    fun buildRoute(vararg params: Pair<String, Any>) = buildString {
        append(authority)
        val queries = params.map { "${it.first}=${it.second}" }
        if (queries.isNotEmpty()) {
            append("?${queries.joinToString("&")}")
        }
    }

    context(NavGraphBuilder)
    fun composable(navHostController: NavHostController) {
        @OptIn(ExperimentalAnimationApi::class)
        composable(
            route = this@Destination.route,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
            arguments = this@Destination.arguments,
            deepLinks = this@Destination.deepLinks,
        ) {
            screen(it, navHostController)
        }
    }

    context(NavGraphBuilder)
    fun composable(navHostController: NavHostController, authStateProvider: () -> AuthState) {
        @OptIn(ExperimentalAnimationApi::class)
        composable(
            route = this@Destination.route,
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition,
            arguments = this@Destination.arguments,
            deepLinks = this@Destination.deepLinks,
        ) {
            when (authStateProvider()) {
                AuthState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                AuthState.Unauthenticated -> {
                    navHostController.navigate(SignInScreen())
                }
                is AuthState.Authenticated -> {
                    screen(it, navHostController)
                }
            }
        }
    }

    context(NavGraphBuilder)
    fun bottomSheet(navHostController: NavHostController) {
        @OptIn(ExperimentalMaterialNavigationApi::class)
        bottomSheetComposable(
            route = this@Destination.route,
            arguments = this@Destination.arguments,
            deepLinks = this@Destination.deepLinks,
            content = { screen(it, navHostController) }
        )
    }

    context(NavGraphBuilder)
    fun bottomSheet(navHostController: NavHostController, authState: () -> AuthState) {
        @OptIn(ExperimentalMaterialNavigationApi::class)
        bottomSheetComposable(
            route = this@Destination.route,
            arguments = this@Destination.arguments,
            deepLinks = this@Destination.deepLinks,
        ) {
            when (authState()) {
                AuthState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                AuthState.Unauthenticated -> {
                    navHostController.navigate(SignInScreen())
                }
                is AuthState.Authenticated -> {
                    screen(it, navHostController)
                }
            }
        }
    }

    operator fun invoke(): String = authority
}
