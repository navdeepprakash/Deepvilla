package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MintAccent,
    secondary = SunRayYellow,
    tertiary = WarmTerracotta,
    background = ForestDarkBg,
    surface = ForestCardDark,
    onPrimary = Color(0xFF071F0A),
    onSecondary = Color(0xFF3E2700),
    onBackground = TextLightGrey,
    onSurface = TextLightGrey,
    surfaceVariant = Color(0xFF2C3E30)
)

private val LightColorScheme = lightColorScheme(
    primary = PureOrganicGreen,
    secondary = LeafSecondary,
    tertiary = WarmTerracotta,
    background = SoilClayWhiteBg,
    surface = CardLightWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextDarkGrey,
    onSurface = TextDarkGrey,
    surfaceVariant = Color(0xFFEDF3ED)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to eye-protecting dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
