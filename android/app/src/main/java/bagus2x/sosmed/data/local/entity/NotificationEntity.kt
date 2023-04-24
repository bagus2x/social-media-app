package bagus2x.sosmed.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import bagus2x.sosmed.domain.model.Notification
import bagus2x.sosmed.domain.model.Profile
import java.time.Instant
import java.time.ZoneId

@Entity(tableName = "notification")
data class NotificationEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("id")
    val id: Long,

    @Embedded("issuer_")
    val issuer: ProfileEntity?,

    @ColumnInfo("icon")
    val icon: String?,

    @ColumnInfo("description")
    val description: String,

    @ColumnInfo("medias")
    val medias: List<MediaEntity>,

    @ColumnInfo("dataId")
    val dataId: Long?,

    @ColumnInfo("type")
    val type: String,

    @ColumnInfo("seen")
    val seen: Boolean,

    @ColumnInfo("created_at")
    val createdAt: Long,
) {

    fun asDomainModel(): Notification {
        return Notification(
            id = id,
            icon = icon,
            description = description,
            medias = medias.map(MediaEntity::asDomainModel),
            type = when (type) {
                "feed_liked" -> Notification.Type.FeedLiked(
                    issuer = Profile(
                        id = issuer!!.id,
                        photo = issuer.photo,
                        username = issuer.username,
                        name = issuer.name
                    ),
                    feedId = dataId!!
                )
                "feed_commented" -> Notification.Type.FeedCommented(
                    issuer = Profile(
                        id = issuer!!.id,
                        photo = issuer.photo,
                        username = issuer.username,
                        name = issuer.name
                    ),
                    commentId = dataId!!
                )
                "comment_replied" -> Notification.Type.CommentReplied(
                    issuer = Profile(
                        id = issuer!!.id,
                        photo = issuer.photo,
                        username = issuer.username,
                        name = issuer.name
                    ),
                    commentId = dataId!!
                )
                "user_following" -> Notification.Type.UserFollowing(
                    issuer = Profile(
                        id = issuer!!.id,
                        photo = issuer.photo,
                        username = issuer.username,
                        name = issuer.name
                    )
                )
                else -> Notification.Type.Other
            },
            createdAt = Instant
                .ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        )
    }
}
