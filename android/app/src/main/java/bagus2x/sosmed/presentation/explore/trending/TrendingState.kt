package bagus2x.sosmed.presentation.explore.trending

import bagus2x.sosmed.domain.model.Trending

data class TrendingState(
    val duration: String = "24h",
    val snackbar: String = "",
    val trending: List<Trending> = emptyList()
)
