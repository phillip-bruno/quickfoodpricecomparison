package com.wolfcola.quickfoodpricecomparison.quickfoodpricecomparison.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    val inputBackground: Color,
    val foodInfo: Color,
    val emptyState: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        inputBackground = InputBackgroundLight,
        foodInfo = FoodInfoColorLight,
        emptyState = EmptyStateColorLight,
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Primary,
    secondary = Accent,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4DB6AC),
    onPrimary = Color.Black,
    primaryContainer = PrimaryDark,
    secondary = Color(0xFFFF80AB),
    onSecondary = Color.Black,
)

private val LightExtendedColors = ExtendedColors(
    inputBackground = InputBackgroundLight,
    foodInfo = FoodInfoColorLight,
    emptyState = EmptyStateColorLight,
)

private val DarkExtendedColors = ExtendedColors(
    inputBackground = InputBackgroundDark,
    foodInfo = FoodInfoColorDark,
    emptyState = EmptyStateColorDark,
)

@Composable
fun QuickFoodPriceComparisonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
