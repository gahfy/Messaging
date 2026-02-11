package com.android.messaging.presentation.utils

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * This Composable allow to adapt status bar icons to the color scheme
 */
@Composable
fun StatusBarColorScheme() {
    val view = LocalView.current
    val isDark = isSystemInDarkTheme()

    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view)
            .isAppearanceLightStatusBars = !isDark
    }
}
