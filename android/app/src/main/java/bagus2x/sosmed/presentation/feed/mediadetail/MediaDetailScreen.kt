package bagus2x.sosmed.presentation.feed.mediadetail

import androidx.compose.animation.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import bagus2x.sosmed.R
import bagus2x.sosmed.domain.model.Media
import bagus2x.sosmed.presentation.common.components.Image
import bagus2x.sosmed.presentation.common.components.VideoPlayer
import bagus2x.sosmed.presentation.common.components.dominantColor
import bagus2x.sosmed.presentation.common.components.rememberExoPlayerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun MediaDetailScreen(
    navController: NavController,
    viewModel: MediaDetailViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MediaDetailScreen(
        stateProvider = { state },
        navigateUp = navController::navigateUp
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaDetailScreen(
    stateProvider: () -> MediaDetailState,
    navigateUp: () -> Unit
) {
    val state = stateProvider()
    val defaultColor = MaterialTheme.colors.background
    val colorBackground = remember { Animatable(defaultColor) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(colorBackground.value)
            }
    ) {
        if (state.feed != null) {
            val scope = rememberCoroutineScope()
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val urls = remember {
                state.feed.medias.map { media ->
                    when (media) {
                        is Media.Image -> media.imageUrl
                        is Media.Video -> media.thumbnailUrl
                    }
                }
            }
            val colors by dominantColor(urls, defaultColor)
            val pagerState = rememberPagerState(initialPage = state.initialPage)
            var contentColor by remember { mutableStateOf(Color.White) }
            val systemUiController = rememberSystemUiController()

            DisposableEffect(isSystemInDarkTheme) {
                val colorFlow = snapshotFlow {
                    colors.getOrNull(pagerState.currentPage)
                }.filterNotNull()
                scope.launch {
                    colorFlow.collect { color ->
                        colorBackground.animateTo(color)
                        val isBackgroundLight = color.luminance() > .4f
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = isBackgroundLight,
                            isNavigationBarContrastEnforced = false
                        )
                        contentColor = if (isBackgroundLight) Color.Black else Color.White
                    }
                }
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
            val exoPlayer = rememberExoPlayerState()
            LaunchedEffect(Unit) {
                snapshotFlow { pagerState.currentPage }
                    .map { state.feed.medias[it] }
                    .collectLatest { media ->
                        if (media is Media.Video) {
                            exoPlayer.setMediaItem(MediaItem.fromUri(media.videoUrl))
                            exoPlayer.prepare()
                            exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                            exoPlayer.playWhenReady = true
                        } else {
                            exoPlayer.pause()
                        }
                    }
            }
            HorizontalPager(
                pageCount = state.feed.medias.size,
                state = pagerState
            ) { index ->
                val media = state.feed.medias[index]
                Column(
                    modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
                ) {
                    if (media is Media.Image) {
                        Image(
                            model = media.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                    if (media is Media.Video) {
                        Box {
                            VideoPlayer(
                                thumbnail = media.thumbnailUrl,
                                playing = pagerState.currentPage == index,
                                player = exoPlayer,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "${exoPlayer.currentPosition.inWholeSeconds} / ${exoPlayer.duration.inWholeSeconds}",
                                modifier = Modifier
                                    .background(Color.Red)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
            TopAppBar(
                backgroundColor = Color.Transparent,
                contentColor = contentColor,
                modifier = Modifier
                    .statusBarsPadding()
                    .align(Alignment.TopStart),
                elevation = 0.dp
            ) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_left_outlined),
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {}, modifier = Modifier.rotate(90f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_outlined),
                        contentDescription = null
                    )
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
