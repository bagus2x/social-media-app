package bagus2x.sosmed.presentation.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.parcelable
import bagus2x.sosmed.presentation.common.theme.MedsosTheme
import bagus2x.sosmed.presentation.common.components.LocalProvider
import bagus2x.sosmed.presentation.home.components.Permission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CameraActivity : ComponentActivity() {
    private val viewModel by viewModels<CameraViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MedsosTheme {
                LocalProvider {
                    val capturedDeviceMedia by viewModel.capturedDeviceMedia.collectAsState()
                    LaunchedEffect(capturedDeviceMedia) {
                        if (capturedDeviceMedia != null) {
                            val intent = Intent().apply {
                                putExtra(KEY_DEVICE_MEDIA, capturedDeviceMedia)
                            }
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }
                    Permission(
                        permission = Manifest.permission.CAMERA,
                        title = stringResource(R.string.text_take_picture_and_video),
                        permissionText = stringResource(R.string.text_camera_require_permission),
                        rationaleText = stringResource(R.string.text_camera_rationale),
                        skipp = this::finish,
                        modifier = Modifier.systemBarsPadding(),
                    ) {
                        CameraScreen(
                            result = viewModel::setCapturedUri
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val RESULT_OK = 1
        const val KEY_DEVICE_MEDIA = "device_media"
    }
}

@Composable
fun rememberCameraLauncher(onResult: (DeviceMedia?) -> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode != CameraActivity.RESULT_OK) {
                return@rememberLauncherForActivityResult
            }
            val selectedDeviceMedias = result
                .data
                ?.parcelable<DeviceMedia>(CameraActivity.KEY_DEVICE_MEDIA)
                ?: return@rememberLauncherForActivityResult
            onResult(selectedDeviceMedias)
        }
    )
}

fun ManagedActivityResultLauncher<Intent, ActivityResult>.launchCamera(context: Context) {
    val intent = Intent(context, CameraActivity::class.java)
    launch(intent)
}
