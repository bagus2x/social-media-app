package bagus2x.sosmed.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.domain.usecase.GetNotificationUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    getNotificationUsecase: GetNotificationUsecase
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationState())
    val state = _state.asStateFlow()

    val notifications = getNotificationUsecase()
        .cachedIn(viewModelScope)
        .catch { e ->
            _state.update { state -> state.copy(snackbar = e.message ?: "") }
            Timber.e(e)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    fun snackbarConsumed() = _state.update { state ->
        state.copy(snackbar = "")
    }
}
