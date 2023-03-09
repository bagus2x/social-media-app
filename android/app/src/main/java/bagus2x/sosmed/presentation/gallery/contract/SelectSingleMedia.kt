package bagus2x.sosmed.presentation.gallery.contract

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.parcelable
import bagus2x.sosmed.presentation.gallery.GalleryActivity

class SelectSingleMedia : ActivityResultContract<MediaType, DeviceMedia?>() {
    override fun createIntent(context: Context, input: MediaType): Intent {
        return Intent(context, GalleryActivity::class.java).apply {
            putExtra(GalleryActivity.KEY_MEDIA_TYPE, input)
            putExtra(GalleryActivity.KEY_SELECTOR, GalleryActivity.KEY_SINGLE)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): DeviceMedia? {
        return intent?.parcelable(GalleryActivity.KEY_SELECTED_DEVICE_MEDIA)
    }
}
