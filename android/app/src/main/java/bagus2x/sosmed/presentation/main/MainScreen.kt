package bagus2x.sosmed.presentation.main

import androidx.compose.animation.*
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.LocalAuthenticatedUser
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.components.LocalProvider
import bagus2x.sosmed.presentation.common.components.Scaffold
import bagus2x.sosmed.presentation.common.connectivity.NetworkTracker
import bagus2x.sosmed.presentation.common.theme.AppColor
import bagus2x.sosmed.presentation.conversation.ChatScreen
import bagus2x.sosmed.presentation.explore.TrendingScreen
import bagus2x.sosmed.presentation.home.HomeScreen
import bagus2x.sosmed.presentation.notification.NotificationScreen
import bagus2x.sosmed.presentation.user.MyProfileScreen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    bottomSheetNavigator: BottomSheetNavigator = rememberBottomSheetNavigator(),
    navController: NavHostController = rememberAnimatedNavController(bottomSheetNavigator)
) {
    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetShape = RectangleShape,
        sheetBackgroundColor = Color.Transparent,
        sheetContentColor = MaterialTheme.colors.onBackground,
        sheetElevation = 0.dp
    ) {
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        val showSnackbar: (String) -> Unit = remember {
            { message ->
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
            }
        }
        val state by viewModel.state.collectAsStateWithLifecycle()
        LocalProvider(
            LocalShowSnackbar provides showSnackbar,
            LocalAuthenticatedUser provides state.authUser
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                scaffoldState = scaffoldState,
                snackbarHost = { hostState ->
                    SnackbarHost(
                        hostState = hostState,
                        modifier = Modifier.imePadding()
                    )
                }
            ) {
                NavGraph(
                    modifier = Modifier.fillMaxSize(),
                    navHostController = navController,
                    authStateProvider = { state.authState }
                )
                BottomNavigation(
                    navController = navController,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
                NetworkStatus(
                    state = state.networkState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

val BotnavItems = listOf(
    Triple(HomeScreen, R.drawable.ic_home_outlined, R.drawable.ic_home_filled),
    Triple(TrendingScreen, R.drawable.ic_search_outlined, R.drawable.ic_search_filled),
    Triple(NotificationScreen, R.drawable.ic_heart_outlined, R.drawable.ic_heart_filled),
    Triple(ChatScreen, R.drawable.ic_message_outlined, R.drawable.ic_message_filled),
    Triple(MyProfileScreen, R.drawable.ic_person_outlined, R.drawable.ic_person_filled),
)

val Routes = BotnavItems.map { it.first.route }

@Composable
fun BottomNavigation(
    navController: NavController,
    modifier: Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isVisible by remember {
        derivedStateOf {
            navBackStackEntry?.destination?.hierarchy?.any { it.route in Routes } == true
        }
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            animationSpec = tween(
                delayMillis = AnimationConstants.DefaultDurationMillis
            ),
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            animationSpec = tween(),
            targetOffsetY = { it }
        ),
        modifier = modifier
    ) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground,
            elevation = 1.dp,
            modifier = Modifier.windowInsetsBottomHeight(
                WindowInsets.navigationBars.add(WindowInsets(bottom = 56.dp))
            ),
        ) {
            BotnavItems.forEach { (destination, outlined, filled) ->
                val selected = navBackStackEntry?.destination
                    ?.hierarchy
                    ?.any { it.route == destination.route } == true
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(if (selected) filled else outlined),
                            contentDescription = null
                        )
                    },
                    selected = selected,
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = MaterialTheme.colors.onBackground,
                    onClick = {
                        navController.navigate(destination()) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    },
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    }
}

@Composable
fun NetworkStatus(
    state: NetworkTracker.State,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        visible = state == NetworkTracker.Unavailable
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically { -it * 2 },
        exit = slideOutVertically(animationSpec = tween(delayMillis = 2000)) { -it * 2 }
    ) {
        val color by animateColorAsState(
            targetValue = when (state) {
                is NetworkTracker.Unavailable -> MaterialTheme.colors.error
                is NetworkTracker.Available, is NetworkTracker.Init -> AppColor.Green500
            }
        )
        val height = with(LocalDensity.current) {
            WindowInsets.statusBars
                .getTop(this)
                .toDp() + 56.dp
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(color)
        ) {
            Text(
                text = when (state) {
                    is NetworkTracker.Unavailable -> stringResource(R.string.text_network_unavailable)
                    is NetworkTracker.Available, is NetworkTracker.Init -> stringResource(R.string.text_network_available)
                },
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .align(Alignment.Center)
                    .statusBarsPadding(),
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}
