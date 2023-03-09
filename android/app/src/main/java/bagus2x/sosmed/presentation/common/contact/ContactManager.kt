package bagus2x.sosmed.presentation.common.contact

import android.content.ContentResolver
import android.content.Context
import android.os.Build
import android.provider.ContactsContract
import androidx.core.os.bundleOf
import androidx.paging.*
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

interface ContactManager {

    fun getContacts(pageSize: Int): Flow<PagingData<Contact>>
}

class ContactManagerImpl(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher
) : ContactManager {
    private val contactQueryUri = ContactsContract.Contacts.CONTENT_URI
    private val contactProjection = arrayOf(
        ContactsContract.Contacts._ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.HAS_PHONE_NUMBER
    )
    private val phoneQueryUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
    private val phoneProjection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        ContactsContract.CommonDataKinds.Phone.NUMBER
    )

    override fun getContacts(pageSize: Int): Flow<PagingData<Contact>> {
        val pagingSource = object : PagingSource<Int, Contact>() {
            override suspend fun load(
                params: LoadParams<Int>
            ): LoadResult<Int, Contact> {
                try {
                    val nextPageNumber = params.key ?: 1
                    val contacts = getContacts(params.loadSize, nextPageNumber)
                    return LoadResult.Page(
                        data = contacts,
                        prevKey = null,
                        nextKey = if (contacts.isNotEmpty()) nextPageNumber + 1 else null
                    )
                } catch (e: Exception) {
                    LoadResult.Error<Int, DeviceMedia>(e)
                    error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, Contact>): Int? {
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

    private suspend fun getContacts(pageSize: Int, page: Int): List<Contact> {
        return withContext(dispatcher) {
            queryContacts(pageSize, page)
        }
    }

    private fun queryContacts(pageSize: Int, page: Int): List<Contact> {
        val offset = (page - 1) * pageSize
        val selection = "${ContactsContract.Contacts.HAS_PHONE_NUMBER} = 1"
        val sort = ContactsContract.Contacts.DISPLAY_NAME
        val cursor = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val bundle = bundleOf(
                    ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
                    ContentResolver.QUERY_ARG_OFFSET to offset,
                    ContentResolver.QUERY_ARG_LIMIT to pageSize,
                    ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(sort),
                    ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_ASCENDING
                )
                context.contentResolver.query(contactQueryUri, contactProjection, bundle, null)
            }
            else -> {
                context.contentResolver.query(
                    contactQueryUri,
                    contactProjection,
                    selection,
                    null,
                    "$sort ASC LIMIT $pageSize OFFSET $offset"
                )
            }

        } ?: return emptyList()

        return buildList {
            cursor.use {
                repeat(cursor.count) { index ->
                    cursor.moveToPosition(index)
                    val id = cursor
                        .getColumnIndex(ContactsContract.Contacts._ID)
                        .let(cursor::getLong)
                    val name = cursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        .let(cursor::getString)
                    val numbers = queryPhoneNumberByContactId(id)
                    add(Contact(id, name, numbers))
                }
            }
        }
    }

    private fun queryPhoneNumberByContactId(contactId: Long): List<String> {
        val selection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $contactId"
        val cursor = context.contentResolver.query(
            phoneQueryUri,
            phoneProjection, selection,
            null,
            null
        ) ?: return emptyList()

        return buildList {
            cursor.use {
                repeat(cursor.count) { index ->
                    cursor.moveToPosition(index)
                    val number = cursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        .let(cursor::getString)
                    add(number)
                }
            }
        }
    }
}
