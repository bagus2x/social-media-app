package bagus2x.sosmed.presentation.imageeditor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bagus2x.sosmed.R
import bagus2x.sosmed.presentation.common.media.DeviceMedia
import bagus2x.sosmed.presentation.common.media.ImageFilter
import bagus2x.sosmed.presentation.common.components.Image

val ImageFilters by lazy {
    listOf(
        R.string.text_normal to ImageFilter.Normal,
        R.string.text_black to ImageFilter.Black
    )
}

@Composable
fun FilterMenu(
    modifier: Modifier = Modifier,
    selected: DeviceMedia.Image,
    onChange: (ImageFilter) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for ((textRes, filter) in ImageFilters) {
            Column(
                modifier = Modifier.width(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (selected.filter == filter) {
                    Image(
                        model = selected.file,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.Gray)
                            .clickable { onChange(filter) },
                        contentDescription = null,
                        colorFilter = filter.colorFilter,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        model = selected.file,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.Gray)
                            .clickable { onChange(filter) },
                        contentDescription = null,
                        colorFilter = filter.colorFilter,
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    text = stringResource(textRes),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
