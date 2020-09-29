package com.janvesely.activitytracker.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativeTileMode
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.TrackedActivity

object Colors{
   val Completed = Color(0xFF59BF2D)
   val NotCompleted = Color(0xFFFF9800)

   val ChipGray = Color(0xFFE0E0E0)
   val ChipGraySelected = Color(0xFFBDBDBD)

   val ButtonGreen = Color(0xFF41C300)
   val AppBackground = Color(0xFFe4eaee)

   val AppAccent = Color(0xFF4DB6AC)
   val AppPrimary = Color(0xFF6200EE)


   fun getMetricColor(type: TrackedActivity.Type, activityGoal: Long, activityGoalRange: TimeRange, metric: Long, metricRange: TimeRange): Color {
      return if ((activityGoalRange == metricRange || type == TrackedActivity.Type.COMPLETED) && activityGoal != 0L)
         if (activityGoal <= metric)
            Completed
         else
            NotCompleted
      else
         if (metric != 0L)
            Completed
         else
            ChipGray
   }
}
