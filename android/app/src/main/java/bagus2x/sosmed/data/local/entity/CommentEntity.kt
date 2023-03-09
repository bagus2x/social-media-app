package bagus2x.sosmed.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import bagus2x.sosmed.domain.model.Comment
import bagus2x.sosmed.domain.model.Profile
import java.time.Instant
import java.time.ZoneId

@Entity("comment")
data class CommentEntity(
    @PrimaryKey
    @ColumnInfo("id")
    val id: Long,
    @ColumnInfo("feed_id")
    val feedId: Long,
    @ColumnInfo("parent_id")
    val parentId: Long?,
    @ColumnInfo("path")
    val path: String,
    @Embedded("author_")
    val author: ProfileEntity,
    @ColumnInfo("description")
    val description: String,
    @ColumnInfo("medias")
    val medias: List<MediaEntity>,
    @ColumnInfo("pinned")
    val pinned: Boolean = false,
    @ColumnInfo("favorite")
    val favorite: Boolean = false,
    @ColumnInfo("total_favorites")
    val totalFavorites: Int,
    @ColumnInfo("total_replies")
    val totalReplies: Int,
    @ColumnInfo("created_at")
    val createdAt: Long
)

fun CommentEntity.asDomainModel(totalLoadedReplies: Int): Comment {
    return Comment(
        id = id,
        feedId = feedId,
        parentId = parentId,
        path = path,
        author = Profile(
            id = author.id,
            photo = author.photo,
            username = author.username,
            name = author.name
        ),
        description = description,
        medias = medias.map { it.asDomainModel() },
        pinned = pinned,
        favorite = favorite,
        totalFavorites = totalFavorites,
        totalReplies = totalReplies,
        totalLoadedReplies = totalLoadedReplies,
        createdAt = Instant
            .ofEpochMilli(createdAt)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime(),
    )
}
