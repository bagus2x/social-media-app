package bagus2x.sosmed.presentation.feed.newfeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.usecase.CreateFeedUseCase
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.media.DeviceMediaManager
import bagus2x.sosmed.presentation.common.translation.Translator
import bagus2x.sosmed.presentation.common.uploader.FileUploader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NewFeedViewModel @Inject constructor(
    private val deviceMediaManager: DeviceMediaManager,
    private val fileUploader: FileUploader,
    private val translator: Translator,
    private val createFeedUseCase: CreateFeedUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(NewFeedState())
    val state = _state.asStateFlow()

    private val shouldLoadMedia = MutableStateFlow(false)

    init {
        loadImagesAndVideos()
    }

    fun loadDeviceMedias() {
        shouldLoadMedia.update { true }
    }

    private fun loadImagesAndVideos() {
        viewModelScope.launch {
            shouldLoadMedia.collect { shouldLoadMedia ->
                if (shouldLoadMedia) {
                    try {
                        val medias = deviceMediaManager.getImagesAndVideos(10, 1)
                        _state.update { it.copy(medias = medias) }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
    }

    fun snackbarConsumed() = _state.update { state ->
        state.copy(snackbar = "")
    }

    fun setDescription(description: String) = _state.update { state ->
        state.copy(description = description)
    }

    fun selectMedia(media: DeviceMedia) = _state.update { state ->
        val selectedMedias = state.selectedMedias.toPersistentList()
        if (selectedMedias.contains(media)) {
            return@update state
        }
        state.copy(selectedMedias = selectedMedias + media)
    }

    fun unselectMedia(medias: DeviceMedia) = _state.update { state ->
        val selectedMedias = state.selectedMedias.toPersistentList()
        state.copy(selectedMedias = selectedMedias.filter { it.id != medias.id }.toImmutableList())
    }

    fun setSelectedMedia(medias: List<DeviceMedia>) = _state.update { state ->
        state.copy(selectedMedias = medias)
    }

    fun replaceMedia(media: DeviceMedia) = _state.update { state ->
        val selectedMedias = state
            .selectedMedias
            .toPersistentList()
            .map { if (it.id == media.id) media else it }.toImmutableList()
        state.copy(selectedMedias = selectedMedias)
    }

    fun create() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            try {
                createFeedUseCase(
                    description = state.value.description,
                    medias = state.value.selectedMedias.map { upload(it) },
                    language = translator.sourceLanguage(text = state.value.description)
                )
                _state.update { it.copy(created = true) }
            } catch (e: Exception) {
                _state.update { it.copy(snackbar = e.message ?: "Failed to create feed") }
                Timber.e(e)
            }
            _state.update { it.copy(loading = false) }
        }
    }

    private suspend fun upload(media: DeviceMedia): Media {
        return when (media) {
            is DeviceMedia.Image -> {
                val uploadedImage = fileUploader.upload(media)
                Media.Image(imageUrl = uploadedImage.contentUrl)
            }
            is DeviceMedia.Video -> {
                val (uploadedThumbnail, uploadedVideo) = fileUploader.upload(media)
                Media.Video(
                    thumbnailUrl = uploadedThumbnail.contentUrl,
                    videoUrl = uploadedVideo.contentUrl
                )
            }
        }
    }
}
