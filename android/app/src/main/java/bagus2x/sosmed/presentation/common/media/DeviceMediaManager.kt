package bagus2x.sosmed.presentation.common.media

import android.content.ContentResolver.*
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import androidx.paging.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.time.Instant
import java.time.ZoneId

interface DeviceMediaManager {

    suspend fun getImagesAndVideos(pageSize: Int, page: Int): List<DeviceMedia>

    suspend fun getImages(pageSize: Int, page: Int): List<DeviceMedia.Image>

    suspend fun getVideos(pageSize: Int, page: Int): List<DeviceMedia.Video>

    suspend fun getImageOrVideo(id: Long): DeviceMedia

    suspend fun getImageOrVideo(uri: Uri): DeviceMedia

    fun getImagesAndVideos(pageSize: Int): Flow<PagingData<DeviceMedia>>

    fun getImages(pageSize: Int): Flow<PagingData<DeviceMedia.Image>>

    fun getVideos(pageSize: Int): Flow<PagingData<DeviceMedia.Video>>
}

class DeviceMediaManagerImpl internal constructor(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher,
) : DeviceMediaManager {
    private val queryUri by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Files.getContentUri("external")
        }
    }

    private val projection by lazy {
        arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.SIZE
        )
    }

    override fun getImagesAndVideos(pageSize: Int): Flow<PagingData<DeviceMedia>> {
        return getImagesAndVideos(pageSize, this::getImagesAndVideos)
    }

    private fun <T : DeviceMedia> getImagesAndVideos(
        pageSize: Int,
        factory: suspend (pageSize: Int, page: Int) -> List<T>
    ): Flow<PagingData<T>> {
        val pagingSource = object : PagingSource<Int, T>() {
            override suspend fun load(
                params: LoadParams<Int>
            ): LoadResult<Int, T> {
                try {
                    val nextPageNumber = params.key ?: 1
                    val deviceMedias = factory(params.loadSize, nextPageNumber)
                    return LoadResult.Page(
                        data = deviceMedias,
                        prevKey = null,
                        nextKey = if (deviceMedias.isNotEmpty()) nextPageNumber + 1 else null
                    )
                } catch (e: Exception) {
                    LoadResult.Error<Int, T>(e)
                    error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, T>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    val anchorPage = state.closestPageToPosition(anchorPosition)
                    anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
                }
            }
        }

        val pager = Pager(
            config = PagingConfig(pageSize),
            pagingSourceFactory = { pagingSource }
        )

        return pager.flow
    }

    override suspend fun getImages(pageSize: Int, page: Int): List<DeviceMedia.Image> {
        return withContext(dispatcher) { queryImages(pageSize, page) }
    }

    override fun getImages(pageSize: Int): Flow<PagingData<DeviceMedia.Image>> {
        return getImagesAndVideos(pageSize, this::getImages)
    }

    override suspend fun getVideos(pageSize: Int, page: Int): List<DeviceMedia.Video> {
        return withContext(dispatcher) { queryVideos(pageSize, page) }
    }

    override fun getVideos(pageSize: Int): Flow<PagingData<DeviceMedia.Video>> {
        return getImagesAndVideos(pageSize, this::getVideos)
    }

    override suspend fun getImagesAndVideos(pageSize: Int, page: Int): List<DeviceMedia> {
        return withContext(dispatcher) { queryImagesAndVideos(pageSize, page) }
    }

    private fun queryImages(pageSize: Int, page: Int): List<DeviceMedia.Image> {
        return queryImagesAndVideos(
            pageSize = pageSize,
            page = page,
            selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}"
        )
            .filterIsInstance<DeviceMedia.Image>()
    }

    private fun queryVideos(pageSize: Int, page: Int): List<DeviceMedia.Video> {
        return queryImagesAndVideos(
            pageSize = pageSize,
            page = page,
            selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}"
        )
            .filterIsInstance<DeviceMedia.Video>()
    }

    private fun queryImagesAndVideos(pageSize: Int, page: Int): List<DeviceMedia> {
        val selection = """
            ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}
            OR
            ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}
        """.trimIndent()
        return queryImagesAndVideos(pageSize, page, selection)
    }

    private fun queryImagesAndVideos(
        pageSize: Int,
        page: Int,
        selection: String
    ): List<DeviceMedia> {
        val offset = (page - 1) * pageSize
        val sort = MediaStore.Images.ImageColumns.DATE_ADDED

        val cursor = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val bundle = bundleOf(
                    QUERY_ARG_SQL_SELECTION to selection,
                    QUERY_ARG_OFFSET to offset,
                    QUERY_ARG_LIMIT to pageSize,
                    QUERY_ARG_SORT_COLUMNS to arrayOf(sort),
                    QUERY_ARG_SORT_DIRECTION to QUERY_SORT_DIRECTION_DESCENDING
                )
                context.contentResolver.query(
                    queryUri,
                    projection,
                    bundle,
                    null
                )
            }
            else -> {
                context.contentResolver.query(
                    queryUri,
                    projection,
                    selection,
                    null,
                    "$sort DESC LIMIT $pageSize OFFSET $offset"
                )
            }
        } ?: return emptyList()

        return buildList {
            cursor.use {
                repeat(cursor.count) { index ->
                    try {
                        add(cursor.deviceMedia(index))
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
    }

    override suspend fun getImageOrVideo(id: Long): DeviceMedia {
        return withContext(dispatcher) {
            queryImageAndVideo(id)
        }
    }

    override suspend fun getImageOrVideo(uri: Uri): DeviceMedia {
        return withContext(dispatcher) {
            queryImageAndVideo(uri)
        }
    }

    private fun queryImageAndVideo(uri: Uri): DeviceMedia {
        val cursor = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                context.contentResolver.query(
                    uri,
                    projection,
                    null,
                    null
                )
            }
            else -> {
                context.contentResolver.query(
                    uri,
                    projection,
                    null,
                    null,
                    null
                )
            }
        }
        requireNotNull(cursor)

        return cursor.use {
            cursor.deviceMedia(0)
        }
    }

    private fun queryImageAndVideo(id: Long): DeviceMedia {
        val selection = """
            (
            ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE}
            OR
            ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ${MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO}
            )
            AND
            ${MediaStore.Files.FileColumns._ID} = $id
        """.trimIndent()

        val cursor = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val bundle = bundleOf(QUERY_ARG_SQL_SELECTION to selection)
                context.contentResolver.query(
                    queryUri,
                    projection,
                    bundle,
                    null
                )
            }
            else -> {
                context.contentResolver.query(
                    queryUri,
                    projection,
                    selection,
                    null,
                    null
                )
            }
        }
        requireNotNull(cursor)

        return cursor.use { cursor.deviceMedia(0) }
    }

    private fun Cursor.deviceMedia(index: Int): DeviceMedia {
        moveToPosition(index)
        val id = getColumnIndex(MediaStore.Files.FileColumns._ID).let(::getLong)
        val data = getColumnIndex(MediaStore.Files.FileColumns.DATA).let(::getString)
        val mimeType = getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE).let(::getString)
        val title = getColumnIndex(MediaStore.Files.FileColumns.TITLE).let(::getString)
        val size = getColumnIndex(MediaStore.Files.FileColumns.SIZE).let(::getLong)
        val dateAdded = getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
            .let(::getLong)
            .let { Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }
        val file = File(data)

        if (mimeType.contains("video")) {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, Uri.fromFile(file))
            val time = retriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull()
            retriever.release()

            return DeviceMedia.Video(
                id = id,
                file = file,
                name = title,
                size = size,
                duration = time ?: 0,
                dateAdded = dateAdded
            )
        } else {
            return DeviceMedia.Image(
                id = id,
                file = file,
                name = title,
                size = size,
                dateAdded = dateAdded
            )
        }
    }
}
