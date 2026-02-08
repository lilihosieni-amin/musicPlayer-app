package com.example.liliplayer.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun isCompactScreen(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp < 320
}
