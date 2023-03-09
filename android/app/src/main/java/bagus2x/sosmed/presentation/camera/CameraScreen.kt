package bagus2x.sosmed.presentation.camera

import android.net.Uri
import androidx.camera.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.presentation.camera.components.CameraSurface
import bagus2x.sosmed.presentation.common.components.RequiresPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.launch
import java.util.*


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    result: (Uri?) -> Unit
) {
    RequiresPermission(
        permissions = remember { listOf(android.Manifest.permission.CAMERA) },
    ) {
        CameraSurface(modifier = Modifier.fillMaxSize()) { capture ->
            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    scope.launch {
                        val uri = capture()
                        result(uri)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(text = "Capture")
            }
        }
    }
}
