package bagus2x.sosmed.presentation.common.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
fun RequiresPermission(
    modifier: Modifier = Modifier,
    permissions: List<String>,
    deniedContent: (@Composable (MultiplePermissionsState) -> Unit)? = null,
    grantedContent: @Composable (MultiplePermissionsState) -> Unit,
) {
    val permissionState = rememberMultiplePermissionsState(permissions)
    AnimatedContent(targetState = permissionState.allPermissionsGranted) { granted ->
        if (granted) {
            grantedContent(permissionState)
        } else {
            if (deniedContent != null) {
                deniedContent(permissionState)
            } else {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
                        Text(text = "Request Permissions")
                    }
                }
            }
        }
    }
}
