package bagus2x.sosmed.presentation.common.components

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
@androidx.annotation.OptIn(UnstableApi::class)
fun VideoPlayer(
    modifier: Modifier = Modifier,
    player: ExoPlayer,
    onControllerVisibilityChanged: ((Boolean) -> Unit)? = null,
    useController: Boolean = false,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT
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
    player: ExoPlayer,
    onControllerVisibilityChanged: ((Boolean) -> Unit)? = null,
    useController: Boolean = false,
    resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT
) {
    Box(modifier = modifier) {
        if (playing) {
            VideoPlayer(
                player = player,
                onControllerVisibilityChanged = onControllerVisibilityChanged,
                useController = useController,
                resizeMode = resizeMode,
                modifier = Modifier.fillMaxSize()
            )
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

@Suppress("Unused")
private val Duration.formatted: String
    get() = toComponents { minutes, seconds, _ ->
        String.format("%d:%02d", minutes, seconds)
    }

class ExoPlayerState(
    context: Context,
    private val scope: CoroutineScope
) : ExoPlayer by ExoPlayer.Builder(context).build(), Player.Listener {
    @get:JvmName("playing")
    var isPlaying by mutableStateOf(false)
        private set
    var duration by mutableStateOf(0.seconds)
        private set
    var currentPosition by mutableStateOf(0.seconds)
        private set

    init {
        addListener(this)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)

        this.isPlaying = isPlaying
        this.duration = getDuration().milliseconds

        scope.launch {
            while (true) {
                this@ExoPlayerState.currentPosition = getCurrentPosition().milliseconds
                delay(200)
            }
        }
    }
}

@Composable
fun rememberExoPlayerState(
    lifecycleAware: Boolean = true
): ExoPlayerState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exoPlayerState = remember { ExoPlayerState(context, scope) }
    val lifecycleOwner = LocalLifecycleOwner.current

    if (lifecycleAware) {
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> exoPlayerState.play()
                    Lifecycle.Event.ON_PAUSE -> exoPlayerState.pause()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                exoPlayerState.release()
            }
        }
    }

    return exoPlayerState
}
