package bagus2x.sosmed.domain.repository

import androidx.paging.PagingData
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Media
import kotlinx.coroutines.flow.Flow

interface FeedRepository {

    suspend fun save(description: String, medias: List<Media>, language: String?): Feed

    fun getFeeds(pageSize: Int): Flow<PagingData<Feed>>

    fun getFeeds(authorId: Long, pageSize: Int): Flow<PagingData<Feed>>

    fun searchFeeds(query: String, pageSize: Int): Flow<PagingData<Feed>>

    fun getFeed(feedId: Long): Flow<Feed?>

    suspend fun favorite(feedId: Long)

    suspend fun unfavorite(feedId: Long)
}
