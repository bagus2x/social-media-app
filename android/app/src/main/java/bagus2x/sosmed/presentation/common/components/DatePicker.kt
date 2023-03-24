package bagus2x.sosmed.presentation.common.components

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.presentation.common.theme.MedsosTheme
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalTextApi::class, ExperimentalFoundationApi::class)
@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(),
    style: TextStyle = MaterialTheme.typography.body1,
    onChange: (LocalDate) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(text = "99999", style = style)
    val density = LocalDensity.current
    val pageSize = with(density) {
        DpSize(
            width = textLayoutResult.size.width.toDp() + 32.dp,
            height = textLayoutResult.size.height.toDp() + 32.dp
        )
    }
    val pagerSize = DpSize(
        width = pageSize.width * 3 + 32.dp,
        height = pageSize.height * 3
    )
    val datesPagerState = rememberPagerState(
        initialPage = state.initialDateIndex
    )
    val monthsPagerState = rememberPagerState(
        initialPage = state.initialMonthIndex
    )
    val yearsPagerState = rememberPagerState(
        initialPage = state.initialYearIndex
    )
    LaunchedEffect(Unit) {
        snapshotFlow { yearsPagerState.currentPage }.collectLatest { index ->
            val year = state.years[index]
            val date = state.dates[datesPagerState.currentPage].withYear(year)
            val dateIndex = state.dates.indexOf(date)
            val monthsIndex = state.months.indexOf(date.month to date.year)
            datesPagerState.scrollToPage(dateIndex)
            monthsPagerState.scrollToPage(monthsIndex)
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { monthsPagerState.currentPage }.collectLatest { index ->
            val (month, year) = state.months[index]
            val yearIndex = state.years.indexOf(year)
            val date = state.dates[datesPagerState.currentPage]
                .withMonth(month.value)
                .withYear(year)
            val dateIndex = state.dates.indexOf(date)
            yearsPagerState.scrollToPage(yearIndex)
            datesPagerState.scrollToPage(dateIndex)
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { datesPagerState.currentPage }.collectLatest { index ->
            val date = state.dates[index].apply(onChange)
            val yearIndex = state.years.indexOf(date.year)
            val monthIndex = state.months.indexOf(date.month to date.year)
            yearsPagerState.scrollToPage(yearIndex)
            monthsPagerState.scrollToPage(monthIndex)
        }
    }
    val indicator = Modifier.indicator(
        color = MaterialTheme.colors.onSurface,
        pageHeight = pageSize.height
    )
    Row(
        modifier = modifier
            .size(pagerSize)
            .fadeTopAndBottom(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        VerticalPager(
            pageCount = state.dates.size,
            contentPadding = PaddingValues(vertical = pageSize.height),
            state = datesPagerState,
            modifier = indicator
        ) { index ->
            val date = state.dates[index]
            Text(
                text = "${date.dayOfMonth}",
                modifier = Modifier
                    .size(pageSize)
                    .wrapContentSize(align = Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
        VerticalPager(
            pageCount = state.months.size,
            contentPadding = PaddingValues(vertical = pageSize.height),
            state = monthsPagerState,
            modifier = indicator
        ) { index ->
            val (month, _) = state.months[index]
            Text(
                text = month.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier
                    .size(pageSize)
                    .wrapContentSize(align = Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
        VerticalPager(
            pageCount = state.years.size,
            contentPadding = PaddingValues(vertical = pageSize.height),
            state = yearsPagerState,
            modifier = indicator
        ) { index ->
            val year = state.years[index]
            Text(
                text = "$year",
                modifier = Modifier
                    .size(pageSize)
                    .wrapContentSize(align = Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Stable
data class DatePickerState(
    val currentDate: LocalDate,
    val startDate: LocalDate,
    val endDate: LocalDate
) {
    val years = (startDate.year..endDate.year).toList()
    val dates = (startDate.toEpochDay()..endDate.toEpochDay()).map(LocalDate::ofEpochDay)
    val months = dates
        .groupBy { it.month to it.year }
        .toList()
        .map { it.first }
        .sortedBy { it.second }
    val initialYearIndex = years.indexOf(currentDate.year)
    val initialMonthIndex = months.indexOf(currentDate.month to currentDate.year)
    val initialDateIndex = dates.indexOf(currentDate)
}

@Composable
fun rememberDatePickerState(
    currentDate: LocalDate = LocalDate.now(),
    startDate: LocalDate = LocalDate.now().minusYears(100),
    endDate: LocalDate = LocalDate.now().plusYears(100)
): DatePickerState {
    return DatePickerState(currentDate, startDate, endDate)
}

private fun Modifier.indicator(
    color: Color,
    strokeWidth: Dp = 2.dp,
    pageHeight: Dp
): Modifier {
    return this.drawBehind {
        drawLine(
            color = color,
            strokeWidth = strokeWidth.toPx(),
            start = Offset(
                x = 0f,
                y = pageHeight.toPx()
            ),
            end = Offset(
                x = size.width,
                y = pageHeight.toPx()
            ),
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            strokeWidth = strokeWidth.toPx(),
            start = Offset(
                x = 0f,
                y = pageHeight.toPx() * 2 - strokeWidth.toPx()
            ),
            end = Offset(
                x = size.width,
                y = pageHeight.toPx() * 2 - strokeWidth.toPx()
            ),
            cap = StrokeCap.Round
        )
    }
}

private fun Modifier.fadeTopAndBottom(): Modifier {
    return this
        .graphicsLayer { alpha = 0.99f }
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black, Color.Black),
                ),
                blendMode = BlendMode.DstIn,
                topLeft = Offset.Zero,
                size = Size(
                    width = size.width,
                    height = size.height / 3
                )
            )
            this.rotate(180f) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black, Color.Black),
                    ),
                    blendMode = BlendMode.DstIn,
                    topLeft = Offset.Zero,
                    size = Size(
                        width = size.width,
                        height = size.height / 3
                    )
                )
            }
        }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DatePickerPreview() {
    MedsosTheme {
        Surface {
            DatePicker(onChange = { })
        }
    }
}
