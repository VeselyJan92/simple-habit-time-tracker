package com.imfibit.activitytracker.database.dao.tracked_activity

import androidx.room.*
import com.imfibit.activitytracker.database.dao.BaseEditableDAO
import com.imfibit.activitytracker.database.entities.PresetTimer

/////////////////////////////////
// ----- DAOPresetTimers ----- ///
/////////////////////////////////
@Dao
abstract class DAOPresetTimers : BaseEditableDAO<PresetTimer> {


    @Query("""
        SELECT * FROM preset_timer
        WHERE tracked_activity_id = :activityId
        order by position
   """)
    abstract fun getAll(activityId: Long): List<PresetTimer>


}
