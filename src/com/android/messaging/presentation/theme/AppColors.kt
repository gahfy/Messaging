package com.android.messaging.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val background: Color,
    val revertBackground: Color,
    val highlightedBackground: Color,
    val onBackground: Color,
    val onRevertBackground: Color,
    val onRevertBackgroundAction: Color,
    val onBackgroundSecondary: Color,
    val appBarColor: Color,
    val appBarForeground: Color,
    val statusBackground: Color,
    val statusForeground: Color,
    val fabBackground: Color,
    val fabForeground: Color,
    val error: Color,
    val contact1: Color,
    val contact2: Color,
    val contact3: Color,
    val contact4: Color,
    val contact5: Color,
    val contact6: Color,
    val contact7: Color
)

val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        background = Color.Transparent,
        revertBackground = Color.Transparent,
        highlightedBackground = Color.Transparent,
        onBackground = Color.Transparent,
        onRevertBackground = Color.Transparent,
        onRevertBackgroundAction = Color.Transparent,
        onBackgroundSecondary = Color.Transparent,
        appBarColor = Color.Transparent,
        appBarForeground = Color.Transparent,
        statusBackground = Color.Transparent,
        statusForeground = Color.Transparent,
        fabBackground = Color.Transparent,
        fabForeground = Color.Transparent,
        error = Color.Transparent,
        contact1 = Color.Transparent,
        contact2 = Color.Transparent,
        contact3 = Color.Transparent,
        contact4 = Color.Transparent,
        contact5 = Color.Transparent,
        contact6 = Color.Transparent,
        contact7 = Color.Transparent,
    )
}
