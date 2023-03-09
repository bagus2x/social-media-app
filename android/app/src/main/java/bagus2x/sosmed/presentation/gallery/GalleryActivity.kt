package bagus2x.sosmed.presentation.gallery

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.components.LocalProvider
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.parcelable
import bagus2x.sosmed.presentation.common.parcelableArrayList
import bagus2x.sosmed.presentation.common.theme.MedsosTheme
import bagus2x.sosmed.presentation.gallery.contract.MediaType
import bagus2x.sosmed.presentation.home.components.Permission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryActivity : ComponentActivity() {
    private val viewModel by viewModels<GalleryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val selectedMedias = intent
            .parcelableArrayList(KEY_SELECTED_DEVICE_MEDIA)
            ?: emptyList<DeviceMedia>()
        val type = intent.parcelable<MediaType>(KEY_MEDIA_TYPE)!!
        val selector = when (val selector = intent.getStringExtra(KEY_SELECTOR)) {
            KEY_MULTIPLE -> KEY_MULTIPLE
            KEY_SINGLE -> KEY_SINGLE
            else -> error("Selector should be single or multiple. Found: $selector")
        }
        val max = intent.getIntExtra(KEY_MAX, 1)

        setContent {
            MedsosTheme {
                LocalProvider {
                    val deviceMedias = viewModel.deviceMedias.collectAsLazyPagingItems()
                    val state by viewModel.state.collectAsStateWithLifecycle()
                    Permission(
                        permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                        title = stringResource(R.string.text_browse_gallery),
                        permissionText = stringResource(R.string.text_gallery_require_permission),
                        rationaleText = stringResource(R.string.text_gallery_rationale),
                        skipp = this::finish,
                        modifier = Modifier.systemBarsPadding(),
                    ) {
                        LaunchedEffect(Unit) {
                            viewModel.init(
                                selectedMedias = selectedMedias,
                                type = type,
                                multiple = selector == KEY_MULTIPLE,
                                max = max,
                            )
                        }
                        GalleryScreen(
                            stateProvider = { state },
                            options = deviceMedias,
                            onSelectDeviceMedia = { media ->
                                if (state.multiple) {
                                    viewModel.selectDeviceMedia(media)
                                } else {
                                    val intent = Intent().apply {
                                        putExtra(KEY_SELECTED_DEVICE_MEDIA, media)
                                    }
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }
                            },
                            onUnselectDeviceMedia = viewModel::unselectDeviceMedia,
                            onCloseClicked = this::finish,
                            onAddClicked = {
                                val intent = Intent().apply {
                                    putExtra(
                                        KEY_SELECTED_DEVICE_MEDIA,
                                        ArrayList(state.selectedMedias)
                                    )
                                }
                                setResult(RESULT_OK, intent)
                                finish()
                            },
                        )
                    }
                }
            }
        }
    }

    companion object {
        internal const val RESULT_OK = 0
        internal const val KEY_SELECTED_DEVICE_MEDIA = "selected_media"
        internal const val KEY_MEDIA_TYPE = "media_type"
        internal const val KEY_SELECTOR = "selector"
        internal const val KEY_MULTIPLE = "multiple"
        internal const val KEY_SINGLE = "single"
        internal const val KEY_MAX = "max"
    }
}

