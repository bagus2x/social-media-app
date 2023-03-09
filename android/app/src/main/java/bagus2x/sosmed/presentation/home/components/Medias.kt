package bagus2x.sosmed.presentation.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import bagus2x.sosmed.domain.model.Media
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Medias(
    medias: List<Media>,
    onImageClicked: (Media.Image) -> Unit,
    onVideoClicked: (Media.Video) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp)
) {
    if (medias.size == 1) {
        val media = medias.first()
        Media(
            media = media,
            onImageClicked = onImageClicked,
            onVideoClicked = onVideoClicked,
            modifier = modifier
                .padding(contentPadding)
                .clip(MaterialTheme.shapes.small)
        )
        return
    }
    val pagerState = rememberPagerState()
    Box {
        HorizontalPager(
            pageCount = medias.size,
            modifier = modifier,
            state = pagerState,
            contentPadding = contentPadding,
        ) { page ->
            Media(
                media = medias[page],
                onImageClicked = onImageClicked,
                onVideoClicked = onVideoClicked,
                modifier = Modifier
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset =
                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

                        // We animate the scaleX + scaleY, between 85% and 100%
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxSize()
            )
        }
        PagerIndicator(
            scrollInProgress = !pagerState.isScrollInProgress,
            count = medias.size,
            currentIndex = pagerState.currentPage
        )
    }
}

@Composable
fun BoxScope.PagerIndicator(
    scrollInProgress: Boolean,
    count: Int,
    currentIndex: Int,
) {
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(scrollInProgress) {
        if (!scrollInProgress) {
            delay(1000)
            alpha.animateTo(0f)
        } else {
            alpha.animateTo(1f)
        }
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Black.copy(alpha = .5f),
        contentColor = Color.White,
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .align(Alignment.TopEnd)
            .graphicsLayer {
                this.alpha = alpha.value
            }
    ) {
        Text(
            text = "${currentIndex + 1}/$count",
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
    }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomCenter)
            .graphicsLayer {
                this.alpha = alpha.value
            },
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = .5f),
                        shape = CircleShape
                    )
                    .background(
                        if (currentIndex == index) {
                            MaterialTheme.colors.primary
                        } else {
                            Color.Gray.copy(alpha = .5f)
                        }
                    )
            )
        }
    }
}
