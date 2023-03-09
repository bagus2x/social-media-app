package bagus2x.sosmed.presentation.common

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.media.ThumbnailUtils
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Size
import android.view.PixelCopy
import android.view.View
import android.view.Window
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resumeWithException

object BitmapUtils {

    fun createBitmapFromVideo(video: File): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ThumbnailUtils.createVideoThumbnail(video, Size(512, 512), null)
        } else {
            @Suppress("DEPRECATION") ThumbnailUtils.createVideoThumbnail(
                video.path, MediaStore.Images.Thumbnails.MINI_KIND
            )
        }
        if (bitmap == null) {
            throw Exception("Failed to create video thumbnail")
        }
        return bitmap
    }

    fun saveBitmap(
        bitmap: Bitmap,
        dir: File,
        filename: String = Misc.getRandomFileName()
    ): File {
        val f = File(dir, filename)
        f.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
        val bitmapData = bos.toByteArray()

        //write the bytes in file
        val fos = FileOutputStream(f)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        return f
    }

    fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        filename: String = Misc.getRandomFileName()
    ): File {
        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            val resolver = context.contentResolver
            //Content resolver will process the content-values
            val contentValues = ContentValues().apply {
                //putting file information in content values
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            //Inserting the contentValues to contentResolver and getting the Uri
            val imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            requireNotNull(imageUri)

            //Opening an output stream with the Uri that we got
            requireNotNull(resolver.openOutputStream(imageUri)).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            return UriUtils.convertUriToFile(context, imageUri)
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(dir, filename)
            FileOutputStream(image).use {
                //Finally writing the bitmap to the output stream that we opened 
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            return image
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun createBitmapFromView(view: View) = suspendCancellableCoroutine<Bitmap> { cont ->
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                val temp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

                // Above Android O, use PixelCopy due
                // https://stackoverflow.com/questions/58314397/
                val window: Window = (view.context as Activity).window

                val location = IntArray(2)

                view.getLocationInWindow(location)

                val rect = Rect(
                    location[0], location[1], location[0] + view.width, location[1] + view.height
                )

                val onPixelCopyListener: PixelCopy.OnPixelCopyFinishedListener =
                    PixelCopy.OnPixelCopyFinishedListener { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            cont.resume(temp, cont::resumeWithException)
                        } else {
                            cont.resumeWithException(RuntimeException("Error while copying pixels, copy result: $copyResult"))
                        }
                    }

                PixelCopy.request(
                    window, rect, temp, onPixelCopyListener, Handler(Looper.getMainLooper())
                )
            } else {

                val temporalBitmap =
                    Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)

                val canvas = Canvas(temporalBitmap)

                view.draw(canvas)

                canvas.setBitmap(null)

                cont.resume(temporalBitmap, cont::resumeWithException)
            }

        } catch (exception: Exception) {
            cont.resumeWithException(exception)
        }
    }
}
