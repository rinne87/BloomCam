package com.senjy.bloomcam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BloomPink,
    secondary = BloomPeach,
    tertiary = BloomGold,
    background = DarkBackground,
    surface = DarkCard,
    onPrimary = White,
    onSecondary = White,
    onTertiary = PureBlack,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun BloomCamTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
