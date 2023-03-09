package bagus2x.sosmed.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.usecase.FavoriteFeedUseCase
import bagus2x.sosmed.domain.usecase.GetFeedsUseCase
import bagus2x.sosmed.domain.usecase.GetStoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    getStoriesUseCase: GetStoriesUseCase,
    getFeedsUseCase: GetFeedsUseCase,
    private val favoriteFeedUseCase: FavoriteFeedUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()
    val stories = getStoriesUseCase()
        .catch { e ->
            _state.update { state -> state.copy(snackbar = e.message ?: "") }
            Timber.e(e)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds),
            initialValue = emptyList()
        )
    val feeds = getFeedsUseCase(pageSize = 20)
        .catch { e ->
            _state.update { state -> state.copy(snackbar = e.message ?: "") }
            Timber.e(e)
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PagingData.empty()
        )

    fun consumeSnackbar() = _state.update { state ->
        state.copy(snackbar = "")
    }

    fun favoriteFeed(feed: Feed) {
        viewModelScope.launch {
            _state.update { state -> state.copy(loading = true) }
            try {
                favoriteFeedUseCase(feedId = feed.id)
            } catch (e: Exception) {
                _state.update { state -> state.copy(snackbar = e.message ?: "") }
                Timber.e(e)
            }
            _state.update { state -> state.copy(loading = false) }
        }
    }
}
