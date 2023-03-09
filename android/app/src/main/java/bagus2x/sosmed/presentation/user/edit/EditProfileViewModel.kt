package bagus2x.sosmed.presentation.user.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.domain.usecase.GetUserUseCase
import bagus2x.sosmed.domain.usecase.UpdateUserUseCase
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.uploader.FileUploader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val fileUploader: FileUploader
) : ViewModel() {
    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    init {
        loadUser()
    }

    fun consumeSnackbar() = _state.update { state ->
        state.copy(snackbar = "")
    }

    fun setName(name: String) = _state.update { state ->
        state.copy(name = name)
    }

    fun setPhoto(photo: DeviceMedia.Image?) = _state.update { state ->
        state.copy(photo = photo)
    }

    fun setHeader(header: DeviceMedia.Image?) = _state.update { state ->
        state.copy(header = header)
    }

    fun setBio(bio: String) = _state.update { state ->
        state.copy(bio = bio)
    }

    fun setLocation(location: String) = _state.update { state ->
        state.copy(location = location)
    }

    fun setWebsite(website: String) = _state.update { state ->
        state.copy(website = website)
    }

    fun setDateOfBirth(dateOfBirth: LocalDate?) = _state.update { state ->
        state.copy(dateOfBirth = dateOfBirth)
    }

    private fun loadUser() {
        viewModelScope.launch {
            getUserUseCase()
                .catch { e ->
                    _state.update { state ->
                        state.copy(
                            snackbar = e.message ?: "Failed to load user"
                        )
                    }
                    Timber.e(e)
                }
                .filterNotNull()
                .collect { user ->
                    _state.update { state ->
                        state.copy(
                            name = user.name,
                            defaultPhoto = user.photo,
                            bio = user.bio ?: "",
                            location = user.location ?: "",
                            website = user.website ?: "",
                            dateOfBirth = null
                        )
                    }
                }
        }
    }

    fun saveAndUpdate() {
        viewModelScope.launch {
            _state.update { state -> state.copy(loading = true) }
            try {
                updateUserUseCase(
                    name = state.value.name,
                    photo = state.value.photo?.let { fileUploader.upload(it) }?.contentUrl,
                    header = state.value.header?.let { fileUploader.upload(it) }?.contentUrl,
                    bio = state.value.bio,
                    location = state.value.location,
                    website = state.value.website,
                    dateOfBirth = null
                )
                _state.update { state -> state.copy(loading = false, updated = true) }
            } catch (e: Exception) {
                _state.update { state ->
                    state.copy(
                        loading = false,
                        snackbar = e.message ?: "Failed to update"
                    )
                }
                Timber.e(e)
            }
        }
    }
}
