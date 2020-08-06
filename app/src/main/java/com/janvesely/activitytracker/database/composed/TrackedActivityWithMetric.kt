package com.janvesely.activitytracker.database.composed

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.janvesely.activitytracker.database.embedable.TimeRange
import com.janvesely.activitytracker.database.entities.TrackedActivity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class ViewRangeData(
    val type: TimeRange,
    val from: LocalDateTime,
    val to: LocalDateTime
) {
    var metric: Int = 0

    fun getLabel(context: Context) = type.getLabel(context, from)

}

data class TrackedActivityWithMetric(
    var activity: TrackedActivity,
    val past: List<ViewRangeData>
) {
    var completed = past[0].metric >= activity.expected


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