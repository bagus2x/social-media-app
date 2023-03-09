package bagus2x.sosmed.presentation.gallery.contract

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class MediaType : Parcelable {

    object Video : MediaType()

    object Image : MediaType()

    object ImageAndVideo : MediaType()
}
