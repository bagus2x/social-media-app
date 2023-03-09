package bagus2x.sosmed.presentation.feed.feeddetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.domain.model.Feed
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.presentation.common.components.FeedActionButtons
import bagus2x.sosmed.presentation.common.components.TextFormatter
import bagus2x.sosmed.presentation.common.components.rememberTranslationState
import bagus2x.sosmed.presentation.home.components.Medias

@Composable
fun FeedDetail(
    feed: Feed,
    onImageClicked: (Media.Image) -> Unit,
    onVideoClicked: (Media.Video) -> Unit,
    onFavoriteClicked: () -> Unit,
    onCommentClicked: () -> Unit,
    onRepostClicked: () -> Unit,
    onSendClicked: () -> Unit,
    onUrlClicked: (String) -> Unit,
    onHashtagClicked: (String) -> Unit,
    onMentionClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val translationState = rememberTranslationState(feed.description, feed.language)
    Column(modifier = modifier) {
        TextFormatter(
            text = translationState.text.value,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.body2,
            onClick = {
                detectClickUrl(onUrlClicked)
                detectClickMention(onMentionClicked)
                detectClickHashtag(onHashtagClicked)
            },
            overflow = TextOverflow.Ellipsis
        )
        if (translationState.isTranslatable) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = translationState.buttonText,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable { translationState.toggle() }
            )
        }
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
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Divider()
    }
}
