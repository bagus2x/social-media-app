package bagus2x.sosmed.presentation.common

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.*
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
    fun bottomSheet(navHostController: NavHostController) {
        @OptIn(ExperimentalMaterialNavigationApi::class)
        bottomSheetComposable(
            route = this@Destination.route,
            arguments = this@Destination.arguments,
            deepLinks = this@Destination.deepLinks,
            content = { screen(it, navHostController) }
        )
    }

    operator fun invoke(): String = authority
}
