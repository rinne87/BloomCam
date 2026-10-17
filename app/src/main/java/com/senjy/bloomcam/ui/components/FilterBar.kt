package com.senjy.bloomcam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senjy.bloomcam.data.CameraMode
import com.senjy.bloomcam.data.FilterType
import com.senjy.bloomcam.ui.theme.BloomGold
import com.senjy.bloomcam.ui.theme.BloomPink
import com.senjy.bloomcam.ui.theme.DarkGlass
import com.senjy.bloomcam.ui.theme.TextPrimary
import com.senjy.bloomcam.ui.theme.TextSecondary
import com.senjy.bloomcam.ui.theme.White

@Composable
fun FilterBar(
    filters: List<FilterType>,
    selectedFilter: FilterType,
    intensity: Float,
    cameraMode: CameraMode,
    onFilterSelected: (FilterType) -> Unit,
    onIntensityChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(DarkGlass)
            .padding(top = 10.dp, bottom = 6.dp)
    ) {
        // Intensity Slider (visible if not original)
        if (selectedFilter != FilterType.ORIGINAL) {
            val sliderLabel = when (cameraMode) {
                CameraMode.PORTRAIT -> "뽀샤시 강도"
                CameraMode.OBJECT -> "컬러/선명도"
                CameraMode.FIGURE_GUNPLA -> "먹선/무광 강도"
            }
            val accentColor = when (cameraMode) {
                CameraMode.PORTRAIT -> BloomPink
                CameraMode.OBJECT -> Color(0xFF00E676)
                CameraMode.FIGURE_GUNPLA -> Color(0xFF00E5FF)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sliderLabel,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = intensity,
                    onValueChange = onIntensityChanged,
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${(intensity * 100).toInt()}%",
                    color = accentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(36.dp)
                )
            }
        }

        // Horizontal list of filters tailored to active CameraMode
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filters, key = { it.id }) { filter ->
                val isSelected = filter == selectedFilter
                val accentColor = when (cameraMode) {
                    CameraMode.PORTRAIT -> BloomPink
                    CameraMode.OBJECT -> Color(0xFF00E676)
                    CameraMode.FIGURE_GUNPLA -> Color(0xFF00E5FF)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onFilterSelected(filter) }
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(filter.previewColor.copy(alpha = 0.85f))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) White else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (filter == FilterType.ORIGINAL) {
                            Text(
                                text = "RAW",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(White.copy(alpha = if (isSelected) 1.0f else 0.5f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = filter.displayName,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) BloomGold else TextPrimary
                    )
                }
            }
        }
    }
}
