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

}
