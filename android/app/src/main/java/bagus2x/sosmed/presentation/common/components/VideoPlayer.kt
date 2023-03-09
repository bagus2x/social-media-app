package bagus2x.sosmed.presentation.common.components

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import bagus2x.sosmed.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private val DurationShape = RoundedCornerShape(4.dp)

@Composable
@androidx.annotation.OptIn(UnstableApi::class)
fun VideoPlayer(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    onControllerVisibilityChanged: ((Boolean) -> Unit)? = null,
    useController: Boolean = false,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FILL
) {
    val context = LocalContext.current
    val playerView = remember {
        PlayerView(context).apply {
            this.useController = useController
            this.player = player
            this.resizeMode = resizeMode
            this.id = View.generateViewId()
            onControllerVisibilityChanged?.let {
                setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
                    it(visibility == View.VISIBLE)
                })
            }
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

    }

    AndroidView(
        factory = { playerView }, modifier = modifier
    )
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun VideoPostPlayer(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    thumbnail: String,
) {
    Box(modifier = modifier) {
        var isPlaying by remember { mutableStateOf(false) }
        var currentTimeRemaining by remember { mutableStateOf(0.milliseconds) }
        var shouldShowControls by remember { mutableStateOf(true) }
        var currentVolume by remember { mutableStateOf(1f) }
        val scope = rememberCoroutineScope()
        DisposableEffect(Unit) {
            val listener = object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    isPlaying = player.isPlaying
                    currentVolume = player.volume
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    scope.launch {
                        while (true) {
                            currentTimeRemaining =
                                (player.duration - player.currentPosition).coerceAtLeast(0L).milliseconds
                            delay(1000)
                        }
                    }
                }
            }
            player.addListener(listener)
            onDispose {
                player.removeListener(listener)
            }
        }
        LaunchedEffect(shouldShowControls, isPlaying) {
            if (shouldShowControls && isPlaying) {
                repeat(3) {
                    delay(1000)
                    shouldShowControls = false
                }
            }
        }

        VideoPlayer(player = player,
            modifier = Modifier
                .fillMaxSize()
                .clickable { shouldShowControls = !shouldShowControls })
        // Thumbnails
        AnimatedVisibility(
            visible = !isPlaying, enter = fadeIn(), exit = fadeOut()
        ) {
            Image(
                model = thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { shouldShowControls = !shouldShowControls },
                contentScale = ContentScale.Crop
            )
        }
        // Controllers
        AnimatedVisibility(
            visible = shouldShowControls,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .clickable {
                        if (isPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    },
                shape = CircleShape,
                color = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                border = BorderStroke(2.dp, Color.White)
            ) {
                Icon(
                    painter = if (!isPlaying) painterResource(R.drawable.ic_play_filled)
                    else painterResource(R.drawable.ic_pause_filled),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                )
            }
        }
        AnimatedVisibility(visible = shouldShowControls,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            enter = slideInVertically(initialOffsetY = { it * 2 }),
            exit = slideOutVertically(targetOffsetY = { it * 2 })) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Surface(
                    color = Color.Black.copy(alpha = .5f),
                    contentColor = Color.White,
                    shape = DurationShape,
                    modifier = Modifier.animateContentSize()
                ) {
                    Text(
                        text = currentTimeRemaining.formatted,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(3.dp)
                    )
                }
                Surface(color = Color.Black.copy(alpha = .5f),
                    contentColor = Color.White,
                    shape = DurationShape,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { player.volume = if (currentVolume == 0f) 1f else 0f }) {
                    Icon(
                        painter = if (currentVolume == 0f) painterResource(R.drawable.ic_audio_off_outlined)
                        else painterResource(R.drawable.ic_audio_on_outlined),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(3.dp)
                            .scale(.8f)
                    )
                }
            }
        }
    }
}

private val Duration.formatted: String
    get() = toComponents { minutes, seconds, _ ->
        String.format("%d:%02d", minutes, seconds)
    }
