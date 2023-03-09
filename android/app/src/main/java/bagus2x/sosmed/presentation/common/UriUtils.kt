package bagus2x.sosmed.presentation.common

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File

object UriUtils {

    fun convertUriToFile(context: Context, uri: Uri): File {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
        requireNotNull(cursor)
        cursor.moveToFirst()
        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
        val filePath: String = cursor.getString(columnIndex)
        cursor.close()
        return File(filePath)
    }
}
