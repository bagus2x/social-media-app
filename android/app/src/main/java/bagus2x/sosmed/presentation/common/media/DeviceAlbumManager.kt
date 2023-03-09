package bagus2x.sosmed.presentation.common.media

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface DeviceAlbumManager {

    suspend fun getAlbums(pageSize: Int = 50, page: Int = 1): List<DeviceAlbum>
}

class DeviceAlbumManagerImpl internal constructor(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
    private val deviceMediaManager: DeviceMediaManager
) : DeviceAlbumManager {
    private val queryUri by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }
    }

    private val projectionAlbum by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                "MAX(${MediaStore.Files.FileColumns._ID})",
                MediaStore.Files.FileColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
            )
        } else {
            arrayOf(
                "MAX(${MediaStore.Images.ImageColumns._ID})",
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
            )
        }
    }


    override suspend fun getAlbums(pageSize: Int, page: Int): List<DeviceAlbum> =
        withContext(dispatcher) {
            val selection = """
            ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}
            OR
            ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}
        """.trimIndent()

            val offset = (page - 1) * pageSize
            val sort = MediaStore.Images.ImageColumns.DATE_TAKEN

            val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bundle = bundleOf(
                    ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
                    ContentResolver.QUERY_ARG_OFFSET to offset,
                    ContentResolver.QUERY_ARG_LIMIT to pageSize,
                    ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(sort),
                    ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
                    ContentResolver.QUERY_ARG_GROUP_COLUMNS to arrayOf(
                        MediaStore.Files.FileColumns.BUCKET_ID,
                        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
                    )
                )
                context.contentResolver.query(
                    queryUri,
                    projectionAlbum,
                    bundle,
                    null
                )
            } else {
                context.contentResolver.query(
                    queryUri,
                    projectionAlbum,
                    selection,
                    null,
                    "$sort DESC LIMIT $pageSize OFFSET $offset"
                )
            }

            requireNotNull(cursor)

            val bucketIds = buildList {
                cursor.use {
                    repeat(cursor.count) { index ->
                        cursor.moveToPosition(index)
                        val mediaId = cursor
                            .getLong(0)
                        val bucketId = cursor
                            .getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)
                            .let(cursor::getLong)
                        val bucketDisplayName = cursor
                            .getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
                            .let(cursor::getString)
                        add(Triple(mediaId, bucketId, bucketDisplayName))
                    }
                }
            }
            buildList {
                bucketIds.forEach { (latestMediaId, bucketId, bucketName) ->
                    val deviceMedia = runCatching {
                        deviceMediaManager.getImageOrVideo(latestMediaId)
                    }.getOrNull()
                    val album = DeviceAlbum(
                        id = bucketId,
                        name = bucketName,
                        preview = deviceMedia ?: return@forEach
                    )
                    add(album)
                }
            }
        }
}
