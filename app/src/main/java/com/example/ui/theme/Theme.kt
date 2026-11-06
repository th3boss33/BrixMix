package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = LegoYellow,
  onPrimary = Color(0xFF211B00),
  primaryContainer = LegoRed,
  onPrimaryContainer = Color.White,
  secondary = LegoBlue,
  onSecondary = Color.White,
  secondaryContainer = LegoBlueLight.copy(alpha = 0.2f),
  onSecondaryContainer = Color.White,
  tertiary = LegoOrange,
  onTertiary = Color.White,
  background = WorkshopBackground,
  onBackground = TextPrimary,
  surface = WorkshopSurface,
  onSurface = TextPrimary,
  surfaceVariant = WorkshopSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = WorkshopCardBorder,
  outlineVariant = Color(0xFF3F485B)
)

private val LightColorScheme = lightColorScheme(
  primary = LegoRed,
  onPrimary = Color.White,
  primaryContainer = LegoYellow,
  onPrimaryContainer = Color(0xFF2A2000),
  secondary = LegoBlue,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFE1F0FF),
  onSecondaryContainer = LegoBlue,
  tertiary = LegoGreen,
  onTertiary = Color.White,
  background = Color(0xFFF7F8FA),
  onBackground = Color(0xFF14171D),
  surface = Color.White,
  onSurface = Color(0xFF14171D),
  surfaceVariant = Color(0xFFEDF0F5),
  onSurfaceVariant = Color(0xFF4A5568),
  outline = Color(0xFFD0D7E2)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek workshop dark theme
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
