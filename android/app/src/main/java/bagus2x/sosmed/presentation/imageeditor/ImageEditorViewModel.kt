package bagus2x.sosmed.presentation.imageeditor

import androidx.lifecycle.ViewModel
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class ImageEditorViewModel : ViewModel() {
    private val _image = MutableStateFlow<DeviceMedia.Image?>(null)
    val image = _image.asStateFlow()

    private var init = false

    fun initImage(image: DeviceMedia.Image) {
        if (!init) {
            _image.update { image }
        }
        init = true
    }
}
