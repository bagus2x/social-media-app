package bagus2x.sosmed.presentation.story.components

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.floor
import kotlin.math.roundToInt

private val StrokeWidth = 4.dp
private val SegmentGap = 8.dp

@Composable
fun SegmentedProgressIndicator(
    @FloatRange(from = 0.0, to = 1.0)
    progress: Float,
    numberOfSegments: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = color.copy(alpha = 0.25f),
    width: Dp = StrokeWidth,
    segmentGap: Dp = SegmentGap
) {
    val gap: Float
    val stroke: Float
    with(LocalDensity.current) {
        gap = segmentGap.toPx()
        stroke = width.toPx()
    }
    Canvas(
        modifier
            .progressSemantics(progress)
            .fillMaxWidth()
            .height(width)
            .focusable()
    ) {
        drawSegments(1f, backgroundColor, stroke, numberOfSegments, gap)
        drawSegments(progress, color, stroke, numberOfSegments, gap)
    }
}

private fun DrawScope.drawSegments(
    progress: Float,
    color: Color,
    strokeWidth: Float,
    segments: Int,
    segmentGap: Float,
) {
    val width = size.width
    val start = 0f
    val gaps = (segments - 1) * segmentGap
    val segmentWidth = (width - gaps) / segments
    val barsWidth = segmentWidth * segments
    val end = barsWidth * progress + (progress * segments).toInt() * segmentGap

    repeat(segments) { index ->
        val offset = index * (segmentWidth + segmentGap)
        if (offset < end) {
            val barEnd = (offset + segmentWidth).coerceAtMost(end)
            drawLine(
                color,
                Offset(start + offset, 0f),
                Offset(barEnd, 0f),
                strokeWidth,
                pathEffect = PathEffect.cornerPathEffect(16.dp.toPx()),
                cap = StrokeCap.Round
            )
        }
    }
}

@Stable
class ProgressState(
    private val segmentsCount: Int,
    private val durationInMills: Int = 3000,
    private val onNext: () -> Unit,
    private val onPrevious: () -> Unit
) {
    private val progress = Animatable(0f)
    val value by derivedStateOf { progress.value.coerceIn(0f, 1f) }
    val isFinished by derivedStateOf { value == 1f }
    val isRunning by derivedStateOf { progress.isRunning }

    suspend fun start(segment: Int? = null) {
        if (!progress.isRunning) {
            if (segment != null) {
                progress.snapTo(segment / segmentsCount.toFloat())
            }

            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (segmentsCount - value * segmentsCount).roundToInt() * durationInMills,
                    easing = LinearEasing
                )
            )
        }
    }

    suspend fun pause() {
        progress.stop()
    }

    suspend fun next() {
        val currentSegment = floor(value * segmentsCount)
        val target = (currentSegment / segmentsCount) + (1f / segmentsCount)
        if (target >= 1) onNext()
        progress.snapTo(target.coerceIn(0f, 1f))
        start()
    }

    suspend fun previous() {
        val currentSegment = floor(value * segmentsCount)
        val target = ((currentSegment / segmentsCount) - (1f / segmentsCount))
        if (target < 0) onPrevious()
        progress.snapTo(target.coerceIn(0f, 1f))
        start()
    }
}

@Composable
fun rememberSegmentProgressState(
    segmentsCount: Int,
    durationInMills: Int = 3000,
    onCompleted: () -> Unit,
    onPrevious: () -> Unit,
    onSegmentChanged: (Int) -> Unit,
): ProgressState {
    val state = remember { ProgressState(segmentsCount, durationInMills, onCompleted, onPrevious) }

    LaunchedEffect(state) {
        snapshotFlow {
            floor(state.value * segmentsCount).toInt().coerceIn(0, segmentsCount - 1)
        }.collect(onSegmentChanged)
    }

    LaunchedEffect(state) {
        snapshotFlow { state.isFinished }.collect { if (it) onCompleted() }
    }

    return state
}
