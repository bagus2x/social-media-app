package bagus2x.sosmed.presentation.auth.signin

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.auth.SignInScreen
import bagus2x.sosmed.presentation.auth.SignUpScreen
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    BackHandler {
        (context as? Activity)?.finish()
    }

    SignInScreen(
        stateProvider = { state },
        snackbarConsumed = viewModel::snackbarConsumed,
        setEmail = viewModel::setEmail,
        setPassword = viewModel::setPassword,
        signIn = viewModel::signIn,
        navigateToSignUpScreen = {
            navController.navigate(SignUpScreen()) {
                popUpTo(SignInScreen()) {
                    inclusive = true
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        navigateUp = navController::navigateUp
    )
}

@Composable
fun SignInScreen(
    stateProvider: () -> SignInState,
    snackbarConsumed: () -> Unit,
    setEmail: (String) -> Unit,
    setPassword: (String) -> Unit,
    signIn: () -> Unit,
    navigateToSignUpScreen: () -> Unit,
    navigateUp: () -> Unit
) {
    val state = stateProvider()
    val showSnackbar = LocalShowSnackbar.current

    LaunchedEffect(Unit) {
        snapshotFlow { stateProvider() }.collectLatest { state ->
            if (state.snackbar.isNotBlank()) {
                showSnackbar(state.snackbar)
                snackbarConsumed()
            }
            if (state.authenticated) {
                navigateUp()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(
                32.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                model = R.drawable.ilustration_sign_in,
                contentDescription = null,
                modifier = Modifier.width(200.dp)
            )
            TextField(
                value = state.email,
                onValueChange = setEmail,
                label = {
                    Text(text = stringResource(R.string.text_email))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
            )
            TextField(
                value = state.password,
                onValueChange = setPassword,
                label = {
                    Text(text = stringResource(R.string.text_password))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(onSend = { signIn() })
            )
            Button(
                onClick = signIn,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.loading && state.isFilled
            ) {
                Text(text = stringResource(R.string.text_sign_in))
            }
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.text_dont_have_an_account),
                    style = MaterialTheme.typography.button
                )
                TextButton(onClick = navigateToSignUpScreen) {
                    Text(text = stringResource(R.string.text_sign_in))
                }
            }
        }
        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
