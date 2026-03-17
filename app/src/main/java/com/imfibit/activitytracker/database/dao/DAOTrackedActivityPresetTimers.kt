package com.imfibit.activitytracker.database.dao

import androidx.room.*
import com.imfibit.activitytracker.database.entities.PresetTimer

/////////////////////////////////
// ----- DAOPresetTimers ----- ///
/////////////////////////////////
@Dao
abstract class DAOTrackedActivityPresetTimers : BaseEditableDAO<PresetTimer> {


    @Query("""
        SELECT * FROM preset_timer
        WHERE tracked_activity_id = :activityId
        order by position
   """)
    abstract fun getAll(activityId: Long): List<PresetTimer>


}
