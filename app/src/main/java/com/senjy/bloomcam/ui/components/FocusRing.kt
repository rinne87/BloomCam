package com.senjy.bloomcam.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.senjy.bloomcam.ui.theme.BloomGold

@Composable
fun FocusRing(
    x: Float,
    y: Float,
    onAnimationEnd: () -> Unit
) {
    val scale = remember { Animatable(1.5f) }
    val alpha = remember { Animatable(1.0f) }

    LaunchedEffect(x, y) {
        scale.animateTo(1.0f, animationSpec = tween(250))
        alpha.animateTo(0.0f, animationSpec = tween(400))
        onAnimationEnd()
    }

    val sizeDp = 70.dp
    val halfSizePx = 35

    Box(
        modifier = Modifier
            .offset { IntOffset((x - halfSizePx).toInt(), (y - halfSizePx).toInt()) }
            .size(sizeDp)
            .scale(scale.value)
            .alpha(alpha.value)
            .border(2.dp, BloomGold, CircleShape)
    )
}
