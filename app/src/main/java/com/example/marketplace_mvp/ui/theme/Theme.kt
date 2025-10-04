package com.example.marketplace_mvp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.marketplace_mvp.ui.theme.AppTypography

private val DarkColors = darkColorScheme(
    primary = PrimaryColor,
    secondary = PrimaryVariant,
    background = BackgroundColor,
    surface = SurfaceColor,
    error = ErrorColor,
    onPrimary = TextColor,
    onSecondary = TextSecondaryColor,
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
