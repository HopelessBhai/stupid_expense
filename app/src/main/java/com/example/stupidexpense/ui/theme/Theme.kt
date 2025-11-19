package com.example.stupidexpense.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palette chosen to keep the widget-like surface calm yet legible in dark mode.
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF80CBC4),
    onPrimary = Color(0xFF00332C),
    secondary = Color(0xFFB39DDB),
    onSecondary = Color(0xFF120033),
    background = Color(0xFF101417),
    onBackground = Color(0xFFE2E6EA),
    surface = Color(0xFF1A1F23),
    onSurface = Color(0xFFE2E6EA)
)

private val AppTypography = Typography()

/** Apply the single dark theme requested for the entire app. */
@Composable
fun StupidExpenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
