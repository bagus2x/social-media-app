package bagus2x.sosmed.presentation.imageeditor

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.parcelable
import bagus2x.sosmed.presentation.common.theme.MedsosTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class ImageEditorActivity : ComponentActivity() {
    private val viewModel by viewModels<ImageEditorViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        intent?.parcelable<DeviceMedia.Image>(KEY_IMAGE)?.let(viewModel::initImage)

        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = false,
                isNavigationBarContrastEnforced = false
            )
            MedsosTheme {
                val image = viewModel.image.collectAsState().value
                if (image != null) {
                    ImageEditorScreen(
                        image = image,
                        navigateUp = this::finish,
                        save = {
                            val intent = Intent().apply {
                                putExtra(KEY_IMAGE, it)
                            }
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val RESULT_OK = 0
        const val KEY_IMAGE = "images"
    }
}

@Composable
fun rememberImageEditorLauncher(onResult: (DeviceMedia.Image?) -> Unit): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode != ImageEditorActivity.RESULT_OK) {
                return@rememberLauncherForActivityResult
            }
            val editedImage = result
                .data
                ?.parcelable<DeviceMedia.Image>(ImageEditorActivity.KEY_IMAGE)
                ?: return@rememberLauncherForActivityResult
            onResult(editedImage)
        }
    )
}

fun ManagedActivityResultLauncher<Intent, ActivityResult>.launchImageEditor(
    context: Context,
    image: DeviceMedia.Image
) {
    val intent = Intent(context, ImageEditorActivity::class.java).apply {
        putExtra(
            ImageEditorActivity.KEY_IMAGE,
            image
        )
    }
    launch(intent)
}
