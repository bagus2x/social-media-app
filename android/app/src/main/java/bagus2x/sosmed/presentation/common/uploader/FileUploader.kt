package bagus2x.sosmed.presentation.common.uploader

import android.content.Context
import bagus2x.sosmed.data.common.ktor
import bagus2x.sosmed.data.remote.HTTP_BASE_URL
import bagus2x.sosmed.presentation.common.BitmapUtils
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.size
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File

interface FileUploader {

    suspend fun upload(
        image: DeviceMedia.Image,
        onUpload: (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> },
    ): FileUpload

    suspend fun upload(
        video: DeviceMedia.Video,
        onUpload: (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> },
    ): Pair<FileUpload, FileUpload>
}

class FileUploaderImpl(
    private val dispatcher: CoroutineDispatcher,
    private val client: HttpClient,
    private val context: Context
) : FileUploader {

    private suspend fun upload(
        file: File,
        name: String,
        contentType: ContentType,
        onUpload: (bytesSentTotal: Long, contentLength: Long) -> Unit = { _, _ -> },
    ): FileUpload = withContext(dispatcher) {
        val compressed = Compressor.compress(context, file) {
            size(2_097_152) // 2 MB
        }

        ktor(client) {
            post("$HTTP_BASE_URL/upload") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("name", name)
                            append(
                                key = "file",
                                value = compressed.readBytes(),
                                headers = Headers.build {
                                    append(
                                        name = HttpHeaders.ContentType,
                                        value = contentType
                                    )
                                    append(
                                        name = HttpHeaders.ContentDisposition,
                                        value = "filename=\"${name}\""
                                    )
                                }
                            )
                        },
                        boundary = "WebAppBoundary"
                    )
                )
                onUpload(onUpload)
            }.body()
        }
    }

    override suspend fun upload(
        image: DeviceMedia.Image,
        onUpload: (bytesSentTotal: Long, contentLength: Long) -> Unit
    ): FileUpload {
        return upload(
            file = image.saveAsFile(context),
            name = image.name,
            contentType = ContentType.Image.Any,
            onUpload = onUpload
        )
    }

    override suspend fun upload(
        video: DeviceMedia.Video,
        onUpload: (bytesSentTotal: Long, contentLength: Long) -> Unit
    ): Pair<FileUpload, FileUpload> = coroutineScope {
        val thumbnailUrlDeferred = async {
            val bitmap = BitmapUtils.createBitmapFromVideo(video.file)
            val thumbnail = BitmapUtils.saveBitmap(bitmap, context.cacheDir)
            upload(
                file = thumbnail,
                name = video.name,
                contentType = ContentType.Image.Any,
                onUpload = onUpload
            )
        }
        val videoUrlDeferred = async {
            upload(
                file = video.file,
                name = video.name,
                contentType = ContentType.Video.Any,
                onUpload = onUpload
            )
        }

        thumbnailUrlDeferred.await() to videoUrlDeferred.await()
    }
}
