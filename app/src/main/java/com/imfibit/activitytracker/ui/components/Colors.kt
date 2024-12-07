package com.imfibit.activitytracker.ui.components

import androidx.compose.ui.graphics.Color
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal

object Colors{
   val Completed = Color(0xFF59BF2D)
   val NotCompleted = Color(0xFFFF9800)

   val ChipGray = Color(0xFFE0E0E0)
   val SuperLight = Color(0xFFF3F3F3)
   val ChipGraySelected = Color(0xFFBDBDBD)

   val ButtonGreen = Color(0xFF41C300)
   val AppBackground = Color(0xFFe4eaee)
   val BackgroundGray = Color(0xFFFAFAFA)

   val AppAccent = Color(0xFF4DB6AC)
   val ButtonX = Color(0xFFB0BEC5)
   val AppPrimary = Color(0xFF4c37ef)

   fun getMetricColor(goal: TrackedActivityGoal, metric: Long, metricRange: TimeRange, default: Color): Color {
      return if ((goal.range == metricRange) && goal.isSet())
         if (goal.value <= metric)
            Completed
         else
            NotCompleted
      else
         if (metric != 0L)
            Completed
         else
            default
   }


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
