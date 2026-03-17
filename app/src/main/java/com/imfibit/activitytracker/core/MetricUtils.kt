package com.imfibit.activitytracker.core

import com.imfibit.activitytracker.core.enums.MetricStatus
import com.imfibit.activitytracker.database.embedable.TimeRange
import com.imfibit.activitytracker.database.embedable.TrackedActivityGoal

fun getMetricStatus(goal: TrackedActivityGoal, metric: Long, metricRange: TimeRange): MetricStatus {
    return if (goal.range == metricRange && goal.isSet()) {
        if (goal.value <= metric) {
            MetricStatus.COMPLETED
        } else {
            MetricStatus.NOT_COMPLETED
        }
    } else {
        if (metric != 0L) {
            MetricStatus.COMPLETED
        } else {
            MetricStatus.DEFAULT
        }
    }
}
