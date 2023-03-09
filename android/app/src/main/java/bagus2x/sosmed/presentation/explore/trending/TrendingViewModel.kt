package bagus2x.sosmed.presentation.explore.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bagus2x.sosmed.domain.usecase.GetTrendingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

@HiltViewModel
class TrendingViewModel @Inject constructor(
    getTrendingUseCase: GetTrendingUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TrendingState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getTrendingUseCase(24.hours)
                .catch { e ->
                    _state.update { state ->
                        state.copy(snackbar = e.message ?: "Failed to load trending")
                    }
                    Timber.e(e)
                }
                .collect { trending ->
                    _state.update { state -> state.copy(trending = trending) }
                }
        }
    }
}
