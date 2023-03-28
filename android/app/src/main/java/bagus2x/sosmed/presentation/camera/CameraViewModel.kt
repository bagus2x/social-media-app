package bagus2x.sosmed.presentation.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.media.DeviceMediaManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val deviceMediaManager: DeviceMediaManager
) : ViewModel() {
    private val _capturedDeviceMedia = MutableStateFlow<DeviceMedia?>(null)
    val capturedDeviceMedia = _capturedDeviceMedia.asStateFlow()

    fun setCapturedUri(uri: Uri?) {
        requireNotNull(uri) { "Uri is not allowed to be null" }
        viewModelScope.launch {
            _capturedDeviceMedia.update { deviceMediaManager.getImageOrVideo(uri) }
        }
    }
}
