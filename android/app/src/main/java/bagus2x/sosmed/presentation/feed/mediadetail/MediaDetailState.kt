package bagus2x.sosmed.presentation.feed.mediadetail

import bagus2x.sosmed.domain.model.Feed

data class MediaDetailState(
    val feed: Feed? = null,
    val initialPage: Int = 0
)
