package bagus2x.sosmed.presentation.common.components

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.async
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun dominantColor(
    urls: List<String>,
    defaultColor: Color
): State<List<Color>> {
    val context = LocalContext.current
    val initialValue = remember(urls) { urls.map { defaultColor } }
    return produceState(initialValue) {
        val colors = urls.map { url ->
            async {
                val request = ImageRequest
                    .Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .build()
                val loader = context.imageLoader

                val bitmap = when (val result = loader.execute(request)) {
                    is SuccessResult -> {
                        (result.drawable as BitmapDrawable).bitmap
                    }
                    is ErrorResult -> {
                        Timber.e(result.throwable)
                        return@async null
                    }
                }

                getDominantColor(bitmap, defaultColor)
            }
        }
            .map { it.await() ?: defaultColor }
        value = colors
    }
}

@Composable
fun dominantColor(
    url: String,
    defaultColor: Color
): State<Color> {
    val context = LocalContext.current
    return produceState(defaultColor) {
        val request = ImageRequest
            .Builder(context)
            .data(url)
            .allowHardware(false)
            .build()
        val loader = context.imageLoader

        val bitmap = when (val result = loader.execute(request)) {
            is SuccessResult -> {
                (result.drawable as BitmapDrawable).bitmap
            }
            is ErrorResult -> {
                Timber.e(result.throwable)
                return@produceState
            }
        }

        value = getDominantColor(bitmap, defaultColor)
    }
}

suspend fun getDominantColor(bitmap: Bitmap, default: Color = Color.Black): Color =
    suspendCoroutine { cont ->
        Palette.Builder(bitmap).generate { palette ->
            if (palette == null) {
                cont.resume(default)
                Timber.e("Palette is null")
            } else {
                val color = palette.getDominantColor(default.toArgb())
                cont.resume(Color(color))
            }
        }
    }
