package bagus2x.sosmed.presentation.story.storydetail

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.domain.model.Story
import bagus2x.sosmed.presentation.common.Misc
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.OutlinedTextField
import bagus2x.sosmed.presentation.story.components.SegmentedProgressIndicator
import bagus2x.sosmed.presentation.story.components.rememberSegmentProgressState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@Composable
fun StoryDetailScreen(
    navController: NavController,
    viewModel: StoryDetailViewModel
) {
    val stories by viewModel.stories.collectAsStateWithLifecycle()
    StoryDetailScreen(
        stories = stories,
        navigateUp = navController::navigateUp
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryDetailScreen(
    stories: List<Story>,
    navigateUp: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    DisposableEffect(isSystemInDarkTheme) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false,
            isNavigationBarContrastEnforced = false
        )
        onDispose {
            if (isSystemInDarkTheme) {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = false,
                    isNavigationBarContrastEnforced = false
                )
            } else {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = true,
                    isNavigationBarContrastEnforced = false
                )
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            val pagerState = rememberPagerState()
            HorizontalPager(
                state = pagerState,
                pageCount = stories.size,
                modifier = Modifier.fillMaxSize()
            ) { storyIndex ->
                val story = stories[storyIndex]
                val scope = rememberCoroutineScope()
                Box {
                    Story(
                        story = story,
                        autoSlide = pagerState.currentPage == storyIndex,
                        onPrevious = {
                            scope.launch {
                                val page = pagerState.currentPage - 1
                                if (page < 0) {
                                    navigateUp()
                                }
                                pagerState.animateScrollToPage(page)
                            }
                        },
                        onCompleted = {
                            scope.launch {
                                val page = pagerState.currentPage + 1
                                if (page > stories.size - 1) {
                                    navigateUp()
                                }
                                pagerState.animateScrollToPage(page)
                            }
                        },
                        onSendClicked = { /*TODO*/ },
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "$storyIndex",
                        style = MaterialTheme.typography.h1,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun Story(
    story: Story,
    autoSlide: Boolean,
    onPrevious: () -> Unit,
    onCompleted: () -> Unit,
    onSendClicked: () -> Unit,
    modifier: Modifier = Modifier,
) = Surface(
    color = Color.Black,
    contentColor = Color.White
) {
    var currentMediaIndex by rememberSaveable { mutableStateOf(0) }
    val progressState = rememberSegmentProgressState(
        segmentsCount = story.medias.size,
        onCompleted = onCompleted,
        onPrevious = onPrevious,
        onSegmentChanged = { segment ->
            currentMediaIndex = segment
        }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(autoSlide) {
        if (autoSlide) {
            progressState.start(currentMediaIndex)
        } else {
            progressState.pause()
        }
    }

    Box(modifier = modifier) {
        val media = story.medias.getOrNull(currentMediaIndex)
        if (media is Media.Image) {
            Image(
                model = media.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .clickable(
                        onLeftSideClicked = { scope.launch { progressState.previous() } },
                        onRightSideClicked = { scope.launch { progressState.next() } },
                        onPressed = { scope.launch { progressState.pause() } },
                        onReleased = { scope.launch { progressState.start() } }
                    ),
                contentScale = ContentScale.FillWidth
            )
        }
        Column(
            modifier = Modifier
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(listOf(Color.Black, Color.Transparent))
                    )
                }
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SegmentedProgressIndicator(
                progress = progressState.value,
                numberOfSegments = story.medias.size,
                color = Color.White,
                width = 2.dp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    model = story.author.photo ?: Misc.getAvatar(story.author.username),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = story.author.username,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = Misc.formatRelative(story.createdAt),
                    style = MaterialTheme.typography.caption,
                )
            }
        }
        Row(
            modifier = Modifier
                .imePadding()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black))
                    )
                }
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomStart),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                minHeight = 40.dp,
                shape = MaterialTheme.shapes.medium
            )
            val isImeVisible = WindowInsets.isImeVisible
            LaunchedEffect(isImeVisible) {
                if (isImeVisible) {
                    progressState.pause()
                } else {
                    progressState.start()
                }
            }
            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_heart_outlined),
                        contentDescription = null
                    )
                }
                val rotate by animateFloatAsState(if (isImeVisible) 45f else 0f)
                IconButton(onClick = onSendClicked) {
                    Icon(
                        painter = painterResource(R.drawable.ic_send_outlined),
                        contentDescription = null,
                        modifier = Modifier.graphicsLayer { rotationZ = rotate }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
fun Modifier.clickable(
    onLeftSideClicked: () -> Unit,
    onRightSideClicked: () -> Unit,
    onPressed: () -> Unit,
    onReleased: () -> Unit
): Modifier {
    return pointerInput(Unit) {
        val widthPx = size.width
        detectTapGestures(
            onPress = { offset ->
                onPressed()
                val released = try {
                    measureTime { tryAwaitRelease() }
                } catch (e: Exception) {
                    Timber.e(e)
                    Duration.ZERO
                }
                if (released < 100.milliseconds) {
                    val isLeftSide = offset.x < widthPx / 2f
                    if (isLeftSide) {
                        onLeftSideClicked()
                    } else {
                        onRightSideClicked()
                    }
                }
                onReleased()
            }
        )
    }
}
