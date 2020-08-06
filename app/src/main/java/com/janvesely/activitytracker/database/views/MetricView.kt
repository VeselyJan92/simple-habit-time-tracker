package com.janvesely.activitytracker.database.views

import androidx.room.DatabaseView



@DatabaseView("""
         
            SELECT tracked_activity_id, sum(metric) as metric FROM (
                SELECT
                    s.tracked_activity_id,
                    1 as metric,
                    s.date_completed || ' 00:00:00' as time
                    
                FROM tracked_activity_completion s
                
                UNION
                
                SELECT
                    s.tracked_activity_id,
                    s.score as metric,
                    s.time_completed as time 
                FROM tracked_activity_score s
    
                UNION
    
                SELECT
                    s.tracked_activity_id,
                    strftime('%s',s.time_end) - strftime('%s', s.time_start) as metric,
                    s.time_start as time
                FROM tracked_activity_session s
            ) x
            LEFT JOIN tracked_activity USING(tracked_activity_id)
    
    
            WHERE
                date(time) >= CASE goal_frequency
                    WHEN 'DAILY' THEN date('now')
                    WHEN 'WEEKLY' THEN date('now', '-6 days')
                    WHEN 'MONTHLY' THEN date('now','start of month')
                    WHEN 'YEARLY' THEN date('now','start of year')
                    ELSE 'ERROR'
                END
            GROUP BY tracked_activity_id
    
        """)
data class MetricView(
    val id: Long,
    val name: String?,
    val departmentId: Long,
    val departmentName: String?
)