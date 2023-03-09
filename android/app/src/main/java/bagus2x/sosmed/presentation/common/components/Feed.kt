package bagus2x.sosmed.presentation.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.home.components.Medias

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Feed(
    feed: Feed,
    onImageClicked: (Media.Image) -> Unit,
    onVideoClicked: (Media.Video) -> Unit,
    onFavoriteClicked: () -> Unit,
    onCommentClicked: () -> Unit,
    onRepostClicked: () -> Unit,
    onSendClicked: () -> Unit,
    onFeedClicked: () -> Unit,
    onUrlClicked: (String) -> Unit,
    onHashtagClicked: (String) -> Unit,
    onMentionClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
   Surface(onClick = onFeedClicked) {
       Column(modifier = modifier) {
           Spacer(modifier = Modifier.height(12.dp))
           val author = feed.author
           AuthorFeed(
               authorName = author.username,
               authorPhoto = author.photo ?: Misc.getAvatar(author.username),
               timestamp = Misc.formatRelative(feed.createdAt),
               modifier = Modifier
                   .padding(start = 16.dp, end = 4.dp)
                   .fillMaxWidth(),
               onOptionsClicked = {}
           )
           Spacer(modifier = Modifier.height(12.dp))
           TextFormatter(
               text = feed.description,
               modifier = Modifier.padding(horizontal = 16.dp),
               style = MaterialTheme.typography.body2,
               maxLines = 4,
               onClick = {
                   detectClickUrl(onUrlClicked)
                   detectClickMention(onMentionClicked)
                   detectClickHashtag(onHashtagClicked)
                   detectClickText { onFeedClicked() }
               },
               overflow = TextOverflow.Ellipsis
           )
           val medias = feed.medias
           if (medias.isNotEmpty()) {
               Spacer(modifier = Modifier.height(12.dp))
               Medias(
                   medias = medias,
                   onImageClicked = onImageClicked,
                   onVideoClicked = onVideoClicked,
                   modifier = Modifier
                       .fillMaxWidth()
                       .aspectRatio(1f)
               )
           }
           FeedActionButtons(
               favorite = feed.favorite,
               totalFavorites = feed.totalFavorites,
               onFavoriteClicked = onFavoriteClicked,
               totalComments = feed.totalComments,
               onCommentClicked = onCommentClicked,
               totalReposts = feed.totalReposts,
               onRepostClicked = onRepostClicked,
               onSendClicked = onSendClicked,
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(horizontal = 4.dp),
           )
       }
   }
}

@Composable
private fun AuthorFeed(
    authorName: String,
    authorPhoto: String?,
    timestamp: String,
    modifier: Modifier = Modifier,
    onOptionsClicked: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            model = authorPhoto,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = authorName,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timestamp,
                style = MaterialTheme.typography.caption,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onOptionsClicked) {
            Icon(
                painter = painterResource(R.drawable.ic_more_outlined),
                contentDescription = null,
                modifier = Modifier.rotate(90f)
            )
        }
    }
}
