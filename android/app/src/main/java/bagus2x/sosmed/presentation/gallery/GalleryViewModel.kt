package bagus2x.sosmed.presentation.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.media.DeviceMediaManager
import bagus2x.sosmed.presentation.gallery.contract.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class GalleryViewModel @Inject constructor(
    deviceMediaManager: DeviceMediaManager
) : ViewModel() {
    private val _state = MutableStateFlow(GalleryState())
    val state = _state.asStateFlow()
    val deviceMedias = state
        .filter { it.shouldLoad }
        .map { it.type }
        .distinctUntilChanged()
        .flatMapLatest { type ->
            val flow = when (type) {
                is MediaType.Image -> deviceMediaManager.getImages(pageSize = 20)
                is MediaType.Video -> deviceMediaManager.getVideos(pageSize = 20)
                is MediaType.ImageAndVideo -> deviceMediaManager.getImagesAndVideos(pageSize = 20)
            }
            flow.map { it -> it.map { it } }
        }
        .catch { e ->
            Timber.e(e)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )
    private var init = false

    fun init(
        selectedMedias: List<DeviceMedia>,
        type: MediaType,
        multiple: Boolean,
        max: Int
    ) {
        if (!init) {
            _state.update { state ->
                state.copy(
                    selectedMedias = selectedMedias,
                    type = type,
                    multiple = multiple,
                    max = max,
                    shouldLoad = true
                )
            }
        }
        init = true
    }

    fun selectDeviceMedia(deviceMedia: DeviceMedia) = _state.update { state ->
        if (!state.selectedMedias.contains(deviceMedia)) {
            state.copy(selectedMedias = state.selectedMedias + deviceMedia)
        } else {
            state
        }
    }

    fun unselectDeviceMedia(deviceMedia: DeviceMedia) = _state.update { state ->
        state.copy(
            selectedMedias = state.selectedMedias
                .toMutableList()
                .apply { remove(deviceMedia) }
        )
    }
}
