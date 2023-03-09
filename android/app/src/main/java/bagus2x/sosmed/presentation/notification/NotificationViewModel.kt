package bagus2x.sosmed.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.presentation.common.contact.ContactManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    contactManager: ContactManager
) : ViewModel() {
    private val shouldLoadContacts = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val contacts = shouldLoadContacts
        .filter { it }
        .flatMapLatest {
            contactManager.getContacts(40)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    fun loadContacts() {
        shouldLoadContacts.update { true }
    }
}
