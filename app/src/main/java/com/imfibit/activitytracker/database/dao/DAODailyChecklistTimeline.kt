package com.imfibit.activitytracker.database.dao

import androidx.room.*
import com.imfibit.activitytracker.database.entities.DailyChecklistTimelineItem
import java.time.LocalDate

@Dao
abstract class DAODailyChecklistTimeline : BaseEditableDAO<DailyChecklistTimelineItem> {

    @Query("""
        SELECT * FROM daily_checklist_timeline
   """)
    abstract fun getAll(): List<DailyChecklistTimelineItem>

    @Query("""
        SELECT * FROM daily_checklist_timeline WHERE :from <= date_completed  AND date_completed <= :to 
   """)
    abstract suspend fun getFromTo(from: LocalDate, to: LocalDate): List<DailyChecklistTimelineItem>

    @Query("""
        WITH RECURSIVE 
            cte AS (
                SELECT date_completed, 1 AS streak 
                FROM daily_checklist_timeline 
                WHERE date_completed = date('now')
                
                UNION ALL
                
                SELECT d.date_completed, c.streak + 1
                FROM cte AS c
                JOIN daily_checklist_timeline AS d ON d.date_completed = date(c.date_completed, '-1 day')
            )
        SELECT MAX(streak) 
        FROM cte;
    """)
    abstract suspend fun getStrike(): Int

}
