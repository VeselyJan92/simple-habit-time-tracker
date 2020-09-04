package com.janvesely.activitytracker.database.composed

import androidx.recyclerview.widget.DiffUtil
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.TrackedActivity
import java.time.LocalDateTime


data class ViewRangeData(
    val type: TimeRange,
    val from: LocalDateTime,
    val to: LocalDateTime
) {
    var goal = 0
    var metric: Int = 0

    lateinit var format: TrackedActivity.Type


    fun getLabel() = type.getLabel(from)

    fun formatMetric() = format.format(metric)

    fun formatGoal() = format.format(goal)

    fun progress() = if (goal == 0 || format == TrackedActivity.Type.COMPLETED) formatMetric() else "${formatMetric()} / ${formatGoal()}"

    fun isCompleted() = metric >= goal

}

data class TrackedActivityWithMetric(
    var activity: TrackedActivity,
    val past: List<ViewRangeData>
) {
    var completed = past[0].metric >= activity.expected

    init {
        past.forEach {
            it.format = activity.type
            it.goal = activity.expected
        }
    }

}


object TrackedActivityWithMetricDiff : DiffUtil.ItemCallback<TrackedActivityWithMetric>() {
    override fun areItemsTheSame(
        old: TrackedActivityWithMetric,
        new: TrackedActivityWithMetric
    ): Boolean {
        return old.activity.id == new.activity.id
    }

    override fun areContentsTheSame(
        old: TrackedActivityWithMetric,
        new: TrackedActivityWithMetric
    ): Boolean {
        return old == new && new.activity.in_session_since != null
    }
}