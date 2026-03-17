package com.imfibit.activitytracker.database.embedable

import androidx.room.ColumnInfo
import com.imfibit.activitytracker.database.entities.TrackedActivity
import com.imfibit.activitytracker.core.enums.MetricStatus


data class TrackedActivityGoal(
    /**Number seconds for [TrackedActivity.Type] TIMED or count */
    @ColumnInfo(name = "goal_value")
    val value: Long,

    @ColumnInfo(name = "goal_range")
    val range: TimeRange
){
    fun isSet() = value != 0L

    fun status(metric: Long) = when {
        range == TimeRange.WEEKLY -> {
            if (value != 0L) {
                if (value <= metric)
                    MetricStatus.COMPLETED
                else
                    MetricStatus.NOT_COMPLETED
            } else  {
                MetricStatus.ACCENT
            }
        }
        else -> {
            MetricStatus.ACCENT
        }
    }

    fun metricPerDay() = when(range){
        TimeRange.DAILY -> value.toFloat()
        TimeRange.WEEKLY -> value.toFloat() / 7f
        TimeRange.MONTHLY -> value.toFloat() / 30f
    }
}
