package bagus2x.sosmed.presentation.auth.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.auth.SignInScreen
import bagus2x.sosmed.presentation.common.LocalShowSnackbar
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    val state by viewModel.state.collectAsState()

    SignUpScreen(
        stateProvider = { state },
        snackbarConsumed = viewModel::snackbarConsumed,
        setUsername = viewModel::setUsername,
        setEmail = viewModel::setEmail,
        setPassword = viewModel::setPassword,
        signIn = viewModel::signIn,
        navigateToSignInScreen = {
            navController.navigate(SignInScreen())
        },
        navigateUp = navController::navigateUp
    )
}

@Composable
fun SignUpScreen(
    stateProvider: () -> SignUpState,
    snackbarConsumed: () -> Unit,
    setUsername: (String) -> Unit,
    setEmail: (String) -> Unit,
    setPassword: (String) -> Unit,
    signIn: () -> Unit,
    navigateToSignInScreen: () -> Unit,
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = state.username,
                onValueChange = setUsername,
                label = {
                    Text(text = stringResource(R.string.text_username))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
            )
            Spacer(modifier = Modifier.height(32.dp))
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
            Spacer(modifier = Modifier.height(32.dp))
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
            Spacer(modifier = Modifier.height(32.dp))
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
                    text = "Already have an account?",
                    style = MaterialTheme.typography.button
                )
                TextButton(onClick = navigateToSignInScreen) {
                    Text(text = "Sign In")
                }
            }
        }
        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
