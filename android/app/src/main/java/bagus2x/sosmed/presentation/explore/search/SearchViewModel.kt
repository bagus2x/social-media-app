package bagus2x.sosmed.presentation.explore.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.domain.usecase.SearchFeedsUseCase
import bagus2x.sosmed.domain.usecase.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchViewModel @Inject constructor(
    searchUsersUseCase: SearchUsersUseCase,
    searchFeedsUseCase: SearchFeedsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()
    val users = state
        .map { it.query }
        .distinctUntilChanged()
        .debounce(500.milliseconds)
        .flatMapLatest { query ->
            if (query.isNotBlank()) searchUsersUseCase(query)
            else flow { emit(PagingData.empty()) }
        }
        .catch { e ->
            _state.update { state -> state.copy(snackbar = e.message ?: "Failed to search") }
            Timber.e(e)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )
    val feeds = state
        .map { it.query }
        .distinctUntilChanged()
        .debounce(500.milliseconds)
        .flatMapLatest { query ->
            if (query.isNotBlank()) searchFeedsUseCase(query)
            else flow { emit(PagingData.empty()) }
        }
        .catch { e ->
            _state.update { state -> state.copy(snackbar = e.message ?: "Failed to search") }
            Timber.e(e)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )

    fun consumeSnackbar() = _state.update { state ->
        state.copy(snackbar = "")
    }

    fun setQuery(query: String) = _state.update { state ->
        state.copy(query = query)
    }
}
