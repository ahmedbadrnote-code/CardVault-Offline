package com.secure.cardvault.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PitchBlack = Color(0xFF000000)
val DeepSurface = Color(0xFF0A0A0A)
val CardGray = Color(0xFF141414)
val BorderGray = Color(0xFF333333)
val CrispWhite = Color(0xFFFFFFFF)
val MutedWhite = Color(0xFF9E9E9E)

private val MonochromeColorScheme = darkColorScheme(
    primary = CrispWhite,
    onPrimary = PitchBlack,
    secondary = CrispWhite,
    onSecondary = PitchBlack,
    background = PitchBlack,
    onBackground = CrispWhite,
    surface = DeepSurface,
    onSurface = CrispWhite,
    surfaceVariant = CardGray,
    onSurfaceVariant = MutedWhite,
    outline = BorderGray
)

@Composable
fun CardVaultTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MonochromeColorScheme,
        content = content
    )
}
