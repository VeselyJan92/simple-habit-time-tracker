package com.janvesely.activitytracker.database.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import java.time.LocalDate
import java.time.LocalDateTime


@DatabaseView("""
        SELECT
            tracked_activity_id,
            date_completed as date,
            1 as metric
        FROM tracked_activity_completion
        UNION ALL
        SELECT 
            tracked_activity_id,
            date(time_completed) as date,
            score as metric
        FROM tracked_activity_score
        UNION ALL
        SELECT 
            tracked_activity_id,
            date(time_end) as date,
            strftime('%s',time_end) - strftime('%s', time_start) as metric
        FROM tracked_activity_session
""", viewName = "tracked_activity_metric")
data class TrackedActivityMetric(
    @ColumnInfo(name = "tracked_activity_id")
    val activityId: Long,
    val date: LocalDate,
    val metric: Long
)