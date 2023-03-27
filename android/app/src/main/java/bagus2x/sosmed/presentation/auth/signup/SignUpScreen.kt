package bagus2x.sosmed.presentation.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
            navController.navigate(SignInScreen()) {
                popUpTo(SignUpScreen()) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .imePadding()
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
                model = R.drawable.ilustration_hello,
                contentDescription = null,
                modifier = Modifier.width(200.dp)
            )
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
            var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
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
                keyboardActions = KeyboardActions(onSend = { signIn() }),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation,
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        if (isPasswordVisible) {
                            Icon(
                                painter = painterResource(R.drawable.ic_eye_outlined),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.ic_eye_slash_outlined),
                                contentDescription = null
                            )
                        }
                    }
                }
            )
            Button(
                onClick = signIn,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.loading && state.isFilled
            ) {
                Text(text = stringResource(R.string.text_sign_up))
            }
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.text_already_have_an_account),
                    style = MaterialTheme.typography.button
                )
                TextButton(onClick = navigateToSignInScreen) {
                    Text(text = stringResource(R.string.text_sign_in))
                }
            }
        }
        if (state.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

private val PasswordVisualTransformation = PasswordVisualTransformation()
