package bagus2x.sosmed.presentation.feed.mediadetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.domain.usecase.GetFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getFeedUseCase: GetFeedUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(MediaDetailState())
    val state = _state.asStateFlow()

    init {
        val feedId = savedStateHandle.get<Long>("feed_id")
        if (feedId != null) {
            viewModelScope.launch {
                getFeedUseCase(feedId)
                    .catch { e ->
                        Timber.e(e)
                    }
                    .filterNotNull()
                    .collect { post ->
                        _state.update { it.copy(feed = post) }
                    }
            }
        }

        val initialPage = savedStateHandle.get<Int>("media_index")
        if (initialPage != null) {
            _state.update { it.copy(initialPage = initialPage) }
        }
    }
}
