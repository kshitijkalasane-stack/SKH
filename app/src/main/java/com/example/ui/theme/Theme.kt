package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
      primary = FarmDarkPrimary,
      primaryContainer = FarmDarkPrimaryContainer,
      secondary = FarmSecondary,
      tertiary = FarmTertiary,
      background = FarmDarkBackground,
      surface = FarmDarkSurface,
      onPrimary = Color.Black,
      onSurface = FarmDarkOnSurface
  )

private val LightColorScheme =
  lightColorScheme(
      primary = FarmPrimary,
      primaryContainer = FarmPrimaryContainer,
      secondary = FarmSecondary,
      tertiary = FarmTertiary,
      background = FarmBackground,
      surface = FarmSurface,
      onPrimary = FarmOnPrimary,
      onPrimaryContainer = FarmOnPrimaryContainer,
      onSurface = FarmOnSurface
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set dynamicColor to false by default to keep the farmer-friendly green theme active
  dynamicColor: Boolean = false,
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
