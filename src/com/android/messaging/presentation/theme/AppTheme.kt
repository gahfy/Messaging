package com.android.messaging.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val BLACK = 0xFF0E0E12
private const val WHITE = 0xFFFCF8FE
private const val LIGHTER_BLACK = 0xFF1A191F

private const val GRAY_900 = 0xFFE8E4F0
private const val GRAY_600 = 0xFF95929D
private const val GRAY_400 = 0xFF605E68
private const val GRAY_200 = 0xFF32313A
private const val GRAY_060 = 0xFFF0ECF6
private const val GRAY_020 = 0xFFFCF8FE

private const val CORAL = 0xFFEE675C
private const val MAGENTA = 0xFFFF63B8
private const val ORANGE = 0xFFFA903E
private const val GREEN = 0xFF5BB974
private const val CYAN = 0xFF4ECDE6
private const val YELLOW = 0xFFFCC934
private const val PURPLE = 0xFFAF5CF7

private const val LIGHTER_PURPLE = 0xFFE4E0FF
private const val LIGHT_PURPLE = 0xFFC6C2EE
private const val MEDIUM_PURPLE = 0xFF5D5A8B
private const val DARK_PURPLE = 0xFF514E74
private const val DARKER_PURPLE = 0xFF3E3C60

private const val LIGHT_RED = 0xFFFF8983
private const val RED = 0xFFDB372D

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

data class Dimensions(
    val mCornerRadius: Dp = 8.dp,

    val xxsMargin: Dp = 2.dp,
    val sMargin: Dp = 8.dp,
    val mMargin: Dp = 16.dp,
    val xlMargin: Dp = 64.dp,

    val bodySmall: TextUnit = 14.sp,
    val body: TextUnit = 16.sp,

    val bigIconSize: Dp = 56.dp,
    val iconInTextSize: Dp = 14.dp
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dimensions = LocalDimensions.current

    val appColors = if (darkTheme) {
        AppColors(
            background = Color(BLACK),
            revertBackground = Color(WHITE),
            highlightedBackground = Color(LIGHTER_BLACK),
            onBackground = Color(GRAY_900),
            onRevertBackground = Color(GRAY_200),
            onRevertBackgroundAction = Color(MEDIUM_PURPLE),
            onBackgroundSecondary = Color(GRAY_600),
            appBarColor = Color(LIGHTER_BLACK),
            appBarForeground = Color(GRAY_900),
            statusBackground = Color(LIGHT_PURPLE),
            statusForeground = Color(DARKER_PURPLE),
            fabBackground = Color(DARK_PURPLE),
            fabForeground = Color(LIGHTER_PURPLE),
            error = Color(LIGHT_RED),
            contact1 = Color(CORAL),
            contact2 = Color(MAGENTA),
            contact3 = Color(ORANGE),
            contact4 = Color(GREEN),
            contact5 = Color(CYAN),
            contact6 = Color(YELLOW),
            contact7 = Color(PURPLE),
        )
    } else {
        AppColors(
            background = Color(GRAY_020),
            revertBackground = Color(BLACK),
            highlightedBackground = Color(GRAY_060),
            onBackground = Color(GRAY_200),
            onRevertBackground = Color(GRAY_900),
            onRevertBackgroundAction = Color(LIGHT_PURPLE),
            onBackgroundSecondary = Color(GRAY_400),
            appBarColor = Color(GRAY_060),
            appBarForeground = Color(GRAY_200),
            statusBackground = Color(MEDIUM_PURPLE),
            statusForeground = Color(GRAY_020),
            fabBackground = Color(LIGHT_PURPLE),
            fabForeground = Color(DARK_PURPLE),
            error = Color(RED),
            contact1 = Color(CORAL),
            contact2 = Color(MAGENTA),
            contact3 = Color(ORANGE),
            contact4 = Color(GREEN),
            contact5 = Color(CYAN),
            contact6 = Color(YELLOW),
            contact7 = Color(PURPLE),
        )
    }

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalDimensions provides dimensions
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            content = content
        )
    }
}
