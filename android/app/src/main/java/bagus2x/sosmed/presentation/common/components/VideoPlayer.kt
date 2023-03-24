package bagus2x.sosmed.presentation.common.components

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.Misc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
@androidx.annotation.OptIn(UnstableApi::class)
fun VideoPlayer(
    modifier: Modifier = Modifier,
    state: VideoPlayerState,
    onControllerVisibilityChanged: ((Boolean) -> Unit)? = null,
    useController: Boolean = false,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT
) {

    val context = LocalContext.current
    val playerView = remember {
        PlayerView(context).apply {
            this.useController = useController
            this.player = state
            this.resizeMode = resizeMode
            this.id = View.generateViewId()
            onControllerVisibilityChanged?.let {
                setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
                    it(visibility == View.VISIBLE)
                })
            }
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
    val aspectRationLayout = remember {
        AspectRatioFrameLayout(context).apply {
            addView(playerView)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    AndroidView(
        factory = { aspectRationLayout },
        modifier = modifier
    )
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    thumbnail: String,
    playing: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    state: VideoPlayerState,
    useController: Boolean = false,
    controllerColor: Color = Color.White,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
) {
    Box(modifier = modifier) {
        if (playing) {
            VideoPlayer(
                state = state,
                resizeMode = resizeMode,
                modifier = Modifier.fillMaxSize()
            )
            if (useController) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(top = 16.dp, bottom = 4.dp),
                ) {
                    Slider(
                        value = state.currentProgress,
                        onValueChange = state::seekTo,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .offset(y = 24.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = controllerColor,
                            activeTrackColor = controllerColor
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = {
                                if (state.isPlaying) {
                                    state.pause()
                                } else {
                                    state.play()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(if (state.isPlaying) R.drawable.ic_pause_filled else R.drawable.ic_play_filled),
                                contentDescription = null,
                                tint = controllerColor.copy(alpha = ContentAlpha.medium)
                            )
                        }
                        IconButton(
                            onClick = {
                                if (state.isMuted) {
                                    state.setVolume(1f)
                                } else {
                                    state.setVolume(0f)
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(if (state.isMuted) R.drawable.ic_audio_off_outlined else R.drawable.ic_audio_on_outlined),
                                contentDescription = null,
                                tint = controllerColor.copy(alpha = ContentAlpha.medium)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = state.formatCurrentProgress,
                            style = MaterialTheme.typography.body2,
                            color = controllerColor.copy(alpha = ContentAlpha.medium),
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        } else {
            Image(
                model = thumbnail,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private val VideoPlayerState.formatCurrentProgress: String
    get() = "${Misc.formatDuration(currentPosition)} / ${Misc.formatDuration(duration)}"

class VideoPlayerState(
    private val exoPlayer: ExoPlayer,
    private val scope: CoroutineScope,
    private val updatePlaybackStateInterval: Duration
) : ExoPlayer by exoPlayer, Player.Listener {
    @get:JvmName("isPlayingX")
    var isPlaying by mutableStateOf(false)
        private set
    var duration by mutableStateOf(0.seconds)
        private set
    var currentPosition by mutableStateOf(0.seconds)
        private set
    val currentProgress by derivedStateOf {
        val progress = (currentPosition / duration).toFloat()
        if (progress.isNaN() || progress.isInfinite()) 0f
        else progress
    }

    @get:JvmName("getVolumeX")
    @set:JvmName("setVolumeX")
    var volume by mutableStateOf(getVolume())
        private set
    val isMuted by derivedStateOf { volume == 0f }

    @get:JvmName("isLoadingX")
    var isLoading by mutableStateOf(false)
        private set
    var isBuffering by mutableStateOf(false)
        private set
    var bufferedPercentage by mutableStateOf(0f)
        private set
    private var playbackStateJob: Job? = null

    init {
        addListener(this)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)

        this.isPlaying = isPlaying
        this.duration = getDuration().milliseconds
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)

        this.isLoading = isLoading
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)

        isBuffering = playbackState == Player.STATE_BUFFERING

        playbackStateJob?.cancel()
        playbackStateJob = scope.launch {
            while (true) {
                this@VideoPlayerState.currentPosition = getCurrentPosition().milliseconds
                this@VideoPlayerState.bufferedPercentage = getBufferedPercentage().toFloat()
                delay(updatePlaybackStateInterval)
            }
        }
    }

    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume
        this.volume = volume
    }

    fun seekTo(progress: Float) {
        seekTo((progress * duration.inWholeMilliseconds).roundToLong())
    }
}

@Composable
fun rememberVideoPlayerState(
    lifecycleAware: Boolean = true,
    updatePlaybackStateInterval: Duration = 300.milliseconds
): VideoPlayerState {
    val context = LocalContext.current
    val exoPlayer = ExoPlayer.Builder(context).build()
    val scope = rememberCoroutineScope()
    val videoPlayerState = remember {
        VideoPlayerState(exoPlayer, scope, updatePlaybackStateInterval)
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    if (lifecycleAware) {
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> videoPlayerState.play()
                    Lifecycle.Event.ON_PAUSE -> videoPlayerState.pause()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                videoPlayerState.release()
            }
        }
    }

    return videoPlayerState
}
