package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ElegantColorScheme = darkColorScheme(
    primary = ElegantAccentPrimary,
    onPrimary = ElegantAccentOnPrimary,
    primaryContainer = ElegantAccentPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = ElegantAccentPrimary,
    secondary = ElegantAccentSecondary,
    onSecondary = ElegantAccentOnSecondary,
    secondaryContainer = ElegantBackground,
    background = ElegantBackground,
    surface = ElegantSurface,
    surfaceVariant = ElegantSurfaceVariant,
    onBackground = ElegantTextPrimary,
    onSurface = ElegantTextPrimary,
    onSurfaceVariant = ElegantTextSecondary,
    outline = ElegantBorderLine,
    outlineVariant = ElegantBorderLine
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // We enforce the beautiful "Elegant Dark" theme
    dynamicColor: Boolean = false, // Disable to respect user design guidelines perfectly
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ElegantColorScheme,
        typography = Typography,
        content = content
    )
}
