package com.janvesely.activitytracker.database.entities

import androidx.room.DatabaseView
import java.time.LocalDateTime

@DatabaseView("""
        SELECT 
            date_completed as time,
            1 as metric
        FROM tracked_activity_completion
        UNION ALL
        SELECT 
            time_completed as time,
            score as metric
        FROM tracked_activity_score
        UNION ALL
        SELECT 
            time_end as time,
            strftime('%s',time_end) - strftime('%s', time_start) as metric
        FROM tracked_activity_session""")
data class TrackedActivityMetric(
    val time: LocalDateTime,
    val metric: Long
)