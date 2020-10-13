package com.janvesely.activitytracker.ui.components

import androidx.compose.ui.graphics.Color
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.embedable.TrackedActivityGoal

object Colors{
   val Completed = Color(0xFF59BF2D)
   val NotCompleted = Color(0xFFFF9800)

   val ChipGray = Color(0xFFE0E0E0)
   val ChipGraySelected = Color(0xFFBDBDBD)

   val ButtonGreen = Color(0xFF41C300)
   val AppBackground = Color(0xFFe4eaee)

   val AppAccent = Color(0xFF4DB6AC)
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
}
