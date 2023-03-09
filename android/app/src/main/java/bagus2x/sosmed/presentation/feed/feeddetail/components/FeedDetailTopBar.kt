package bagus2x.sosmed.presentation.feed.feeddetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image

@Composable
fun FeedDetailTopBar(
    feed: Feed,
    onBackClicked: () -> Unit,
    onMoreVertClicked: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left_outlined),
                    contentDescription = null
                )
            }
        },
        title = {
            Image(
                model = feed.author.photo ?: Misc.getAvatar(feed.author.username),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = feed.author.username,
                    style = MaterialTheme.typography.h6,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = Misc.formatRelative(feed.createdAt),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            IconButton(onClick = onMoreVertClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_more_outlined),
                    contentDescription = null,
                    modifier = Modifier.rotate(90f)
                )
            }
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 2.dp
    )
}
