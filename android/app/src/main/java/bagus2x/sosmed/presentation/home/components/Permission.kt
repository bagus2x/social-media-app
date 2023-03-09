package bagus2x.sosmed.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
inline fun Permission(
    permission: String,
    title: String,
    rationaleText: String,
    permissionText: String,
    modifier: Modifier = Modifier,
    noinline skipp: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberPermissionState(permission)
    if (state.status.isGranted) {
        content()
        return
    }
    val textToShow = if (state.status.shouldShowRationale) rationaleText else permissionText
    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        topBar = {
            TopAppBar(
                elevation = 0.dp
            ) {
                IconButton(onClick = skipp) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close_outlined),
                        contentDescription = null
                    )
                }
            }
        },
        modifier = modifier
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = title,
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center
            )
            Text(
                text = textToShow,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(180.dp))
            Button(
                onClick = state::launchPermissionRequest,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colors.primary
                )
            ) {
                Text(text = stringResource(R.string.text_next))
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = skipp,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.text_skip))
            }
        }
    }
}
