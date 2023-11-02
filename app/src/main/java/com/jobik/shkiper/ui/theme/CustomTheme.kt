package com.jobik.shkiper.ui.theme

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

data class CustomThemeColors(
    val active: Color,
    val mainBackground: Color,
    val secondaryBackground: Color,
    val text: Color,
    val textOnActive: Color,
    val textSecondary: Color,
    val stroke: Color,
    val secondaryStroke: Color,
    val modalBackground: Color,
)

data class CustomThemeShapes(
    val none: Shape,
    val small: Shape,
    val medium: Shape,
    val large: Shape,
)

object CustomTheme {
    val colors: CustomThemeColors
        @Composable
        get() = LocalCustomThemeColors.current
    val shapes: CustomThemeShapes
        @Composable
        get() = LocalCustomThemeShapes.current
}

@Keep
enum class CustomThemeStyle(val dark: CustomThemeColors, val light: CustomThemeColors) {
    DarkPurple(dark = DarkDeepPurple, light = LightDeepPurple),
    DarkRed(dark = DarkDeepRed, light = LightDeepRed),
    DarkOrange(dark = DarkDeepOrange, light = LightDeepOrange),
    DarkBlue(dark = DarkDeepBlue, light = LightDeepBlue),
    DarkGreen(dark = DarkDeepGreen, light = LightDeepGreen),

    LightPurple(dark = DarkPastelPurple, light = LightPastelPurple),
    LightRed(dark = DarkPastelRed, light = LightPastelRed),
    LightOrange(dark = DarkPastelOrange, light = LightPastelOrange),
    LightBlue(dark = DarkPastelBlue, light = LightPastelBlue),
    LightGreen(dark = DarkPastelGreen, light = LightPastelGreen);
    
    fun getColors(isDarkTheme: Boolean): CustomThemeColors {
        return if (isDarkTheme) dark else light
    }
}

val LocalCustomThemeColors = staticCompositionLocalOf<CustomThemeColors> {
    error("No colors provided")
}

val LocalCustomThemeShapes = staticCompositionLocalOf<CustomThemeShapes> {
    error("No shapes provided")
}