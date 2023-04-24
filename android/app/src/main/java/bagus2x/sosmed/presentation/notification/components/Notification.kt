package bagus2x.sosmed.presentation.notification.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.model.Notification
import bagus2x.sosmed.domain.model.Profile
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.TextFormatter
import bagus2x.sosmed.presentation.common.theme.MedsosTheme
import java.time.LocalDateTime

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Notification(
    notification: Notification,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = DefaultContentPadding,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier,
        onClick = onClick ?: { },
        enabled = onClick != null
    ) {
        Row(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                model = notification.iconOrPhoto,
                contentDescription = notification.contentDescription,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.weight(1f)) {
                TextFormatter(
                    text = notification.text,
                    style = MaterialTheme.typography.body2
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (notification.medias.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (media in notification.medias) {
                            Image(
                                model = when (media) {
                                    is Media.Image -> media.imageUrl
                                    is Media.Video -> media.thumbnailUrl
                                },
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(MaterialTheme.shapes.small)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Misc.formatRelative(notification.createdAt),
                    style = MaterialTheme.typography.overline
                )
            }
        }
    }
}

private val Notification.iconOrPhoto: String?
    get() = when (type) {
        is Notification.Type.FeedLiked -> type.issuer.photo
            ?: Misc.getAvatar(type.issuer.username)
        is Notification.Type.FeedCommented -> type.issuer.photo
            ?: Misc.getAvatar(type.issuer.username)
        is Notification.Type.CommentReplied -> type.issuer.photo
            ?: Misc.getAvatar(type.issuer.username)
        is Notification.Type.UserFollowing -> type.issuer.photo
            ?: Misc.getAvatar(type.issuer.username)
        Notification.Type.Other -> null
    }

private val Notification.contentDescription: String?
    get() = when (type) {
        is Notification.Type.FeedLiked -> type.issuer.username
        is Notification.Type.FeedCommented -> type.issuer.username
        is Notification.Type.CommentReplied -> type.issuer.username
        is Notification.Type.UserFollowing -> type.issuer.username
        Notification.Type.Other -> null
    }

private val Notification.text: String
    @Composable
    get() = when (type) {
        is Notification.Type.FeedLiked -> stringResource(
            R.string.text_feed_liked,
            "@${type.issuer.username}",
        )
        is Notification.Type.FeedCommented -> stringResource(
            R.string.text_feed_commented,
            "@${type.issuer.username}",
            description
        )
        is Notification.Type.CommentReplied -> stringResource(
            R.string.text_comment_replied,
            "@${type.issuer.username}",
            description
        )
        is Notification.Type.UserFollowing -> stringResource(
            R.string.text_user_following,
            "@${type.issuer.username}",
        )
        Notification.Type.Other -> description
    }

private val DefaultContentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

@Preview
@Composable
fun NotificationPreview() {
    MedsosTheme {
        Text(text = "@tubagus")
        Notification(
            notification = Notification(
                id = 0,
                icon = Misc.getAvatar("bagus2x"),
                description = LoremIpsum(50).values.joinToString(" "),
                medias = listOf(
                    Media.Image(Misc.getIcon("a")),
                    Media.Image(Misc.getIcon("b"))
                ),
                type = Notification.Type.FeedCommented(
                    issuer = Profile(
                        id = 1,
                        photo = Misc.getAvatar("bagus2x"),
                        username = "bagus2x",
                        name = "Tubagus"
                    ),
                    commentId = 2
                ),
                createdAt = LocalDateTime.now().minusHours(1)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
