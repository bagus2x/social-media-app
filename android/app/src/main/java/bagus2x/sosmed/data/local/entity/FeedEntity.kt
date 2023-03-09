package bagus2x.sosmed.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import bagus2x.sosmed.data.common.Misc
import bagus2x.sosmed.domain.model.Feed

@Entity(tableName = "feed")
data class FeedEntity(
    @ColumnInfo("id")
    @PrimaryKey
    val id: Long,
    @Embedded(prefix = "author_")
    val author: ProfileEntity,
    @ColumnInfo("description")
    val description: String,
    @ColumnInfo("medias")
    val medias: List<MediaEntity>,
    @ColumnInfo("favorite")
    val favorite: Boolean,
    @ColumnInfo("total_favorites")
    val totalFavorites: Int,
    @ColumnInfo("total_comments")
    val totalComments: Int,
    @ColumnInfo("total_reposts")
    val totalReposts: Int,
    @ColumnInfo("language")
    val language: String?,
    @ColumnInfo("created_at")
    val createdAt: Long,
    @ColumnInfo("updated_at")
    val updatedAt: Long
) {

    fun asDomainModel(): Feed {
        return Feed(
            id = id,
            author = author.asDomainModel(),
            medias = medias.map { it.asDomainModel() },
            description = description,
            favorite = favorite,
            totalFavorites = totalFavorites,
            totalComments = totalComments,
            totalReposts = totalReposts,
            language = language,
            updatedAt = Misc.epochMillisToLocalDate(updatedAt),
            createdAt = Misc.epochMillisToLocalDate(createdAt)
        )
    }
}


