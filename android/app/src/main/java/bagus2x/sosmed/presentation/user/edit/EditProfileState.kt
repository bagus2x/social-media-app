package bagus2x.sosmed.presentation.user.edit

import androidx.compose.runtime.Stable
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import java.time.LocalDate

@Stable
data class EditProfileState(
    val name: String = "",
    val defaultPhoto: String? = null,
    val photo: DeviceMedia.Image? = null,
    val defaultHeader: String? = null,
    val header: DeviceMedia.Image? = null,
    val bio: String = "",
    val location: String = "",
    val website: String = "",
    val dateOfBirth: LocalDate? = null,
    val loading: Boolean = false,
    val snackbar: String = "",
    val updated: Boolean = false
) {
    val isFulfilled: Boolean
        get() = name.isNotBlank() && bio.length < 500
}
