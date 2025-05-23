package com.imfibit.activitytracker.database.embedable

import androidx.room.ColumnInfo
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.ui.components.Colors


data class TrackedActivityGoal(
    /**Number seconds for [TrackedActivity.Type] TIMED or count */
    @ColumnInfo(name = "goal_value")
    val value: Long,

    @ColumnInfo(name = "goal_range")
    val range: TimeRange
){
    fun isSet() = value != 0L

    fun color(metric: Long) = when {
        range == TimeRange.WEEKLY -> {
            if (value != 0L) {
                if (value <= metric)
                    Colors.Completed
                else
                    Colors.NotCompleted
            } else  {
                Colors.AppAccent
            }
        }
        else -> {
            Colors.AppAccent
        }
    }

    fun metricPerDay() = when(range){
        TimeRange.DAILY -> value.toFloat()
        TimeRange.WEEKLY -> value.toFloat() / 7f
        TimeRange.MONTHLY -> value.toFloat() / 30f
    }
}