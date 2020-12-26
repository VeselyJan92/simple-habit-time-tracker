package com.imfibit.activitytracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import java.time.LocalDate


@DatabaseView(
    value = """SELECT
            tracked_activity_id,
            date_completed as date,
            1 as metric
        FROM tracked_activity_completion
        UNION ALL
        SELECT
            tracked_activity_id,
            date(datetime_completed) as date,
            score as metric
        FROM tracked_activity_score
        UNION ALL
        SELECT
            tracked_activity_id,
            date(datetime_start) as date,
            strftime('%s',datetime_end) - strftime('%s', datetime_start) as metric
        FROM tracked_activity_session
        """,
    viewName = "tracked_activity_metric"
)
data class TrackedActivityMetric(
    @ColumnInfo(name = "tracked_activity_id")
    val activityId: Long,
    val date: LocalDate,
    val metric: Long
)