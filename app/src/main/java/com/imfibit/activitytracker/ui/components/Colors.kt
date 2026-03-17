package com.imfibit.activitytracker.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

object Colors{

   val chooseableColors = listOf(
      Color(0xFFFFCDD2),
      Color(0xFFF8BBD0),
      Color(0xFFE1BEE7),

      Color(0xFFD1C4E9),
      Color(0xFFC5CAE9),
      Color(0xFFBBDEFB),

      Color(0xFFB3E5FC),
      Color(0xFFB2EBF2),
      Color(0xFFB2DFDB),

      Color(0xFFC8E6C9),
      Color(0xFFDCEDC8),
      Color(0xFFF0F4C3),

      Color(0xFFFFF9C4),
      Color(0xFFFFECB3),
      Color(0xFFFFE0B2),

      Color(0xFFFFCCBC),
      Color(0xFFD7CCC8),
      Color(0xFFF5F5F5),

      Color(0xFFCFD8DC),
   )
}

fun Color.darker(factor: Float) = copy(
   red = red - factor,
   green = green - factor,
   blue = blue - factor
)

@Composable
fun Color.harmonizeWithTheme(isDarkTheme: Boolean = isSystemInDarkTheme()): Color {
   if (!isDarkTheme) return this
   
   val hsl = FloatArray(3)
   ColorUtils.colorToHSL(this.toArgb(), hsl)
   
   // Reduce lightness and adjust saturation for dark mode
   hsl[2] = (hsl[2] * 0.6f).coerceIn(0.2f, 0.4f) // Making it darker
   hsl[1] = (hsl[1] * 0.8f).coerceAtMost(0.6f)   // Desaturating slightly
   
   return Color(ColorUtils.HSLToColor(hsl))
}
