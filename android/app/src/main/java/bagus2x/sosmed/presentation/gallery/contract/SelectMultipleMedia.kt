package bagus2x.sosmed.presentation.gallery.contract

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.parcelableArrayList
import bagus2x.sosmed.presentation.gallery.GalleryActivity

class SelectMultipleMedia(
    private val max: Int,
    private val selected: List<DeviceMedia> = emptyList()
) : ActivityResultContract<MediaType, List<DeviceMedia>?>() {
    override fun createIntent(context: Context, input: MediaType): Intent {
        return Intent(context, GalleryActivity::class.java).apply {
            putExtra(GalleryActivity.KEY_MEDIA_TYPE, input)
            putExtra(GalleryActivity.KEY_SELECTOR, GalleryActivity.KEY_MULTIPLE)
            putParcelableArrayListExtra(
                GalleryActivity.KEY_SELECTED_DEVICE_MEDIA,
                ArrayList(selected)
            )
            putExtra(GalleryActivity.KEY_MAX, max)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<DeviceMedia>? {
        return intent?.parcelableArrayList(GalleryActivity.KEY_SELECTED_DEVICE_MEDIA)
    }
}
