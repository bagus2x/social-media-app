package bagus2x.sosmed.presentation.common

import android.content.Context
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import java.io.File

object ComposeScreenshot {

    suspend fun screenshot(context: Context, content: @Composable () -> Unit): File {
        val view = ComposeView(context).apply {
            setContent(content)
            layoutParams = ViewGroup.LayoutParams(
                200,
               500
            )
        }
        val bitmap = BitmapUtils.createBitmapFromView(view)
        return BitmapUtils.saveBitmapToGallery(context, bitmap)
    }
}
