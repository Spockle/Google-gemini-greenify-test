package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldPrimary,
  onPrimary = EmeraldOnPrimary,
  primaryContainer = EmeraldPrimaryContainer,
  onPrimaryContainer = EmeraldOnPrimaryContainer,
  secondary = MintSecondary,
  onSecondary = MintOnSecondary,
  secondaryContainer = MintSecondaryContainer,
  onSecondaryContainer = MintOnSecondaryContainer,
  tertiary = TealTertiary,
  onTertiary = TealOnTertiary,
  tertiaryContainer = TealTertiaryContainer,
  onTertiaryContainer = TealOnTertiaryContainer,
  background = DarkBackground,
  onBackground = DarkOnBackground,
  surface = DarkSurface,
  onSurface = DarkOnSurface,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = DarkOnSurfaceVariant,
  surfaceContainer = DarkSurfaceContainer,
  surfaceContainerHigh = DarkSurfaceContainerHigh,
  outline = DarkOutline,
  outlineVariant = DarkOutlineVariant
)

private val LightColorScheme = lightColorScheme(
  primary = EmeraldPrimaryContainer,
  onPrimary = EmeraldPrimary,
  primaryContainer = EmeraldOnPrimaryContainer,
  onPrimaryContainer = EmeraldOnPrimary,
  secondary = MintSecondary,
  onSecondary = MintOnSecondary,
  secondaryContainer = MintSecondaryContainer,
  onSecondaryContainer = MintOnSecondaryContainer,
  tertiary = TealTertiary,
  onTertiary = TealOnTertiary,
  background = LightBackground,
  onBackground = LightOnBackground,
  surface = LightSurface,
  onSurface = LightOnSurface,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightOnSurfaceVariant,
  outline = LightOutline
)

@Composable
fun GreenifyTheme(
  darkTheme: Boolean = true, // Default to sleek eco-dark aesthetic iconic to Greenify
  dynamicColor: Boolean = false, // Keep branded emerald signature
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

// Alias for template compatibility
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  GreenifyTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
