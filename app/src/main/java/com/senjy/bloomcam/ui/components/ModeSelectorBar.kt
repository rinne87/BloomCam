package com.senjy.bloomcam.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.senjy.bloomcam.data.CameraMode
import com.senjy.bloomcam.ui.theme.BloomGold
import com.senjy.bloomcam.ui.theme.BloomPink
import com.senjy.bloomcam.ui.theme.DarkGlass
import com.senjy.bloomcam.ui.theme.White

@Composable
fun ModeSelectorBar(
    currentMode: CameraMode,
    onModeSelected: (CameraMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(DarkGlass)
                .padding(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CameraMode.values().forEach { mode ->
                    val isSelected = mode == currentMode
                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            when (mode) {
                                CameraMode.PORTRAIT -> BloomPink
                                CameraMode.OBJECT -> Color(0xFF00E676)
                                CameraMode.FIGURE_GUNPLA -> Color(0xFF00E5FF)
                            }
                        } else Color.Transparent,
                        animationSpec = tween(250),
                        label = "modeBg"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color.Black else White.copy(alpha = 0.8f),
                        animationSpec = tween(250),
                        label = "modeText"
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(bgColor)
                            .clickable { onModeSelected(mode) }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}
