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

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryGreenDark,
    onPrimary = OnPrimaryGreenDark,
    primaryContainer = PrimaryGreenContainerDark,
    onPrimaryContainer = OnPrimaryGreenContainerDark,
    secondary = SecondaryHarvestDark,
    onSecondary = OnSecondaryHarvestDark,
    secondaryContainer = SecondaryHarvestContainerDark,
    onSecondaryContainer = OnSecondaryHarvestContainerDark,
    tertiary = TertiaryTerracottaDark,
    onTertiary = OnTertiaryTerracottaDark,
    tertiaryContainer = TertiaryTerracottaContainerDark,
    onTertiaryContainer = OnTertiaryTerracottaContainerDark,
    background = FarmBackgroundDark,
    surface = FarmSurfaceDark,
    surfaceVariant = FarmSurfaceVariantDark,
    onSurface = FarmOnSurfaceDark,
    outline = FarmOutlineDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryGreen,
    primaryContainer = PrimaryGreenContainer,
    onPrimaryContainer = OnPrimaryGreenContainer,
    secondary = SecondaryHarvest,
    onSecondary = OnSecondaryHarvest,
    secondaryContainer = SecondaryHarvestContainer,
    onSecondaryContainer = OnSecondaryHarvestContainer,
    tertiary = TertiaryTerracotta,
    onTertiary = OnTertiaryTerracotta,
    tertiaryContainer = TertiaryTerracottaContainer,
    onTertiaryContainer = OnTertiaryTerracottaContainer,
    background = FarmBackgroundLight,
    surface = FarmSurfaceLight,
    surfaceVariant = FarmSurfaceVariantLight,
    onSurface = FarmOnSurfaceLight,
    outline = FarmOutlineLight,
  )

@Composable
fun FarmAITheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep distinctive agricultural branding by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

// Keep backwards-compatible alias
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) = FarmAITheme(darkTheme, dynamicColor, content)
