package com.imfibit.activitytracker.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.imfibit.activitytracker.database.entities.DailyChecklistItem

@Dao
abstract class DAODailyChecklistItem : BaseEditableDAO<DailyChecklistItem> {

    @Query("""
        SELECT * FROM daily_checklist_items order by position
   """)
    abstract fun getAll(): List<DailyChecklistItem>

}
