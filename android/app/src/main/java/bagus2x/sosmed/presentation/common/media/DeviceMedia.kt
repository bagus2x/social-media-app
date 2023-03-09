package bagus2x.sosmed.presentation.common.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asAndroidColorFilter
import androidx.core.graphics.withSave
import bagus2x.sosmed.presentation.common.BitmapUtils
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.parcelize.Parcelize
import java.io.File
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

@Parcelize
sealed class DeviceMedia(
    open val id: Long,
    open val file: File,
    open val name: String,
    open val size: Long,
    open val dateAdded: LocalDateTime
) : Parcelable {
    @Parcelize
    @Immutable
    data class Image(
        override val id: Long,
        override val file: File,
        override val name: String,
        override val size: Long,
        override val dateAdded: LocalDateTime,
        val filter: ImageFilter = ImageFilter.Normal,
        val stickers: List<ImageSticker> = emptyList()
    ) : DeviceMedia(id, file, name, size, dateAdded), Parcelable {

        // Save an edited image as file
        suspend fun saveAsFile(context: Context): File {
            val bitmap = saveAsBitmap(context)
            return BitmapUtils.saveBitmap(bitmap, context.cacheDir)
        }

        // Save an edited image as bitmap
        suspend fun saveAsBitmap(context: Context): Bitmap {
            val loader = context.imageLoader
            val request = ImageRequest.Builder(context).data(file).build()
//            val result = (loader.execute(request) as SuccessResult).drawable
//            val bitmap = (result as BitmapDrawable).bitmap
            val input = when (val result = loader.execute(request)) {
                is SuccessResult -> {
                    (result.drawable as BitmapDrawable).bitmap
                }
                is ErrorResult -> {
                    throw result.throwable
                }
            }

            return saveAsBitmap(input, context)
        }

        suspend fun saveAsBitmap(input: Bitmap, context: Context): Bitmap = coroutineScope {
            if (filter == ImageFilter.Normal && stickers.isEmpty()) {
                return@coroutineScope input
            }

            val width = input.width
            val height = input.height
            val output = Bitmap.createBitmap(input).copy(Bitmap.Config.ARGB_8888, true)

            val stickersInBitmap = stickers
                .map { sticker ->
                    async {
                        BitmapFactory.decodeResource(
                            context.resources,
                            sticker.stickerRes
                        )
                    }
                }
                .awaitAll()

            Canvas(output).apply {
                stickers.forEachIndexed { index, imageSticker ->
                    val stickerBitmap = stickersInBitmap[index]

                    drawBitmap(
                        output,
                        0f,
                        0f,
                        Paint().apply { colorFilter = filter.colorFilter?.asAndroidColorFilter() }
                    )

                    withSave {
                        scale(
                            imageSticker.scale,
                            imageSticker.scale,
                            (imageSticker.rect.left + imageSticker.rect.width / 2) * width,
                            (imageSticker.rect.top + imageSticker.rect.height / 2) * height,
                        )
                        rotate(
                            imageSticker.rotation,
                            (imageSticker.rect.left + imageSticker.rect.width / 2) * width,
                            (imageSticker.rect.top + imageSticker.rect.height / 2) * height,
                        )
                        drawBitmap(
                            stickerBitmap,
                            null,
                            imageSticker.rect.asAndroidRect(width, height),
                            null,
                        )
                    }
                }
            }

            output
        }
    }

    @Parcelize
    data class Video(
        override val id: Long,
        override val file: File,
        override val name: String,
        override val size: Long,
        val duration: Long,
        override val dateAdded: LocalDateTime
    ) : DeviceMedia(id, file, name, size, dateAdded), Parcelable
}

@Parcelize
@Immutable
enum class ImageFilter : Parcelable {
    Normal,
    Black;

    val colorFilter: ColorFilter?
        get() {
            return when (this) {
                Black -> ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                Normal -> null
            }
        }
}

@Parcelize
@Immutable
data class ImageSticker(
    val id: String = UUID.randomUUID().toString(),
    @DrawableRes val stickerRes: Int,
    val translationX: Float,
    val translationY: Float,
    val scale: Float,
    val rotation: Float,
    val rect: Rect
) : Parcelable

@Parcelize
@Immutable
data class Rect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) : Parcelable {
    val width get() = right - left
    val height get() = bottom - top

    operator fun times(other: Float): Rect {
        return Rect(
            left = left * other,
            top = top * other,
            right = right * other,
            bottom = bottom * other
        )
    }

    fun asAndroidRect(width: Int, height: Int): android.graphics.Rect {
        return android.graphics.Rect(
            (left * width).roundToInt(),
            (top * height).roundToInt(),
            (right * width).roundToInt(),
            (bottom * height).roundToInt()
        )
    }

    companion object {
        val Empty = Rect(0f, 0f, 0f, 0f)
    }
}
