package com.example.liliplayer.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LiliColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = BgCard,
    primaryContainer = LavenderLight,
    onPrimaryContainer = TextPrimary,
    secondary = Secondary,
    onSecondary = BgCard,
    secondaryContainer = PinkLight,
    onSecondaryContainer = TextPrimary,
    tertiary = Accent,
    onTertiary = BgCard,
    tertiaryContainer = MintLight,
    onTertiaryContainer = TextPrimary,
    background = BgMain,
    onBackground = TextPrimary,
    surface = BgCard,
    onSurface = TextPrimary,
    surfaceVariant = BgMain,
    onSurfaceVariant = TextSecondary,
    outline = BorderDark,
    error = ErrorRed,
    onError = BgCard,
    errorContainer = PinkLight,
    onErrorContainer = TextPrimary
)

@Composable
fun LiliPlayerTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BgMain.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = LiliColorScheme,
        typography = LiliTypography,
        content = content
    )
}
